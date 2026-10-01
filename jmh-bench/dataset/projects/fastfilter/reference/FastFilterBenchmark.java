package bench.generated;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Supplier;
import java.util.concurrent.TimeUnit;

import org.fastfilter.Filter;
import org.fastfilter.FilterType;
import org.fastfilter.gcs.BitBuffer;
import org.fastfilter.gcs.MonotoneList;
import org.fastfilter.gcs.Sort;
import org.fastfilter.utils.Hash;
import org.fastfilter.utils.StringUtils;
import org.fastfilter.xorplus.Rank9;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Gold reference JMH suite for the FastFilter mutation track.
 *
 * Covers a broad cross-section of the library: construction of every filter
 * family through {@link FilterType}, incremental insertion into the mutable
 * families, probing, the Golomb/Elias bit-buffer codec, the succinct
 * {@link MonotoneList} and {@link Rank9} structures, the unsigned radix sort and
 * the hashing helpers.
 *
 * Everything is in-memory and CPU-bound: the key set is built once per trial
 * with a fixed seed, so run-to-run variance comes from the JVM rather than the
 * data. Construction benchmarks deliberately build inside the measured method —
 * that is the operation under test — while probe benchmarks build in
 * {@link Setup} and measure only lookups.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class FastFilterBenchmark {

    /** Key-set size: big enough to be representative, small enough to build fast. */
    private static final int KEY_COUNT = 20_000;
    /** Bits per key for the size-parameterised (Bloom / GCS) families. */
    private static final int BITS_PER_KEY = 10;
    /** Cuckoo table load factor — the same one the library's `construct` uses. */
    private static final double CUCKOO_LOAD = 0.94;

    private long[] keys;
    private long[] absentKeys;
    private int[] monotone;
    private BitSet bits;

    /** One representative of each family, prebuilt for the probe benchmarks. */
    private Filter bloom;
    private Filter blockedBloom;
    private Filter xor8;
    private Filter xorPlus8;
    private Filter cuckoo8;
    private Filter gcs;
    private Rank9 rank9;

    @Setup(Level.Trial)
    public void setUp() {
        final Random random = new Random(42);
        Hash.setSeed(0x9E3779B97F4A7C15L);

        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }
        absentKeys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            absentKeys[i] = random.nextLong();
        }

        monotone = new int[4096];
        int running = 0;
        for (int i = 0; i < monotone.length; i++) {
            running += 1 + random.nextInt(16);
            monotone[i] = running;
        }

        bits = new BitSet();
        for (int i = 0; i < 1 << 16; i++) {
            if (random.nextBoolean()) {
                bits.set(i);
            }
        }
        rank9 = new Rank9(bits, 1 << 16);

        bloom = FilterType.BLOOM.construct(keys, BITS_PER_KEY);
        blockedBloom = FilterType.BLOCKED_BLOOM.construct(keys, BITS_PER_KEY);
        xor8 = FilterType.XOR_8.construct(keys, 0);
        xorPlus8 = FilterType.XOR_PLUS_8.construct(keys, 0);
        cuckoo8 = FilterType.CUCKOO_8.construct(keys, 0);
        gcs = FilterType.GCS.construct(keys, BITS_PER_KEY);
    }

    // ---- construction, one benchmark per family ---------------------------

    /**
     * Construction of every implementation the library ships, driven through the
     * {@link FilterType} enum so a single method covers the whole catalogue.
     *
     * <p>{@code XOR_SIMPLE} and {@code XOR_SIMPLE_2} pick a fresh random seed per
     * attempt and throw {@link ArrayIndexOutOfBoundsException} on roughly one
     * construction in twenty; retrying is what the caller is expected to do, and
     * without it a long JMH run would abort partway through.</p>
     */
    @Benchmark
    public void constructAllTypes(final Blackhole bh) {
        for (final FilterType type : FilterType.values()) {
            bh.consume(constructRetrying(type));
        }
    }

    @Benchmark
    public Filter constructBloom() {
        return FilterType.BLOOM.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public Filter constructBlockedBloom() {
        return FilterType.BLOCKED_BLOOM.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public Filter constructCountingBloom() {
        return FilterType.COUNTING_BLOOM.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public void constructSuccinctCountingFamily(final Blackhole bh) {
        bh.consume(FilterType.SUCCINCT_COUNTING_BLOOM.construct(keys, BITS_PER_KEY));
        bh.consume(FilterType.SUCCINCT_COUNTING_BLOOM_RANKED.construct(keys, BITS_PER_KEY));
        bh.consume(FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM.construct(keys, BITS_PER_KEY));
        bh.consume(FilterType.SUCCINCT_COUNTING_BLOCKED_BLOOM_RANKED.construct(keys, BITS_PER_KEY));
    }

    @Benchmark
    public void constructXorFamily(final Blackhole bh) {
        bh.consume(constructRetrying(FilterType.XOR_SIMPLE));
        bh.consume(constructRetrying(FilterType.XOR_SIMPLE_2));
        bh.consume(FilterType.XOR_8.construct(keys, 0));
        bh.consume(FilterType.XOR_16.construct(keys, 0));
        bh.consume(FilterType.XOR_PLUS_8.construct(keys, 0));
    }

    /** Builds a filter, retrying the seed-dependent peeling failures. */
    private Filter constructRetrying(final FilterType type) {
        for (int attempt = 0; attempt < 8; attempt++) {
            try {
                return type.construct(keys, BITS_PER_KEY);
            } catch (final ArrayIndexOutOfBoundsException retryable) {
                // Unlucky seed: the peeling pass overflowed its stack. Try again.
            }
        }
        throw new IllegalStateException("could not construct " + type);
    }

    @Benchmark
    public Filter constructXorFuse8() {
        return org.fastfilter.xor.XorFuse8.construct(keys);
    }

    @Benchmark
    public void constructCuckooFamily(final Blackhole bh) {
        bh.consume(FilterType.CUCKOO_8.construct(keys, 0));
        bh.consume(FilterType.CUCKOO_16.construct(keys, 0));
        bh.consume(FilterType.CUCKOO_PLUS_8.construct(keys, 0));
        bh.consume(FilterType.CUCKOO_PLUS_16.construct(keys, 0));
    }

    @Benchmark
    public Filter constructGolombCompressedSet() {
        return FilterType.GCS.construct(keys, BITS_PER_KEY);
    }

    /**
     * Incremental insertion, the one path the static factories never take.
     *
     * <p>A cuckoo table seeds itself randomly and can run out of kicks before the
     * last key lands, throwing {@code IllegalStateException("Table full")}. The
     * library's own {@code construct} answers that by rebuilding from scratch, so
     * these benchmarks do the same — one build attempt is not a well-defined
     * operation to measure.</p>
     */
    @Benchmark
    public Filter insertIntoCuckoo8() {
        return insertRetrying(() -> {
            final org.fastfilter.cuckoo.Cuckoo8 filter =
                    new org.fastfilter.cuckoo.Cuckoo8((int) (KEY_COUNT / CUCKOO_LOAD));
            for (final long key : keys) {
                filter.insert(key);
            }
            return filter;
        });
    }

    @Benchmark
    public Filter insertIntoCuckoo16() {
        return insertRetrying(() -> {
            final org.fastfilter.cuckoo.Cuckoo16 filter =
                    new org.fastfilter.cuckoo.Cuckoo16((int) (KEY_COUNT / CUCKOO_LOAD));
            for (final long key : keys) {
                filter.insert(key);
            }
            return filter;
        });
    }

    @Benchmark
    public Filter insertIntoCuckooPlus8() {
        return insertRetrying(() -> {
            final org.fastfilter.cuckoo.CuckooPlus8 filter =
                    new org.fastfilter.cuckoo.CuckooPlus8((int) (KEY_COUNT / CUCKOO_LOAD));
            for (final long key : keys) {
                filter.insert(key);
            }
            return filter;
        });
    }

    @Benchmark
    public Filter insertIntoCuckooPlus16() {
        return insertRetrying(() -> {
            final org.fastfilter.cuckoo.CuckooPlus16 filter =
                    new org.fastfilter.cuckoo.CuckooPlus16((int) (KEY_COUNT / CUCKOO_LOAD));
            for (final long key : keys) {
                filter.insert(key);
            }
            return filter;
        });
    }

    /** Rebuilds the table when a random seed leaves it unable to place a key. */
    private Filter insertRetrying(final Supplier<Filter> build) {
        for (int attempt = 0; attempt < 16; attempt++) {
            try {
                return build.get();
            } catch (final IllegalStateException tableFull) {
                // Unlucky seed: the cuckoo chain ran out of kicks. Rebuild.
            }
        }
        throw new IllegalStateException("could not fill cuckoo table");
    }

    // ---- probing ----------------------------------------------------------

    @Benchmark
    public int probeBloom() {
        return countHits(bloom);
    }

    @Benchmark
    public int probeBlockedBloom() {
        return countHits(blockedBloom);
    }

    @Benchmark
    public int probeXor8() {
        return countHits(xor8);
    }

    @Benchmark
    public int probeXorPlus8() {
        return countHits(xorPlus8);
    }

    @Benchmark
    public int probeCuckoo8() {
        return countHits(cuckoo8);
    }

    @Benchmark
    public int probeGolombCompressedSet() {
        return countHits(gcs);
    }

    private int countHits(final Filter filter) {
        int hits = 0;
        for (final long key : absentKeys) {
            if (filter.mayContain(key)) {
                hits++;
            }
        }
        return hits;
    }

    // ---- bit-level codec and succinct structures --------------------------

    @Benchmark
    public BitBuffer golombRiceRoundTrip() {
        final BitBuffer buffer = new BitBuffer(64 * KEY_COUNT);
        for (int i = 0; i < 4096; i++) {
            buffer.writeGolombRice(4, i & 0xFF);
        }
        buffer.seek(0);
        for (int i = 0; i < 4096; i++) {
            buffer.skipGolombRice(4);
        }
        return buffer;
    }

    @Benchmark
    public BitBuffer golombRiceFastWrite() {
        final BitBuffer buffer = new BitBuffer(64 * KEY_COUNT);
        for (int i = 0; i < 4096; i++) {
            buffer.writeGolombRiceFast(4, i & 0xFF);
        }
        final int end = buffer.position();
        buffer.seek(0);
        buffer.skipGolombRice(0, 4);
        buffer.clear();
        return end == 0 ? null : buffer;
    }

    @Benchmark
    public long eliasDeltaRoundTrip() {
        final BitBuffer buffer = new BitBuffer(64 * 4096);
        long size = 0;
        for (int i = 1; i <= 4096; i++) {
            size += BitBuffer.getEliasDeltaSize(i);
            buffer.writeEliasDelta(BitBuffer.foldSigned(i));
        }
        buffer.seek(0);
        long sum = size;
        for (int i = 1; i <= 4096; i++) {
            sum += BitBuffer.unfoldSigned(buffer.readEliasDelta());
        }
        return sum;
    }

    @Benchmark
    public long numberCodecRoundTrip() {
        final BitBuffer buffer = new BitBuffer(64 * 4096);
        for (int i = 0; i < 4096; i++) {
            buffer.writeNumber(i, 13);
        }
        buffer.seek(0);
        long sum = buffer.readUntilZero(0);
        for (int i = 0; i < 4096; i++) {
            sum += buffer.readNumber(13);
        }
        return sum;
    }

    @Benchmark
    public long monotoneListBuildAndRead() {
        final BitBuffer buffer = new BitBuffer(8L * MonotoneList.getSize(monotone));
        final MonotoneList list = MonotoneList.generate(monotone, buffer);
        buffer.seek(0);
        final MonotoneList reloaded = MonotoneList.load(buffer);
        long sum = 0;
        for (int i = 0; i < monotone.length; i += 8) {
            sum += list.get(i) + reloaded.getPair(i);
        }
        return sum;
    }

    @Benchmark
    public long rank9Queries() {
        long sum = 0;
        for (int i = 0; i < 1 << 16; i += 64) {
            sum += rank9.rank(i) + rank9.getAndPartialRank(i) + rank9.remainingRank(i);
        }
        return sum;
    }

    @Benchmark
    public int selectInLong() {
        int sum = 0;
        for (int i = 1; i < 4096; i++) {
            sum += org.fastfilter.bloom.count.Select.selectInLong(i * 0x0101010101010101L, 3);
        }
        return sum;
    }

    // ---- sorting and hashing ---------------------------------------------

    @Benchmark
    public long[] sortUnsigned() {
        final long[] copy = keys.clone();
        Sort.sortUnsigned(copy);
        return copy;
    }

    @Benchmark
    public long[] sortUnsignedRange() {
        final long[] copy = keys.clone();
        Sort.sortUnsigned(copy, 0, copy.length / 2);
        return copy;
    }

    @Benchmark
    public long hashing() {
        long sum = Hash.randomSeed();
        for (final long key : keys) {
            sum += Hash.hash64(key, 0x2545F4914F6CDD1DL);
            sum += Hash.reduce((int) key, 1024);
        }
        return sum;
    }

    @Benchmark
    public int hexDecoding() {
        final char[] digits = "0123456789abcdefABCDEF".toCharArray();
        int sum = 0;
        for (int i = 0; i < 4096; i++) {
            sum += StringUtils.getHex(digits[i % digits.length]);
        }
        return sum;
    }
}

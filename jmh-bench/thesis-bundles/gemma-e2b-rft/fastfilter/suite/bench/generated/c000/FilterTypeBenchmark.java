package bench.generated.c000;

import org.fastfilter.FilterType;
import org.fastfilter.Filter;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FilterTypeBenchmark {

    // State fields for inputs
    private long[] keys;
    private int setting;
    private final Random random = new Random(42);

    // Input size constants
    private static final int KEY_COUNT = 10000;
    private static final int SETTING = 12;

    @Setup(Level.Trial)
    public void setup() {
        // Build a fixed set of keys for all benchmarks in the trial scope
        keys = new long[KEY_COUNT];
        for (int i = 0; i < KEY_COUNT; i++) {
            keys[i] = random.nextLong();
        }
        setting = SETTING;
    }

    // --- Bloom Family Benchmarks ---

    @Benchmark
    public void benchmarkBloomConstruct(Blackhole bh) {
        Filter f = FilterType.BLOOM.construct(keys, setting);
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkBlockedBloomConstruct(Blackhole bh) {
        Filter f = FilterType.BLOCKED_BLOOM.construct(keys, setting);
        bh.consume(f);
    }

    // --- Counting Bloom Family Benchmarks ---

    @Benchmark
    public void benchmarkCountingBloomConstruct(Blackhole bh) {
        Filter f = FilterType.COUNTING_BLOOM.construct(keys, setting);
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkSuccinctCountingBloomConstruct(Blackhole bh) {
        Filter f = FilterType.SUCCINCT_COUNTING_BLOOM.construct(keys, setting);
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkSuccinctCountingBloomRankedConstruct(Blackhole bh) {
        Filter f = FilterType.SUCCINCT_COUNTING_BLOOM_RANKED.construct(keys, setting);
        bh.consume(f);
    }

    // --- Xor Family Benchmarks ---

    @Benchmark
    public void benchmarkXorSimpleConstruct(Blackhole bh) {
        Filter f = FilterType.XOR_SIMPLE.construct(keys, 0); // XorSimple constructor signature check: it takes keys, setting is ignored.
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkXor8Construct(Blackhole bh) {
        Filter f = FilterType.XOR_8.construct(keys, 0);
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkXor16Construct(Blackhole bh) {
        Filter f = FilterType.XOR_16.construct(keys, 0);
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkXorPlus8Construct(Blackhole bh) {
        Filter f = FilterType.XOR_PLUS_8.construct(keys, 0);
        bh.consume(f);
    }

    // --- Cuckoo Family Benchmarks ---

    @Benchmark
    public void benchmarkCuckoo8Construct(Blackhole bh) {
        Filter f = FilterType.CUCKOO_8.construct(keys, 0);
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkCuckoo16Construct(Blackhole bh) {
        Filter f = FilterType.CUCKOO_16.construct(keys, 0);
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkCuckooPlus8Construct(Blackhole bh) {
        Filter f = FilterType.CUCKOO_PLUS_8.construct(keys, 0);
        bh.consume(f);
    }

    @Benchmark
    public void benchmarkCuckooPlus16Construct(Blackhole bh) {
        Filter f = FilterType.CUCKOO_PLUS_16.construct(keys, 0);
        bh.consume(f);
    }

    // --- GCS Family Benchmark ---

    @Benchmark
    public void benchmarkGCSConstruct(Blackhole bh) {
        Filter f = FilterType.GCS.construct(keys, setting);
        bh.consume(f);
    }
}

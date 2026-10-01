package bench.generated;

import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.BitSetIterator;
import com.carrotsearch.hppc.ByteArrayDeque;
import com.carrotsearch.hppc.ByteStack;
import com.carrotsearch.hppc.CharArrayList;
import com.carrotsearch.hppc.CharCharHashMap;
import com.carrotsearch.hppc.CharFloatHashMap;
import com.carrotsearch.hppc.CharIntHashMap;
import com.carrotsearch.hppc.CharObjectHashMap;
import com.carrotsearch.hppc.CharShortHashMap;
import com.carrotsearch.hppc.Containers;
import com.carrotsearch.hppc.DoubleArrayList;
import com.carrotsearch.hppc.FloatArrayDeque;
import com.carrotsearch.hppc.FloatStack;
import com.carrotsearch.hppc.IntArrayDeque;
import com.carrotsearch.hppc.IntByteHashMap;
import com.carrotsearch.hppc.IntDoubleHashMap;
import com.carrotsearch.hppc.IntFloatHashMap;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntStack;
import com.carrotsearch.hppc.LongArrayList;
import com.carrotsearch.hppc.LongCharHashMap;
import com.carrotsearch.hppc.LongFloatHashMap;
import com.carrotsearch.hppc.LongIntHashMap;
import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.LongStack;
import com.carrotsearch.hppc.ObjectArrayList;
import com.carrotsearch.hppc.ObjectByteIdentityHashMap;
import com.carrotsearch.hppc.ObjectCharIdentityHashMap;
import com.carrotsearch.hppc.ObjectDoubleIdentityHashMap;
import com.carrotsearch.hppc.ObjectFloatIdentityHashMap;
import com.carrotsearch.hppc.ObjectIdentityHashSet;
import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.ObjectLongHashMap;
import com.carrotsearch.hppc.ObjectShortIdentityHashMap;
import com.carrotsearch.hppc.ShortByteHashMap;
import com.carrotsearch.hppc.ShortHashSet;
import com.carrotsearch.hppc.ShortObjectHashMap;
import com.carrotsearch.hppc.SortedIterationCharCharHashMap;
import com.carrotsearch.hppc.SortedIterationCharLongHashMap;
import com.carrotsearch.hppc.SortedIterationIntCharHashMap;
import com.carrotsearch.hppc.SortedIterationIntIntHashMap;
import com.carrotsearch.hppc.SortedIterationLongByteHashMap;
import com.carrotsearch.hppc.SortedIterationLongIntHashMap;
import com.carrotsearch.hppc.SortedIterationObjectByteHashMap;
import com.carrotsearch.hppc.SortedIterationObjectIntHashMap;
import com.carrotsearch.hppc.SortedIterationShortByteHashMap;
import com.carrotsearch.hppc.SortedIterationShortIntHashMap;
import com.carrotsearch.hppc.CharLongHashMap;
import com.carrotsearch.hppc.LongByteHashMap;
import com.carrotsearch.hppc.ObjectByteHashMap;
import com.carrotsearch.hppc.ShortIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.sorting.IndirectSort;
import com.carrotsearch.hppc.sorting.QuickSort;
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
 * Gold reference JMH suite for the HPPC mutation track.
 *
 * Covers a broad cross-section of the library: hash maps and sets across the
 * primitive and object key types, identity maps and sets, array lists, deques
 * and stacks, the bit set, the sorted-iteration views, and the indirect and
 * quick sorts. Each container kind is exercised for building, probing,
 * iterating (both the cursor loop and the `forEach` procedure/predicate forms)
 * and releasing its buffers.
 *
 * Data is generated once per trial with a fixed seed. Benchmarks that mutate a
 * container build their own copy inside the measured method, so no benchmark
 * depends on the order the others ran in.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class HppcBenchmark {

    /** Element count: large enough to force several rehashes. */
    private static final int SIZE = 50_000;

    private int[] intKeys;
    private long[] longKeys;
    private char[] charKeys;
    private short[] shortKeys;
    private byte[] byteValues;
    private float[] floatValues;
    private double[] doubleValues;
    private String[] objectKeys;

    private IntIntHashMap intIntMap;
    private LongIntHashMap longIntMap;
    private CharIntHashMap charIntMap;
    private ObjectIntHashMap<String> objectIntMap;
    private BitSet left;
    private BitSet right;

    @Setup(Level.Trial)
    public void setUp() {
        final Random random = new Random(20240115L);

        intKeys = new int[SIZE];
        longKeys = new long[SIZE];
        charKeys = new char[SIZE];
        shortKeys = new short[SIZE];
        byteValues = new byte[SIZE];
        floatValues = new float[SIZE];
        doubleValues = new double[SIZE];
        objectKeys = new String[SIZE];
        for (int i = 0; i < SIZE; i++) {
            intKeys[i] = random.nextInt();
            longKeys[i] = random.nextLong();
            charKeys[i] = (char) random.nextInt(Character.MAX_VALUE);
            shortKeys[i] = (short) random.nextInt();
            byteValues[i] = (byte) random.nextInt();
            floatValues[i] = random.nextFloat();
            doubleValues[i] = random.nextDouble();
            objectKeys[i] = "key-" + i;
        }

        intIntMap = new IntIntHashMap(SIZE);
        longIntMap = new LongIntHashMap(SIZE);
        charIntMap = new CharIntHashMap();
        objectIntMap = new ObjectIntHashMap<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            intIntMap.put(intKeys[i], i);
            longIntMap.put(longKeys[i], i);
            charIntMap.put(charKeys[i], i);
            objectIntMap.put(objectKeys[i], i);
        }

        left = new BitSet(1 << 20);
        right = new BitSet(1 << 20);
        for (int i = 0; i < 1 << 16; i++) {
            left.set(random.nextInt(1 << 20));
            right.set(random.nextInt(1 << 20));
        }
    }

    // ---- hash maps: build, probe, iterate ---------------------------------

    @Benchmark
    public IntIntHashMap buildIntIntMap() {
        final IntIntHashMap map = new IntIntHashMap();
        for (int i = 0; i < SIZE; i++) {
            map.put(intKeys[i], i);
        }
        return map;
    }

    @Benchmark
    public long probeIntIntMap() {
        long sum = 0;
        for (int i = 0; i < SIZE; i++) {
            sum += intIntMap.get(intKeys[i]);
        }
        return sum;
    }

    @Benchmark
    public long iterateIntIntMapCursors() {
        long sum = 0;
        for (final IntIntCursor cursor : intIntMap) {
            sum += cursor.key ^ cursor.value;
        }
        return sum;
    }

    /**
     * The predicate form of {@code forEach} on a map's {@code keys()} view, which
     * stops iterating as soon as the predicate returns {@code false}.
     */
    @Benchmark
    public void forEachPredicateOverIntMaps(final Blackhole bh) {
        final IntIntHashMap intInt = new IntIntHashMap();
        final IntByteHashMap intByte = new IntByteHashMap();
        final IntFloatHashMap intFloat = new IntFloatHashMap();
        final IntDoubleHashMap intDouble = new IntDoubleHashMap();
        final IntObjectHashMap<String> intObject = new IntObjectHashMap<>();
        for (int i = 0; i < 4096; i++) {
            intInt.put(intKeys[i], i);
            intByte.put(intKeys[i], byteValues[i]);
            intFloat.put(intKeys[i], floatValues[i]);
            intDouble.put(intKeys[i], doubleValues[i]);
            intObject.put(intKeys[i], objectKeys[i]);
        }
        bh.consume(intInt.keys().forEach((com.carrotsearch.hppc.predicates.IntPredicate) key -> key != 0));
        bh.consume(intByte.keys().forEach((com.carrotsearch.hppc.predicates.IntPredicate) key -> key != 0));
        bh.consume(intFloat.keys().forEach((com.carrotsearch.hppc.predicates.IntPredicate) key -> key != 0));
        bh.consume(intDouble.keys().forEach((com.carrotsearch.hppc.predicates.IntPredicate) key -> key != 0));
        bh.consume(intObject.keys().forEach((com.carrotsearch.hppc.predicates.IntPredicate) key -> key != 0));
    }

    @Benchmark
    public void forEachPredicateOverLongMaps(final Blackhole bh) {
        final LongIntHashMap longInt = new LongIntHashMap();
        final LongCharHashMap longChar = new LongCharHashMap();
        final LongFloatHashMap longFloat = new LongFloatHashMap();
        final LongObjectHashMap<String> longObject = new LongObjectHashMap<>();
        for (int i = 0; i < 4096; i++) {
            longInt.put(longKeys[i], i);
            longChar.put(longKeys[i], charKeys[i]);
            longFloat.put(longKeys[i], floatValues[i]);
            longObject.put(longKeys[i], objectKeys[i]);
        }
        bh.consume(longInt.keys().forEach((com.carrotsearch.hppc.predicates.LongPredicate) k -> k != 0));
        bh.consume(longChar.keys().forEach((com.carrotsearch.hppc.predicates.LongPredicate) k -> k != 0));
        bh.consume(longFloat.keys().forEach((com.carrotsearch.hppc.predicates.LongPredicate) k -> k != 0));
        bh.consume(longObject.keys().forEach((com.carrotsearch.hppc.predicates.LongPredicate) k -> k != 0));
    }

    @Benchmark
    public void forEachPredicateOverCharMaps(final Blackhole bh) {
        final CharCharHashMap charChar = new CharCharHashMap();
        final CharIntHashMap charInt = new CharIntHashMap();
        final CharShortHashMap charShort = new CharShortHashMap();
        final CharFloatHashMap charFloat = new CharFloatHashMap();
        final CharObjectHashMap<String> charObject = new CharObjectHashMap<>();
        for (int i = 0; i < 4096; i++) {
            charChar.put(charKeys[i], charKeys[i]);
            charInt.put(charKeys[i], i);
            charShort.put(charKeys[i], shortKeys[i]);
            charFloat.put(charKeys[i], floatValues[i]);
            charObject.put(charKeys[i], objectKeys[i]);
        }
        bh.consume(charChar.keys().forEach((com.carrotsearch.hppc.predicates.CharPredicate) k -> k != 0));
        bh.consume(charInt.keys().forEach((com.carrotsearch.hppc.predicates.CharPredicate) k -> k != 0));
        bh.consume(charShort.keys().forEach((com.carrotsearch.hppc.predicates.CharPredicate) k -> k != 0));
        bh.consume(charFloat.keys().forEach((com.carrotsearch.hppc.predicates.CharPredicate) k -> k != 0));
        bh.consume(charObject.keys().forEach((com.carrotsearch.hppc.predicates.CharPredicate) k -> k != 0));
    }

    @Benchmark
    public void forEachPredicateOverShortMaps(final Blackhole bh) {
        final ShortByteHashMap shortByte = new ShortByteHashMap();
        final ShortObjectHashMap<String> shortObject = new ShortObjectHashMap<>();
        for (int i = 0; i < 4096; i++) {
            shortByte.put(shortKeys[i], byteValues[i]);
            shortObject.put(shortKeys[i], objectKeys[i]);
        }
        bh.consume(shortByte.keys().forEach((com.carrotsearch.hppc.predicates.ShortPredicate) k -> k != 0));
        bh.consume(
                shortObject.keys().forEach((com.carrotsearch.hppc.predicates.ShortPredicate) k -> k != 0));
    }

    @Benchmark
    public void forEachPredicateOverObjectMaps(final Blackhole bh) {
        final ObjectIntHashMap<String> objectInt = new ObjectIntHashMap<>();
        final ObjectLongHashMap<String> objectLong = new ObjectLongHashMap<>();
        for (int i = 0; i < 4096; i++) {
            objectInt.put(objectKeys[i], i);
            objectLong.put(objectKeys[i], i);
        }
        bh.consume(
                objectInt.keys().forEach((com.carrotsearch.hppc.predicates.ObjectPredicate<String>) k -> k != null));
        bh.consume(
                objectLong.keys().forEach((com.carrotsearch.hppc.predicates.ObjectPredicate<String>) k -> k != null));
    }

    // ---- identity containers ----------------------------------------------

    @Benchmark
    public void identityContainersFrom(final Blackhole bh) {
        final String[] keys = new String[1024];
        final byte[] bytes = new byte[1024];
        final short[] shorts = new short[1024];
        final char[] chars = new char[1024];
        final float[] floats = new float[1024];
        final double[] doubles = new double[1024];
        System.arraycopy(objectKeys, 0, keys, 0, 1024);
        System.arraycopy(byteValues, 0, bytes, 0, 1024);
        System.arraycopy(shortKeys, 0, shorts, 0, 1024);
        System.arraycopy(charKeys, 0, chars, 0, 1024);
        System.arraycopy(floatValues, 0, floats, 0, 1024);
        System.arraycopy(doubleValues, 0, doubles, 0, 1024);

        bh.consume(ObjectIdentityHashSet.from(keys));
        bh.consume(ObjectByteIdentityHashMap.from(keys, bytes));
        bh.consume(ObjectShortIdentityHashMap.from(keys, shorts));
        bh.consume(ObjectCharIdentityHashMap.from(keys, chars));
        bh.consume(ObjectFloatIdentityHashMap.from(keys, floats));
        bh.consume(ObjectDoubleIdentityHashMap.from(keys, doubles));
    }

    // ---- sets --------------------------------------------------------------

    @Benchmark
    public ShortHashSet buildShortSetWithCapacity() {
        final ShortHashSet set = new ShortHashSet();
        set.ensureCapacity(SIZE);
        for (int i = 0; i < SIZE; i++) {
            set.add(shortKeys[i]);
        }
        return set;
    }

    // ---- lists, deques, stacks --------------------------------------------

    @Benchmark
    public void releaseListBuffers(final Blackhole bh) {
        final CharArrayList chars = new CharArrayList();
        final DoubleArrayList doubles = new DoubleArrayList();
        final LongArrayList longs = new LongArrayList();
        final ObjectArrayList<String> objects = new ObjectArrayList<>();
        for (int i = 0; i < 8192; i++) {
            chars.add(charKeys[i]);
            doubles.add(doubleValues[i]);
            longs.add(longKeys[i]);
            objects.add(objectKeys[i]);
        }
        bh.consume(chars.size() + doubles.size() + longs.size() + objects.size());
        chars.release();
        doubles.release();
        longs.release();
        objects.clear();
        bh.consume(objects);
    }

    /**
     * Pushes whole batches: the varargs {@code push(T...)} overload, not the
     * fixed-arity ones the compiler picks for up to four arguments.
     */
    @Benchmark
    public void pushOntoStacks(final Blackhole bh) {
        final IntStack ints = new IntStack();
        final LongStack longs = new LongStack();
        final ByteStack bytes = new ByteStack();
        final FloatStack floats = new FloatStack();
        final int batch = 64;
        final int[] intBatch = new int[batch];
        final long[] longBatch = new long[batch];
        final byte[] byteBatch = new byte[batch];
        final float[] floatBatch = new float[batch];
        for (int offset = 0; offset + batch <= 4096; offset += batch) {
            System.arraycopy(intKeys, offset, intBatch, 0, batch);
            System.arraycopy(longKeys, offset, longBatch, 0, batch);
            System.arraycopy(byteValues, offset, byteBatch, 0, batch);
            System.arraycopy(floatValues, offset, floatBatch, 0, batch);
            ints.push(intBatch);
            longs.push(longBatch);
            bytes.push(byteBatch);
            floats.push(floatBatch);
        }
        bh.consume(ints.pop() + longs.pop() + bytes.pop() + floats.pop());
    }

    @Benchmark
    public long dequeMemoryFootprint() {
        final IntArrayDeque ints = new IntArrayDeque();
        final ByteArrayDeque bytes = new ByteArrayDeque();
        final FloatArrayDeque floats = new FloatArrayDeque();
        for (int i = 0; i < 8192; i++) {
            ints.addLast(intKeys[i]);
            bytes.addFirst(byteValues[i]);
            floats.addLast(floatValues[i]);
        }
        return ints.ramBytesUsed() + bytes.ramBytesUsed() + floats.ramBytesUsed();
    }

    // ---- bit set -----------------------------------------------------------

    @Benchmark
    public long intersectBitSets() {
        final BitSet copy = (BitSet) left.clone();
        copy.intersect(right);
        return copy.cardinality();
    }

    @Benchmark
    public long iterateSetBits() {
        long sum = 0;
        final BitSetIterator iterator = left.iterator();
        for (int bit = iterator.nextSetBit(); bit >= 0; bit = iterator.nextSetBit()) {
            sum += bit;
        }
        return sum;
    }

    // ---- sorted-iteration views -------------------------------------------

    @Benchmark
    public void iterateSortedViewsOnPrimitiveKeys(final Blackhole bh) {
        final IntCharHashMapPair intChar = IntCharHashMapPair.build(intKeys, charKeys);
        final SortedIterationIntCharHashMap intCharView =
                new SortedIterationIntCharHashMap(intChar.map, Integer::compare);
        final SortedIterationIntIntHashMap intIntView =
                new SortedIterationIntIntHashMap(intIntMap, Integer::compare);
        final SortedIterationLongIntHashMap longIntView =
                new SortedIterationLongIntHashMap(longIntMap, Long::compare);
        bh.consume(intCharView.values());
        bh.consume(intIntView.values());
        bh.consume(longIntView.values());
    }

    @Benchmark
    public void iterateSortedViewsOnNarrowKeys(final Blackhole bh) {
        final CharCharHashMap charChar = new CharCharHashMap();
        final CharLongHashMap charLong = new CharLongHashMap();
        final LongByteHashMap longByte = new LongByteHashMap();
        final ShortByteHashMap shortByte = new ShortByteHashMap();
        final ShortIntHashMap shortInt = new ShortIntHashMap();
        for (int i = 0; i < 4096; i++) {
            charChar.put(charKeys[i], charKeys[i]);
            charLong.put(charKeys[i], longKeys[i]);
            longByte.put(longKeys[i], byteValues[i]);
            shortByte.put(shortKeys[i], byteValues[i]);
            shortInt.put(shortKeys[i], i);
        }
        final SortedIterationCharCharHashMap charCharView =
                new SortedIterationCharCharHashMap(charChar, Character::compare);
        final SortedIterationCharLongHashMap charLongView =
                new SortedIterationCharLongHashMap(charLong, Character::compare);
        final SortedIterationLongByteHashMap longByteView =
                new SortedIterationLongByteHashMap(longByte, Long::compare);
        final SortedIterationShortByteHashMap shortByteView =
                new SortedIterationShortByteHashMap(shortByte, Short::compare);
        final SortedIterationShortIntHashMap shortIntView =
                new SortedIterationShortIntHashMap(shortInt, Short::compare);
        bh.consume(charCharView.values());
        bh.consume(charLongView.values());
        bh.consume(longByteView.values());
        bh.consume(shortByteView.values());
        bh.consume(shortIntView.values());
    }

    @Benchmark
    public void iterateSortedViewsOnObjectKeys(final Blackhole bh) {
        final ObjectByteHashMap<String> objectByte = new ObjectByteHashMap<>();
        for (int i = 0; i < 4096; i++) {
            objectByte.put(objectKeys[i], byteValues[i]);
        }
        final Comparator<String> byName = Comparator.naturalOrder();
        final SortedIterationObjectByteHashMap<String> objectByteView =
                new SortedIterationObjectByteHashMap<>(objectByte, byName);
        final SortedIterationObjectIntHashMap<String> objectIntView =
                new SortedIterationObjectIntHashMap<>(objectIntMap, byName);
        bh.consume(objectByteView.values());
        bh.consume(objectIntView.values());
    }

    // ---- sorting and seeding ----------------------------------------------

    @Benchmark
    public int[] indirectMergesort() {
        return IndirectSort.mergesort(0, 8192, (a, b) -> Integer.compare(intKeys[a], intKeys[b]));
    }

    @Benchmark
    public int[] quicksortIndices() {
        final int[] order = new int[8192];
        for (int i = 0; i < order.length; i++) {
            order[i] = i;
        }
        QuickSort.sort(order, (a, b) -> Integer.compare(intKeys[a], intKeys[b]));
        return order;
    }

    @Benchmark
    public long randomSeed() {
        return Containers.randomSeed64();
    }

    /** Keeps the IntCharHashMap construction out of the view benchmark's body. */
    private static final class IntCharHashMapPair {

        private final com.carrotsearch.hppc.IntCharHashMap map;

        private IntCharHashMapPair(final com.carrotsearch.hppc.IntCharHashMap map) {
            this.map = map;
        }

        static IntCharHashMapPair build(final int[] keys, final char[] values) {
            final com.carrotsearch.hppc.IntCharHashMap map =
                    new com.carrotsearch.hppc.IntCharHashMap();
            for (int i = 0; i < 4096; i++) {
                map.put(keys[i], values[i]);
            }
            return new IntCharHashMapPair(map);
        }
    }
}

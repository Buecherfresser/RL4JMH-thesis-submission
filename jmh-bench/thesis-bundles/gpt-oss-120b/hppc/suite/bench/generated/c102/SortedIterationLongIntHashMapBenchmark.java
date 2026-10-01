package bench.generated.c102;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationLongIntHashMap;
import com.carrotsearch.hppc.LongIntHashMap;
import com.carrotsearch.hppc.cursors.LongIntCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.LongIntProcedure;
import com.carrotsearch.hppc.predicates.LongIntPredicate;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongIntComparator;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongIntHashMapBenchmark {

    private static final int MAP_SIZE = 1024;

    private LongIntHashMap delegate;
    private SortedIterationLongIntHashMap viewByKey;
    private SortedIterationLongIntHashMap viewByKeyValue;
    private long[] lookupKeys;
    private int[] lookupIndices;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(0);
        delegate = new LongIntHashMap(MAP_SIZE);
        lookupKeys = new long[MAP_SIZE];
        lookupIndices = new int[MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            long key = rnd.nextLong();
            int value = rnd.nextInt();
            delegate.put(key, value);
            lookupKeys[i] = key;
            lookupIndices[i] = i;
        }

        LongComparator keyComparator = (a, b) -> Long.compare(a, b);
        LongIntComparator keyValueComparator = (ka, va, kb, vb) -> Long.compare(ka, kb);

        viewByKey = new SortedIterationLongIntHashMap(delegate, keyComparator);
        viewByKeyValue = new SortedIterationLongIntHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public int size() {
        return viewByKey.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return viewByKey.isEmpty();
    }

    @Benchmark
    public boolean containsKey() {
        long key = lookupKeys[(int) (System.nanoTime() % MAP_SIZE)];
        return viewByKey.containsKey(key);
    }

    @Benchmark
    public int get() {
        long key = lookupKeys[(int) (System.nanoTime() % MAP_SIZE)];
        return viewByKey.get(key);
    }

    @Benchmark
    public int getOrDefault() {
        long key = lookupKeys[(int) (System.nanoTime() % MAP_SIZE)];
        return viewByKey.getOrDefault(key, -1);
    }

    @Benchmark
    public int indexOf() {
        long key = lookupKeys[(int) (System.nanoTime() % MAP_SIZE)];
        return viewByKey.indexOf(key);
    }

    @Benchmark
    public boolean indexExists() {
        int idx = lookupIndices[(int) (System.nanoTime() % MAP_SIZE)];
        return viewByKey.indexExists(idx);
    }

    @Benchmark
    public int indexGet() {
        int idx = lookupIndices[(int) (System.nanoTime() % MAP_SIZE)];
        return viewByKey.indexGet(idx);
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return viewByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void iterateEntries(Blackhole bh) {
        for (LongIntCursor c : viewByKey) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateKeys(Blackhole bh) {
        for (LongCursor c : viewByKey.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateValues(Blackhole bh) {
        for (IntCursor c : viewByKey.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        viewByKey.forEach((LongIntProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        viewByKey.forEach((LongIntPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public int sizeKeyValueSortedView() {
        return viewByKeyValue.size();
    }

    @Benchmark
    public void iterateEntriesKeyValueSorted(Blackhole bh) {
        for (LongIntCursor c : viewByKeyValue) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }
}

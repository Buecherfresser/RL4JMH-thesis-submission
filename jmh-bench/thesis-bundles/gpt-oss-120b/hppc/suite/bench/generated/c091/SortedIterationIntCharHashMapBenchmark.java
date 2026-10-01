package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.SortedIterationIntCharHashMap;
import com.carrotsearch.hppc.IntCharHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntCharComparator;
import com.carrotsearch.hppc.cursors.IntCharCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.procedures.IntCharProcedure;
import com.carrotsearch.hppc.predicates.IntCharPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntCharHashMapBenchmark {

    private int mapSize;
    private IntCharHashMap delegate;
    private SortedIterationIntCharHashMap mapByKey;
    private SortedIterationIntCharHashMap mapByKeyValue;
    private int[] sampleKeys;
    private int sampleKey;
    private char sampleDefault;

    @Setup(Level.Trial)
    public void setUp() {
        mapSize = 1024;
        sampleDefault = 0;
        delegate = new IntCharHashMap(mapSize);
        sampleKeys = new int[mapSize];
        Random rnd = new Random(0);
        for (int i = 0; i < mapSize; i++) {
            int key = rnd.nextInt();
            char value = (char) rnd.nextInt(Character.MAX_VALUE + 1);
            delegate.put(key, value);
            sampleKeys[i] = key;
        }
        sampleKey = sampleKeys[0];

        IntComparator keyComparator = (a, b) -> Integer.compare(a, b);
        mapByKey = new SortedIterationIntCharHashMap(delegate, keyComparator);

        IntCharComparator keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Integer.compare(k1, k2);
            if (cmp != 0) return cmp;
            return Character.compare(v1, v2);
        };
        mapByKeyValue = new SortedIterationIntCharHashMap(delegate, keyValueComparator);
    }

    // -------------------------------------------------------------------------
    // Basic read operations
    // -------------------------------------------------------------------------

    @Benchmark
    public int sizeByKey() {
        return mapByKey.size();
    }

    @Benchmark
    public int sizeByKeyValue() {
        return mapByKeyValue.size();
    }

    @Benchmark
    public boolean isEmptyByKey() {
        return mapByKey.isEmpty();
    }

    @Benchmark
    public boolean isEmptyByKeyValue() {
        return mapByKeyValue.isEmpty();
    }

    @Benchmark
    public boolean containsKeyByKey() {
        return mapByKey.containsKey(sampleKey);
    }

    @Benchmark
    public boolean containsKeyByKeyValue() {
        return mapByKeyValue.containsKey(sampleKey);
    }

    @Benchmark
    public char getByKey() {
        return mapByKey.get(sampleKey);
    }

    @Benchmark
    public char getByKeyValue() {
        return mapByKeyValue.get(sampleKey);
    }

    @Benchmark
    public char getOrDefaultByKey() {
        return mapByKey.getOrDefault(sampleKey, sampleDefault);
    }

    @Benchmark
    public char getOrDefaultByKeyValue() {
        return mapByKeyValue.getOrDefault(sampleKey, sampleDefault);
    }

    // -------------------------------------------------------------------------
    // Index based operations
    // -------------------------------------------------------------------------

    @Benchmark
    public int indexOfByKey() {
        return mapByKey.indexOf(sampleKey);
    }

    @Benchmark
    public int indexOfByKeyValue() {
        return mapByKeyValue.indexOf(sampleKey);
    }

    @Benchmark
    public boolean indexExistsByKey() {
        int idx = mapByKey.indexOf(sampleKey);
        return mapByKey.indexExists(idx);
    }

    @Benchmark
    public boolean indexExistsByKeyValue() {
        int idx = mapByKeyValue.indexOf(sampleKey);
        return mapByKeyValue.indexExists(idx);
    }

    @Benchmark
    public char indexGetByKey() {
        int idx = mapByKey.indexOf(sampleKey);
        return mapByKey.indexGet(idx);
    }

    @Benchmark
    public char indexGetByKeyValue() {
        int idx = mapByKeyValue.indexOf(sampleKey);
        return mapByKeyValue.indexGet(idx);
    }

    // -------------------------------------------------------------------------
    // Iteration
    // -------------------------------------------------------------------------

    @Benchmark
    public void iterateEntriesByKey(Blackhole bh) {
        Iterator<IntCharCursor> it = mapByKey.iterator();
        if (it.hasNext()) {
            IntCharCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateEntriesByKeyValue(Blackhole bh) {
        Iterator<IntCharCursor> it = mapByKeyValue.iterator();
        if (it.hasNext()) {
            IntCharCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateKeysByKey(Blackhole bh) {
        Iterator<IntCursor> it = mapByKey.keys().iterator();
        if (it.hasNext()) {
            IntCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateKeysByKeyValue(Blackhole bh) {
        Iterator<IntCursor> it = mapByKeyValue.keys().iterator();
        if (it.hasNext()) {
            IntCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateValuesByKey(Blackhole bh) {
        Iterator<CharCursor> it = mapByKey.values().iterator();
        if (it.hasNext()) {
            CharCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateValuesByKeyValue(Blackhole bh) {
        Iterator<CharCursor> it = mapByKeyValue.values().iterator();
        if (it.hasNext()) {
            CharCursor c = it.next();
            bh.consume(c.value);
        }
    }

    // -------------------------------------------------------------------------
    // forEach variants
    // -------------------------------------------------------------------------

    @Benchmark
    public void forEachProcedureByKey(Blackhole bh) {
        mapByKey.forEach((IntCharProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachProcedureByKeyValue(Blackhole bh) {
        mapByKeyValue.forEach((IntCharProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicateByKey(Blackhole bh) {
        mapByKey.forEach((IntCharPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void forEachPredicateByKeyValue(Blackhole bh) {
        mapByKeyValue.forEach((IntCharPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    // -------------------------------------------------------------------------
    // Miscellaneous
    // -------------------------------------------------------------------------

    @Benchmark
    public String visualizeKeyDistributionByKey() {
        return mapByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public String visualizeKeyDistributionByKeyValue() {
        return mapByKeyValue.visualizeKeyDistribution(10);
    }
}

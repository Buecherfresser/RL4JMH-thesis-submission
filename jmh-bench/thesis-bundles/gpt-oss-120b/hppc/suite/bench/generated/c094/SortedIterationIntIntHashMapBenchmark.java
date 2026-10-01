package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.SortedIterationIntIntHashMap;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.IntIntProcedure;
import com.carrotsearch.hppc.predicates.IntIntPredicate;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.predicates.IntPredicate;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntIntComparator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntIntHashMapBenchmark {
    private IntIntHashMap delegate;
    private SortedIterationIntIntHashMap viewByKey;
    private SortedIterationIntIntHashMap viewByKeyValue;
    private IntComparator keyComparator;
    private IntIntComparator keyValueComparator;
    private int existingKey;
    private int existingValue;
    private int missingKey;

    @Setup
    public void setup() {
        Random rnd = new Random(12345L);
        int size = 1024;
        delegate = new IntIntHashMap(size);
        for (int i = 0; i < size; i++) {
            int k = rnd.nextInt();
            int v = rnd.nextInt();
            delegate.put(k, v);
            if (i == 0) {
                existingKey = k;
                existingValue = v;
            }
        }
        do {
            missingKey = rnd.nextInt();
        } while (delegate.containsKey(missingKey));

        keyComparator = (a, b) -> Integer.compare(a, b);
        keyValueComparator = (ka, va, kb, vb) -> {
            int cmp = Integer.compare(ka, kb);
            if (cmp != 0) return cmp;
            return Integer.compare(va, vb);
        };

        viewByKey = new SortedIterationIntIntHashMap(delegate, keyComparator);
        viewByKeyValue = new SortedIterationIntIntHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public int getExisting() {
        return viewByKey.get(existingKey);
    }

    @Benchmark
    public int getMissing() {
        return viewByKey.get(missingKey);
    }

    @Benchmark
    public int getOrDefaultExisting() {
        return viewByKey.getOrDefault(existingKey, -1);
    }

    @Benchmark
    public int getOrDefaultMissing() {
        return viewByKey.getOrDefault(missingKey, -1);
    }

    @Benchmark
    public boolean containsKeyExisting() {
        return viewByKey.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsKeyMissing() {
        return viewByKey.containsKey(missingKey);
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
    public int indexOfExisting() {
        return viewByKey.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExistsExisting() {
        int idx = viewByKey.indexOf(existingKey);
        return viewByKey.indexExists(idx);
    }

    @Benchmark
    public int indexGetExisting() {
        int idx = viewByKey.indexOf(existingKey);
        return viewByKey.indexGet(idx);
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        viewByKey.forEach((IntIntProcedure) (k, v) -> bh.consume(k ^ v));
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        viewByKey.forEach((IntIntPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void iterateKeys(Blackhole bh) {
        for (IntCursor c : viewByKey.keys()) {
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
    public void iteratorNext(Blackhole bh) {
        Iterator<IntIntCursor> it = viewByKey.iterator();
        if (it.hasNext()) {
            IntIntCursor cur = it.next();
            bh.consume(cur.key);
            bh.consume(cur.value);
        }
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return viewByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void forEachProcedureKeyValueComparator(Blackhole bh) {
        viewByKeyValue.forEach((IntIntProcedure) (k, v) -> bh.consume(k ^ v));
    }
}

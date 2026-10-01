package bench.generated.c092;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationIntDoubleHashMap;
import com.carrotsearch.hppc.IntDoubleHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntDoubleComparator;
import com.carrotsearch.hppc.cursors.IntDoubleCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.IntDoubleProcedure;
import com.carrotsearch.hppc.predicates.IntDoublePredicate;
import com.carrotsearch.hppc.IntCollection;
import com.carrotsearch.hppc.DoubleContainer;
import java.util.Iterator;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntDoubleHashMapBenchmark {

    private IntDoubleHashMap delegate;
    private SortedIterationIntDoubleHashMap sortedByKey;
    private SortedIterationIntDoubleHashMap sortedByKeyValue;
    private int testKey;
    private int testIndex;

    @Setup(Level.Trial)
    public void setup() {
        int size = 1024;
        delegate = new IntDoubleHashMap(size);
        Random rnd = new Random(12345L);
        int firstKey = 0;
        for (int i = 0; i < size; i++) {
            int key = rnd.nextInt();
            double value = rnd.nextDouble();
            delegate.put(key, value);
            if (i == 0) {
                firstKey = key;
            }
        }
        testKey = firstKey;
        testIndex = 0; // first position in sorted iteration order

        IntComparator keyComparator = new IntComparator() {
            @Override
            public int compare(int a, int b) {
                return Integer.compare(a, b);
            }
        };

        IntDoubleComparator keyValueComparator = new IntDoubleComparator() {
            @Override
            public int compare(int k1, double v1, int k2, double v2) {
                int cmp = Integer.compare(k1, k2);
                if (cmp != 0) {
                    return cmp;
                }
                return Double.compare(v1, v2);
            }
        };

        sortedByKey = new SortedIterationIntDoubleHashMap(delegate, keyComparator);
        sortedByKeyValue = new SortedIterationIntDoubleHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return sortedByKey.containsKey(testKey);
    }

    @Benchmark
    public int benchmarkSize() {
        return sortedByKey.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return sortedByKey.isEmpty();
    }

    @Benchmark
    public double benchmarkGet() {
        return sortedByKey.get(testKey);
    }

    @Benchmark
    public double benchmarkGetOrDefault() {
        return sortedByKey.getOrDefault(testKey, -1.0);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return sortedByKey.indexOf(testKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        return sortedByKey.indexExists(testIndex);
    }

    @Benchmark
    public double benchmarkIndexGet() {
        return sortedByKey.indexGet(testIndex);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return sortedByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        Iterator<IntDoubleCursor> it = sortedByKey.iterator();
        while (it.hasNext()) {
            IntDoubleCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        sortedByKey.forEach((IntDoubleProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        sortedByKey.forEach((IntDoublePredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkKeysIteration(Blackhole bh) {
        Iterator<IntCursor> it = sortedByKey.keys().iterator();
        while (it.hasNext()) {
            IntCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(Blackhole bh) {
        Iterator<DoubleCursor> it = sortedByKey.values().iterator();
        while (it.hasNext()) {
            DoubleCursor c = it.next();
            bh.consume(c.value);
        }
    }
}

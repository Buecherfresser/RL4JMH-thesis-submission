package bench.generated.c100;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationLongDoubleHashMap;
import com.carrotsearch.hppc.LongDoubleHashMap;
import com.carrotsearch.hppc.cursors.LongDoubleCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongDoubleComparator;
import com.carrotsearch.hppc.procedures.LongDoubleProcedure;
import com.carrotsearch.hppc.predicates.LongDoublePredicate;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongDoubleHashMapBenchmark {

    private static final int SIZE = 10_000;

    private LongDoubleHashMap delegate;
    private SortedIterationLongDoubleHashMap viewKeyComparator;
    private SortedIterationLongDoubleHashMap viewKeyValueComparator;
    private long[] keysArray;
    private LongComparator keyComparator;
    private LongDoubleComparator keyValueComparator;

    @Setup(Level.Trial)
    public void setUp() {
        delegate = new LongDoubleHashMap(SIZE);
        Random rnd = new Random(12345L);
        keysArray = new long[SIZE];
        for (int i = 0; i < SIZE; i++) {
            long k = rnd.nextLong();
            double v = rnd.nextDouble();
            delegate.put(k, v);
            keysArray[i] = k;
        }
        keyComparator = (a, b) -> Long.compare(a, b);
        keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Long.compare(k1, k2);
            if (cmp != 0) return cmp;
            return Double.compare(v1, v2);
        };
        viewKeyComparator = new SortedIterationLongDoubleHashMap(delegate, keyComparator);
        viewKeyValueComparator = new SortedIterationLongDoubleHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public int benchmarkSize() {
        return viewKeyComparator.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return viewKeyComparator.isEmpty();
    }

    @Benchmark
    public double benchmarkGetExistingKey() {
        long key = keysArray[0];
        return viewKeyComparator.get(key);
    }

    @Benchmark
    public int benchmarkContainsKey() {
        long key = keysArray[0];
        return viewKeyComparator.containsKey(key) ? 1 : 0;
    }

    @Benchmark
    public double benchmarkIteratorSum() {
        double sum = 0.0;
        Iterator<LongDoubleCursor> it = viewKeyComparator.iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public long benchmarkKeysIteratorSum() {
        long sum = 0L;
        Iterator<LongCursor> it = viewKeyComparator.keys().iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public double benchmarkValuesIteratorSum() {
        double sum = 0.0;
        Iterator<DoubleCursor> it = viewKeyComparator.values().iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public double benchmarkForEachProcedure() {
        SumProcedure proc = new SumProcedure();
        viewKeyComparator.forEach(proc);
        return proc.sum;
    }

    @Benchmark
    public double benchmarkForEachPredicate() {
        SumPredicate pred = new SumPredicate();
        viewKeyComparator.forEach(pred);
        return pred.sum;
    }

    @Benchmark
    public int benchmarkConstructionKeyComparator() {
        SortedIterationLongDoubleHashMap v = new SortedIterationLongDoubleHashMap(delegate, keyComparator);
        return v.size();
    }

    @Benchmark
    public int benchmarkConstructionKeyValueComparator() {
        SortedIterationLongDoubleHashMap v = new SortedIterationLongDoubleHashMap(delegate, keyValueComparator);
        return v.size();
    }

    private static class SumProcedure implements LongDoubleProcedure {
        double sum = 0.0;
        @Override
        public void apply(long key, double value) {
            sum += value;
        }
    }

    private static class SumPredicate implements LongDoublePredicate {
        double sum = 0.0;
        @Override
        public boolean apply(long key, double value) {
            sum += value;
            return true;
        }
    }
}

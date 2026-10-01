package bench.generated.c092;

import com.carrotsearch.hppc.IntDoubleHashMap;
import com.carrotsearch.hppc.SortedIterationIntDoubleHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.IntDoubleCursor;
import com.carrotsearch.hppc.predicates.IntDoublePredicate;
import com.carrotsearch.hppc.procedures.DoubleProcedure;
import com.carrotsearch.hppc.procedures.IntDoubleProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntDoubleHashMapBenchmark {

    private IntDoubleHashMap delegate;
    private SortedIterationIntDoubleHashMap sortedView;
    private IntComparator comparator;
    private int existingKey;
    private int missingKey;
    private int existingIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(0x12345678L);
        delegate = new IntDoubleHashMap();
        int size = 1000;
        for (int i = 0; i < size; i++) {
            delegate.put(i, random.nextDouble());
        }
        existingKey = 500;
        missingKey = -1;
        comparator = (a, b) -> Integer.compare(a, b);
        sortedView = new SortedIterationIntDoubleHashMap(delegate, comparator);
        existingIndex = sortedView.indexOf(existingKey);
        if (!sortedView.indexExists(existingIndex)) {
            throw new IllegalStateException("Index not found for existing key");
        }
    }

    @Benchmark
    public SortedIterationIntDoubleHashMap constructView() {
        return new SortedIterationIntDoubleHashMap(delegate, comparator);
    }

    @Benchmark
    public boolean containsKey() {
        return sortedView.containsKey(existingKey);
    }

    @Benchmark
    public double get() {
        return sortedView.get(existingKey);
    }

    @Benchmark
    public double getOrDefault() {
        return sortedView.getOrDefault(existingKey, 0.0);
    }

    @Benchmark
    public int indexOf() {
        return sortedView.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExists() {
        return sortedView.indexExists(existingIndex);
    }

    @Benchmark
    public double indexGet() {
        return sortedView.indexGet(existingIndex);
    }

    @Benchmark
    public int size() {
        return sortedView.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return sortedView.isEmpty();
    }

    @Benchmark
    public double forEachProcedure() {
        final double[] sum = {0.0};
        sortedView.forEach((IntDoubleProcedure) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public double forEachPredicate() {
        final double[] sum = {0.0};
        sortedView.forEach((IntDoublePredicate) (k, v) -> {
            sum[0] += v;
            return true;
        });
        return sum[0];
    }

    @Benchmark
    public int keysForEach() {
        final int[] count = {0};
        sortedView.keys().forEach((IntProcedure) k -> count[0]++);
        return count[0];
    }

    @Benchmark
    public double valuesForEach() {
        final double[] sum = {0.0};
        sortedView.values().forEach((DoubleProcedure) v -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public double iterator() {
        double sum = 0.0;
        for (IntDoubleCursor c : sortedView) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public int keysIterator() {
        int count = 0;
        for (IntCursor c : sortedView.keys()) {
            count++;
        }
        return count;
    }

    @Benchmark
    public double valuesIterator() {
        double sum = 0.0;
        for (DoubleCursor c : sortedView.values()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return sortedView.visualizeKeyDistribution(10);
    }
}

package bench.generated.c087;

import com.carrotsearch.hppc.CharLongHashMap;
import com.carrotsearch.hppc.SortedIterationCharLongHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.CharLongCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.CharLongPredicate;
import com.carrotsearch.hppc.procedures.CharLongProcedure;
import com.carrotsearch.hppc.procedures.CharProcedure;
import com.carrotsearch.hppc.procedures.LongProcedure;
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
public class SortedIterationCharLongHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        CharLongHashMap delegate;
        SortedIterationCharLongHashMap sortedView;
        char existingKey;
        char nonExistingKey;
        final int size = 1000;

        @Setup(Level.Trial)
        public void setup() {
            Random random = new Random(12345);
            delegate = new CharLongHashMap();
            boolean first = true;
            for (int i = 0; i < size; i++) {
                char key = (char) random.nextInt(Character.MAX_VALUE + 1);
                long value = random.nextLong();
                delegate.put(key, value);
                if (first) {
                    existingKey = key;
                    first = false;
                }
            }
            // Find a key that is not in the map
            do {
                nonExistingKey = (char) random.nextInt(Character.MAX_VALUE + 1);
            } while (delegate.containsKey(nonExistingKey));

            sortedView = new SortedIterationCharLongHashMap(delegate,
                    (CharComparator) (a, b) -> Character.compare(a, b));
        }
    }

    // Construction (sorting) benchmark
    @Benchmark
    public SortedIterationCharLongHashMap construction(BenchmarkState state) {
        return new SortedIterationCharLongHashMap(state.delegate,
                (CharComparator) (a, b) -> Character.compare(a, b));
    }

    // Iteration via forEach with procedure
    @Benchmark
    public long forEachProcedure(BenchmarkState state) {
        long[] sum = new long[1];
        state.sortedView.forEach((CharLongProcedure) (k, v) -> sum[0] += v);
        return sum[0];
    }

    // Iteration via forEach with predicate (always true)
    @Benchmark
    public long forEachPredicate(BenchmarkState state) {
        long[] sum = new long[1];
        state.sortedView.forEach((CharLongPredicate) (k, v) -> {
            sum[0] += v;
            return true;
        });
        return sum[0];
    }

    // Iteration via iterator
    @Benchmark
    public long iteratorSum(BenchmarkState state) {
        long sum = 0;
        for (CharLongCursor c : state.sortedView) {
            sum += c.value;
        }
        return sum;
    }

    // Keys view iteration via forEach
    @Benchmark
    public long keysForEach(BenchmarkState state) {
        long[] sum = new long[1];
        state.sortedView.keys().forEach((CharProcedure) k -> sum[0] += k);
        return sum[0];
    }

    // Keys view iteration via iterator
    @Benchmark
    public long keysIteratorSum(BenchmarkState state) {
        long sum = 0;
        for (CharCursor c : state.sortedView.keys()) {
            sum += c.value;
        }
        return sum;
    }

    // Values view iteration via forEach
    @Benchmark
    public long valuesForEach(BenchmarkState state) {
        long[] sum = new long[1];
        state.sortedView.values().forEach((LongProcedure) v -> sum[0] += v);
        return sum[0];
    }

    // Values view iteration via iterator
    @Benchmark
    public long valuesIteratorSum(BenchmarkState state) {
        long sum = 0;
        for (LongCursor c : state.sortedView.values()) {
            sum += c.value;
        }
        return sum;
    }

    // Lookup operations
    @Benchmark
    public long getExisting(BenchmarkState state) {
        return state.sortedView.get(state.existingKey);
    }

    @Benchmark
    public long getOrDefaultExisting(BenchmarkState state) {
        return state.sortedView.getOrDefault(state.existingKey, 0L);
    }

    @Benchmark
    public long getOrDefaultNonExisting(BenchmarkState state) {
        return state.sortedView.getOrDefault(state.nonExistingKey, 0L);
    }

    @Benchmark
    public boolean containsKeyExisting(BenchmarkState state) {
        return state.sortedView.containsKey(state.existingKey);
    }

    @Benchmark
    public boolean containsKeyNonExisting(BenchmarkState state) {
        return state.sortedView.containsKey(state.nonExistingKey);
    }

    @Benchmark
    public int indexOfExisting(BenchmarkState state) {
        return state.sortedView.indexOf(state.existingKey);
    }

    @Benchmark
    public boolean indexExistsExisting(BenchmarkState state) {
        int index = state.sortedView.indexOf(state.existingKey);
        return state.sortedView.indexExists(index);
    }

    @Benchmark
    public long indexGetExisting(BenchmarkState state) {
        int index = state.sortedView.indexOf(state.existingKey);
        return state.sortedView.indexGet(index);
    }

    // Distribution visualization (less performance-critical but included)
    @Benchmark
    public String visualizeKeyDistribution(BenchmarkState state) {
        return state.sortedView.visualizeKeyDistribution(100);
    }
}

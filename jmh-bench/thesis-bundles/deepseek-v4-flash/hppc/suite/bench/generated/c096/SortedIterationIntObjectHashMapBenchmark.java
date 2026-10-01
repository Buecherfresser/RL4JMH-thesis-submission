package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.HashSet;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.SortedIterationIntObjectHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import com.carrotsearch.hppc.procedures.IntObjectProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.predicates.IntObjectPredicate;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntObjectComparator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntObjectHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        IntObjectHashMap<Integer> delegate;
        SortedIterationIntObjectHashMap<Integer> sortedByKey;
        SortedIterationIntObjectHashMap<Integer> sortedByKeyValue;
        int[] existingKeys;
        int[] missingKeys;
        int[] indices;
        int size = 1000;
        Random random = new Random(12345);
        IntComparator keyComparator = (a, b) -> Integer.compare(a, b);
        IntObjectComparator<Integer> keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Integer.compare(k1, k2);
            if (cmp == 0) return Integer.compare(v1, v2);
            return cmp;
        };

        @Setup(Level.Trial)
        public void setup() {
            delegate = new IntObjectHashMap<Integer>();
            HashSet<Integer> used = new HashSet<>();
            while (used.size() < size) {
                int key = random.nextInt(100000);
                if (used.add(key)) {
                    delegate.put(key, random.nextInt(1000));
                }
            }
            sortedByKey = new SortedIterationIntObjectHashMap<>(delegate, keyComparator);
            sortedByKeyValue = new SortedIterationIntObjectHashMap<>(delegate, keyValueComparator);

            existingKeys = new int[10];
            int idx = 0;
            for (IntObjectCursor<Integer> c : delegate) {
                if (idx < 10) {
                    existingKeys[idx++] = c.key;
                } else break;
            }

            missingKeys = new int[10];
            int missIdx = 0;
            while (missIdx < 10) {
                int k = random.nextInt(100000);
                if (!delegate.containsKey(k)) {
                    missingKeys[missIdx++] = k;
                }
            }

            indices = new int[existingKeys.length];
            for (int i = 0; i < existingKeys.length; i++) {
                indices[i] = sortedByKey.indexOf(existingKeys[i]);
            }
        }
    }

    // Construction
    @Benchmark
    public SortedIterationIntObjectHashMap<Integer> constructByKey(BenchmarkState state) {
        return new SortedIterationIntObjectHashMap<>(state.delegate, state.keyComparator);
    }

    @Benchmark
    public SortedIterationIntObjectHashMap<Integer> constructByKeyValue(BenchmarkState state) {
        return new SortedIterationIntObjectHashMap<>(state.delegate, state.keyValueComparator);
    }

    // Iteration
    @Benchmark
    public int forEachProcedure(BenchmarkState state) {
        final int[] sum = {0};
        state.sortedByKey.forEach((IntObjectProcedure<Integer>) (k, v) -> sum[0] += k);
        return sum[0];
    }

    @Benchmark
    public int forEachPredicate(BenchmarkState state) {
        final int[] sum = {0};
        state.sortedByKey.forEach((IntObjectPredicate<Integer>) (k, v) -> { sum[0] += k; return true; });
        return sum[0];
    }

    @Benchmark
    public int iteratorSum(BenchmarkState state) {
        int sum = 0;
        for (IntObjectCursor<Integer> c : state.sortedByKey) {
            sum += c.key;
        }
        return sum;
    }

    @Benchmark
    public int keysForEach(BenchmarkState state) {
        final int[] sum = {0};
        state.sortedByKey.keys().forEach((IntProcedure) k -> sum[0] += k);
        return sum[0];
    }

    @Benchmark
    public int valuesForEach(BenchmarkState state) {
        final int[] sum = {0};
        state.sortedByKey.values().forEach((ObjectProcedure<Integer>) v -> sum[0] += v);
        return sum[0];
    }

    // Lookup
    @Benchmark
    public int getExisting(BenchmarkState state) {
        int sum = 0;
        for (int k : state.existingKeys) {
            sum += state.sortedByKey.get(k);
        }
        return sum;
    }

    @Benchmark
    public int getMissing(BenchmarkState state) {
        int sum = 0;
        for (int k : state.missingKeys) {
            Integer v = state.sortedByKey.get(k);
            if (v != null) sum += v;
        }
        return sum;
    }

    @Benchmark
    public int getOrDefaultExisting(BenchmarkState state) {
        int sum = 0;
        for (int k : state.existingKeys) {
            sum += state.sortedByKey.getOrDefault(k, -1);
        }
        return sum;
    }

    @Benchmark
    public int containsKeyExisting(BenchmarkState state) {
        int count = 0;
        for (int k : state.existingKeys) {
            if (state.sortedByKey.containsKey(k)) count++;
        }
        return count;
    }

    @Benchmark
    public int containsKeyMissing(BenchmarkState state) {
        int count = 0;
        for (int k : state.missingKeys) {
            if (state.sortedByKey.containsKey(k)) count++;
        }
        return count;
    }

    @Benchmark
    public int indexOfExisting(BenchmarkState state) {
        int sum = 0;
        for (int k : state.existingKeys) {
            sum += state.sortedByKey.indexOf(k);
        }
        return sum;
    }

    @Benchmark
    public int indexExists(BenchmarkState state) {
        int count = 0;
        for (int idx : state.indices) {
            if (state.sortedByKey.indexExists(idx)) count++;
        }
        return count;
    }

    @Benchmark
    public int indexGet(BenchmarkState state) {
        int sum = 0;
        for (int idx : state.indices) {
            sum += state.sortedByKey.indexGet(idx);
        }
        return sum;
    }

    // Simple queries
    @Benchmark
    public int size(BenchmarkState state) {
        return state.sortedByKey.size();
    }

    @Benchmark
    public boolean isEmpty(BenchmarkState state) {
        return state.sortedByKey.isEmpty();
    }

    @Benchmark
    public String visualizeKeyDistribution(BenchmarkState state) {
        return state.sortedByKey.visualizeKeyDistribution(10);
    }
}

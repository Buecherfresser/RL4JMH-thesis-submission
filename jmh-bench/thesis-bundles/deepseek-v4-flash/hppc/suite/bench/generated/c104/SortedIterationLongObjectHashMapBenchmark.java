package bench.generated.c104;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;
import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.SortedIterationLongObjectHashMap;
import com.carrotsearch.hppc.cursors.LongObjectCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.procedures.LongObjectProcedure;
import com.carrotsearch.hppc.predicates.LongObjectPredicate;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongObjectComparator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongObjectHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class DataState {
        LongObjectHashMap<String> delegate;
        SortedIterationLongObjectHashMap<String> sortedView;
        long existingKey;
        long missingKey;
        int existingIndex;
        String defaultValue = "default";

        @Setup(Level.Trial)
        public void setup() {
            delegate = new LongObjectHashMap<String>();
            Random rnd = new Random(12345);
            Set<Long> keys = new HashSet<>();
            while (keys.size() < 1000) {
                long k = rnd.nextLong();
                if (k != 0 && keys.add(k)) {
                    delegate.put(k, "value" + k);
                }
            }
            existingKey = keys.iterator().next();
            do {
                missingKey = rnd.nextLong();
            } while (missingKey == 0 || keys.contains(missingKey));

            sortedView = new SortedIterationLongObjectHashMap<>(delegate, (a, b) -> Long.compare(a, b));
            existingIndex = sortedView.indexOf(existingKey);
        }
    }

    @Benchmark
    public SortedIterationLongObjectHashMap<String> constructionKeyComparator(DataState state) {
        return new SortedIterationLongObjectHashMap<>(state.delegate, (a, b) -> Long.compare(a, b));
    }

    @Benchmark
    public SortedIterationLongObjectHashMap<String> constructionKeyValueComparator(DataState state) {
        return new SortedIterationLongObjectHashMap<>(state.delegate, (k1, v1, k2, v2) -> {
            int cmp = Long.compare(k1, k2);
            if (cmp == 0) cmp = v1.compareTo(v2);
            return cmp;
        });
    }

    @Benchmark
    public boolean containsKeyHit(DataState state) {
        return state.sortedView.containsKey(state.existingKey);
    }

    @Benchmark
    public boolean containsKeyMiss(DataState state) {
        return state.sortedView.containsKey(state.missingKey);
    }

    @Benchmark
    public String getHit(DataState state) {
        return state.sortedView.get(state.existingKey);
    }

    @Benchmark
    public String getMiss(DataState state) {
        return state.sortedView.get(state.missingKey);
    }

    @Benchmark
    public String getOrDefaultHit(DataState state) {
        return state.sortedView.getOrDefault(state.existingKey, state.defaultValue);
    }

    @Benchmark
    public String getOrDefaultMiss(DataState state) {
        return state.sortedView.getOrDefault(state.missingKey, state.defaultValue);
    }

    @Benchmark
    public int size(DataState state) {
        return state.sortedView.size();
    }

    @Benchmark
    public boolean isEmpty(DataState state) {
        return state.sortedView.isEmpty();
    }

    @Benchmark
    public int indexOfHit(DataState state) {
        return state.sortedView.indexOf(state.existingKey);
    }

    @Benchmark
    public int indexOfMiss(DataState state) {
        return state.sortedView.indexOf(state.missingKey);
    }

    @Benchmark
    public boolean indexExists(DataState state) {
        return state.sortedView.indexExists(state.existingIndex);
    }

    @Benchmark
    public String indexGet(DataState state) {
        return state.sortedView.indexGet(state.existingIndex);
    }

    @Benchmark
    public LongObjectProcedure<String> forEachProcedure(DataState state, Blackhole bh) {
        return state.sortedView.forEach(new LongObjectProcedure<String>() {
            @Override
            public void apply(long key, String value) {
                bh.consume(key);
                bh.consume(value);
            }
        });
    }

    @Benchmark
    public LongObjectPredicate<String> forEachPredicate(DataState state, Blackhole bh) {
        return state.sortedView.forEach(new LongObjectPredicate<String>() {
            @Override
            public boolean apply(long key, String value) {
                bh.consume(key);
                bh.consume(value);
                return true;
            }
        });
    }

    @Benchmark
    public void iterator(DataState state, Blackhole bh) {
        for (LongObjectCursor<String> cursor : state.sortedView) {
            bh.consume(cursor.key);
            bh.consume(cursor.value);
        }
    }

    @Benchmark
    public void keys(DataState state, Blackhole bh) {
        for (LongCursor cursor : state.sortedView.keys()) {
            bh.consume(cursor.value);
        }
    }

    @Benchmark
    public void values(DataState state, Blackhole bh) {
        for (ObjectCursor<String> cursor : state.sortedView.values()) {
            bh.consume(cursor.value);
        }
    }

    @Benchmark
    public String visualizeKeyDistribution(DataState state) {
        return state.sortedView.visualizeKeyDistribution(10);
    }
}

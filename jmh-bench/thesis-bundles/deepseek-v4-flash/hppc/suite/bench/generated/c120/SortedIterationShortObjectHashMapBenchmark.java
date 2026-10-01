package bench.generated.c120;

import com.carrotsearch.hppc.ShortObjectHashMap;
import com.carrotsearch.hppc.SortedIterationShortObjectHashMap;
import com.carrotsearch.hppc.comparators.ShortComparator;
import com.carrotsearch.hppc.comparators.ShortObjectComparator;
import com.carrotsearch.hppc.procedures.ShortObjectProcedure;
import com.carrotsearch.hppc.procedures.ShortProcedure;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.predicates.ShortObjectPredicate;
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
public class SortedIterationShortObjectHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ShortObjectHashMap<String> delegate;
        SortedIterationShortObjectHashMap<String> view;
        SortedIterationShortObjectHashMap<String> viewWithValueComparator;
        short[] keys;
        short[] lookupKeys;
        int size;
        ShortComparator keyComparator;
        ShortObjectComparator<String> keyValueComparator;

        @Setup(Level.Trial)
        public void setup() {
            Random random = new Random(0x12345678L);
            delegate = new ShortObjectHashMap<>();
            int n = 1000;
            keys = new short[n];
            for (int i = 0; i < n; i++) {
                short key;
                do {
                    key = (short) random.nextInt(Short.MAX_VALUE + 1);
                } while (delegate.containsKey(key));
                delegate.put(key, "value" + i);
                keys[i] = key;
            }
            size = n;
            keyComparator = (a, b) -> Short.compare(a, b);
            keyValueComparator = (k1, v1, k2, v2) -> {
                int cmp = Short.compare(k1, k2);
                if (cmp == 0) cmp = v1.compareTo(v2);
                return cmp;
            };
            view = new SortedIterationShortObjectHashMap<>(delegate, keyComparator);
            viewWithValueComparator = new SortedIterationShortObjectHashMap<>(delegate, keyValueComparator);

            lookupKeys = new short[200];
            for (int i = 0; i < 100; i++) lookupKeys[i] = keys[i];
            for (int i = 100; i < 200; i++) {
                short key;
                do {
                    key = (short) random.nextInt(Short.MAX_VALUE + 1);
                } while (delegate.containsKey(key));
                lookupKeys[i] = key;
            }
        }
    }

    // Construction benchmarks

    @Benchmark
    public SortedIterationShortObjectHashMap<String> constructionKeyComparator(BenchmarkState state) {
        return new SortedIterationShortObjectHashMap<>(state.delegate, state.keyComparator);
    }

    @Benchmark
    public SortedIterationShortObjectHashMap<String> constructionKeyValueComparator(BenchmarkState state) {
        return new SortedIterationShortObjectHashMap<>(state.delegate, state.keyValueComparator);
    }

    // Read-only operations

    @Benchmark
    public int iteration(BenchmarkState state, Blackhole bh) {
        int sum = 0;
        for (var cursor : state.view) {
            sum += cursor.key;
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public int forEachProcedure(BenchmarkState state) {
        SumProcedure proc = new SumProcedure();
        state.view.forEach(proc);
        return proc.sum;
    }

    @Benchmark
    public int forEachPredicate(BenchmarkState state) {
        CountPredicate pred = new CountPredicate();
        state.view.forEach(pred);
        return pred.count;
    }

    @Benchmark
    public boolean containsKey(BenchmarkState state) {
        return state.view.containsKey(state.keys[0]);
    }

    @Benchmark
    public String get(BenchmarkState state) {
        return state.view.get(state.keys[0]);
    }

    @Benchmark
    public String getOrDefault(BenchmarkState state) {
        return state.view.getOrDefault((short) 12345, "default");
    }

    @Benchmark
    public int indexOf(BenchmarkState state) {
        return state.view.indexOf(state.keys[0]);
    }

    @Benchmark
    public boolean indexExists(BenchmarkState state) {
        int idx = state.view.indexOf(state.keys[0]);
        return state.view.indexExists(idx);
    }

    @Benchmark
    public String indexGet(BenchmarkState state) {
        int idx = state.view.indexOf(state.keys[0]);
        return state.view.indexGet(idx);
    }

    @Benchmark
    public int keysIteration(BenchmarkState state) {
        SumShortProcedure proc = new SumShortProcedure();
        state.view.keys().forEach(proc);
        return proc.sum;
    }

    @Benchmark
    public int valuesIteration(BenchmarkState state) {
        CountObjectProcedure proc = new CountObjectProcedure();
        state.view.values().forEach(proc);
        return proc.count;
    }

    @Benchmark
    public String visualizeKeyDistribution(BenchmarkState state) {
        return state.view.visualizeKeyDistribution(100);
    }

    // Helper classes for procedures/predicates

    private static class SumProcedure implements ShortObjectProcedure<String> {
        int sum;
        @Override
        public void apply(short key, String value) {
            sum += key;
        }
    }

    private static class CountPredicate implements ShortObjectPredicate<String> {
        int count;
        @Override
        public boolean apply(short key, String value) {
            count++;
            return true;
        }
    }

    private static class SumShortProcedure implements ShortProcedure {
        int sum;
        @Override
        public void apply(short value) {
            sum += value;
        }
    }

    private static class CountObjectProcedure implements ObjectProcedure<String> {
        int count;
        @Override
        public void apply(String value) {
            count++;
        }
    }
}

package bench.generated.c118;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationShortIntHashMap;
import com.carrotsearch.hppc.ShortIntHashMap;
import com.carrotsearch.hppc.comparators.ShortComparator;
import com.carrotsearch.hppc.comparators.ShortIntComparator;
import com.carrotsearch.hppc.cursors.ShortIntCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.ShortIntProcedure;
import com.carrotsearch.hppc.predicates.ShortIntPredicate;
import com.carrotsearch.hppc.predicates.ShortPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationShortIntHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ShortIntHashMap delegate;
        SortedIterationShortIntHashMap view;
        ShortComparator keyComparator;
        ShortIntComparator keyValueComparator;
        short sampleKey;
        short missingKey;
        int sampleIndex;

        @Setup(Level.Trial)
        public void setUp() {
            int size = 1024;
            delegate = new ShortIntHashMap(size);
            Random rnd = new Random(12345L);
            for (int i = 0; i < size; i++) {
                short key = (short) rnd.nextInt(Short.MAX_VALUE + 1);
                int value = rnd.nextInt();
                delegate.put(key, value);
            }

            keyComparator = new ShortComparator() {
                @Override
                public int compare(short a, short b) {
                    return Short.compare(a, b);
                }
            };

            keyValueComparator = new ShortIntComparator() {
                @Override
                public int compare(short key1, int value1, short key2, int value2) {
                    int cmp = Short.compare(key1, key2);
                    if (cmp != 0) return cmp;
                    return Integer.compare(value1, value2);
                }
            };

            // Use key comparator for the view; both comparators produce the same order for this data.
            view = new SortedIterationShortIntHashMap(delegate, keyComparator);

            // pick a key that is present
            short[] keysArray = delegate.keys;
            int idx = 0;
            while (idx < delegate.keys.length && keysArray[idx] == 0) {
                idx++;
            }
            sampleKey = keysArray[idx];
            sampleIndex = view.indexOf(sampleKey);

            // a key that is unlikely to be present
            missingKey = (short) (Short.MAX_VALUE);
        }
    }

    @Benchmark
    public int size(BenchmarkState state) {
        return state.view.size();
    }

    @Benchmark
    public boolean isEmpty(BenchmarkState state) {
        return state.view.isEmpty();
    }

    @Benchmark
    public boolean containsKey(BenchmarkState state) {
        return state.view.containsKey(state.sampleKey);
    }

    @Benchmark
    public int get(BenchmarkState state) {
        return state.view.get(state.sampleKey);
    }

    @Benchmark
    public int getOrDefault(BenchmarkState state) {
        return state.view.getOrDefault(state.missingKey, -1);
    }

    @Benchmark
    public void forEachProcedure(BenchmarkState state, Blackhole bh) {
        state.view.forEach((ShortIntProcedure) (k, v) -> bh.consume(k ^ v));
    }

    @Benchmark
    public void forEachPredicate(BenchmarkState state, Blackhole bh) {
        state.view.forEach((ShortIntPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void iterateKeys(BenchmarkState state, Blackhole bh) {
        for (ShortCursor c : state.view.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateValues(BenchmarkState state, Blackhole bh) {
        for (IntCursor c : state.view.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int indexOf(BenchmarkState state) {
        return state.view.indexOf(state.sampleKey);
    }

    @Benchmark
    public boolean indexExists(BenchmarkState state) {
        return state.view.indexExists(state.sampleIndex);
    }

    @Benchmark
    public int indexGet(BenchmarkState state) {
        return state.view.indexGet(state.sampleIndex);
    }

    @Benchmark
    public String visualizeKeyDistribution(BenchmarkState state) {
        return state.view.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void iteratorNext(BenchmarkState state, Blackhole bh) {
        Iterator<ShortIntCursor> it = state.view.iterator();
        if (it.hasNext()) {
            ShortIntCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }
}

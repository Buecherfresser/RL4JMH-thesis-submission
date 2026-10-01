package bench.generated.c110;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationObjectIntHashMap;
import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.cursors.ObjectIntCursor;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import com.carrotsearch.hppc.predicates.ObjectIntPredicate;
import com.carrotsearch.hppc.predicates.ObjectPredicate;
import com.carrotsearch.hppc.ObjectCollection;
import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.predicates.IntPredicate;
import com.carrotsearch.hppc.comparators.ObjectIntComparator;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import java.util.Comparator;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectIntHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        int MAP_SIZE = 1024;
        ObjectIntHashMap<String> delegate;
        SortedIterationObjectIntHashMap<String> view;
        String[] keys;
        int[] values;
        int keyPos = 0;
        int validIndex;
        Comparator<String> keyComparator = Comparator.naturalOrder();

        @Setup(Level.Trial)
        public void setUp() {
            delegate = new ObjectIntHashMap<>(MAP_SIZE);
            keys = new String[MAP_SIZE];
            values = new int[MAP_SIZE];
            Random rnd = new Random(12345L);
            for (int i = 0; i < MAP_SIZE; i++) {
                String key = "key-" + rnd.nextInt(Integer.MAX_VALUE);
                int value = rnd.nextInt();
                keys[i] = key;
                values[i] = value;
                delegate.put(key, value);
            }
            view = new SortedIterationObjectIntHashMap<>(delegate, keyComparator);
            validIndex = view.indexOf(keys[0]);
        }
    }

    @Benchmark
    public int benchmarkSize(BenchmarkState s) {
        return s.view.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty(BenchmarkState s) {
        return s.view.isEmpty();
    }

    @Benchmark
    public boolean benchmarkContainsKey(BenchmarkState s) {
        int pos = s.keyPos;
        s.keyPos = (pos + 1) % s.MAP_SIZE;
        return s.view.containsKey(s.keys[pos]);
    }

    @Benchmark
    public int benchmarkGet(BenchmarkState s) {
        int pos = s.keyPos;
        s.keyPos = (pos + 1) % s.MAP_SIZE;
        return s.view.get(s.keys[pos]);
    }

    @Benchmark
    public int benchmarkGetOrDefault(BenchmarkState s) {
        int pos = s.keyPos;
        s.keyPos = (pos + 1) % s.MAP_SIZE;
        return s.view.getOrDefault(s.keys[pos], -1);
    }

    @Benchmark
    public int benchmarkIndexOf(BenchmarkState s) {
        int pos = s.keyPos;
        s.keyPos = (pos + 1) % s.MAP_SIZE;
        return s.view.indexOf(s.keys[pos]);
    }

    @Benchmark
    public boolean benchmarkIndexExists(BenchmarkState s) {
        return s.view.indexExists(s.validIndex);
    }

    @Benchmark
    public int benchmarkIndexGet(BenchmarkState s) {
        return s.view.indexGet(s.validIndex);
    }

    @Benchmark
    public void benchmarkIterator(BenchmarkState s, Blackhole bh) {
        Iterator<ObjectIntCursor<String>> it = s.view.iterator();
        while (it.hasNext()) {
            ObjectIntCursor<String> c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkKeysIteration(BenchmarkState s, Blackhole bh) {
        Iterator<ObjectCursor<String>> it = s.view.keys().iterator();
        while (it.hasNext()) {
            ObjectCursor<String> c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(BenchmarkState s, Blackhole bh) {
        Iterator<IntCursor> it = s.view.values().iterator();
        while (it.hasNext()) {
            IntCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(BenchmarkState s, Blackhole bh) {
        ObjectIntProcedure<String> proc = (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        };
        s.view.forEach(proc);
    }

    @Benchmark
    public void benchmarkForEachPredicate(BenchmarkState s, Blackhole bh) {
        ObjectIntPredicate<String> pred = (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        };
        s.view.forEach(pred);
    }

    @Benchmark
    public void benchmarkKeysForEachProcedure(BenchmarkState s, Blackhole bh) {
        ObjectProcedure<String> proc = k -> bh.consume(k);
        s.view.keys().forEach(proc);
    }

    @Benchmark
    public void benchmarkValuesForEachProcedure(BenchmarkState s, Blackhole bh) {
        IntProcedure proc = v -> bh.consume(v);
        s.view.values().forEach(proc);
    }
}

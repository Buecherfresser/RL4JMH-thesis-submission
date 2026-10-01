package bench.generated.c096;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import java.util.Objects;
import com.carrotsearch.hppc.SortedIterationIntObjectHashMap;
import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntObjectComparator;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.predicates.IntObjectPredicate;
import com.carrotsearch.hppc.predicates.IntPredicate;
import com.carrotsearch.hppc.predicates.ObjectPredicate;
import com.carrotsearch.hppc.procedures.IntObjectProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.IntCollection;
import com.carrotsearch.hppc.ObjectContainer;
import java.util.function.IntBinaryOperator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntObjectHashMapBenchmark {

    private static final int MAP_SIZE = 1024;

    private IntObjectHashMap<Integer> delegate;
    private SortedIterationIntObjectHashMap<Integer> view;
    private int existingKey;
    private int missingKey;
    private NoOpProcedure noOpProcedure;
    private NoOpPredicate noOpPredicate;
    private NoOpIntProcedure noOpIntProcedure;
    private NoOpIntPredicate noOpIntPredicate;
    private NoOpObjectProcedure noOpObjectProcedure;
    private NoOpObjectPredicate noOpObjectPredicate;

    @Setup
    public void setup() {
        Random rnd = new Random(12345L);
        delegate = new IntObjectHashMap<>(MAP_SIZE);
        // Fill with deterministic keys to avoid collisions.
        for (int i = 0; i < MAP_SIZE; i++) {
            int key = i;
            delegate.put(key, key * 2);
        }
        existingKey = MAP_SIZE / 2;
        missingKey = -1; // not present in the map

        IntComparator keyComparator = new IntComparator() {
            @Override
            public int compare(int a, int b) {
                return Integer.compare(a, b);
            }
        };
        view = new SortedIterationIntObjectHashMap<>(delegate, keyComparator);

        noOpProcedure = new NoOpProcedure();
        noOpPredicate = new NoOpPredicate();
        noOpIntProcedure = new NoOpIntProcedure();
        noOpIntPredicate = new NoOpIntPredicate();
        noOpObjectProcedure = new NoOpObjectProcedure();
        noOpObjectPredicate = new NoOpObjectPredicate();
    }

    @Benchmark
    public Iterator<IntObjectCursor<Integer>> benchmarkIterator() {
        return view.iterator();
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return view.containsKey(existingKey);
    }

    @Benchmark
    public int benchmarkSize() {
        return view.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return view.isEmpty();
    }

    @Benchmark
    public Integer benchmarkGet() {
        return view.get(existingKey);
    }

    @Benchmark
    public Integer benchmarkGetOrDefault() {
        return view.getOrDefault(missingKey, -1);
    }

    @Benchmark
    public IntObjectProcedure<Integer> benchmarkForEachProcedure() {
        return view.forEach(noOpProcedure);
    }

    @Benchmark
    public IntObjectPredicate<Integer> benchmarkForEachPredicate() {
        return view.forEach(noOpPredicate);
    }

    @Benchmark
    public IntCollection benchmarkKeys() {
        return view.keys();
    }

    @Benchmark
    public ObjectContainer<Integer> benchmarkValues() {
        return view.values();
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return view.indexOf(existingKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        int idx = view.indexOf(existingKey);
        return view.indexExists(idx);
    }

    @Benchmark
    public Integer benchmarkIndexGet() {
        int idx = view.indexOf(existingKey);
        return view.indexGet(idx);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return view.visualizeKeyDistribution(10);
    }

    // Helper classes

    private static class NoOpProcedure implements IntObjectProcedure<Integer> {
        @Override
        public void apply(int key, Integer value) {
            // no-op
        }
    }

    private static class NoOpPredicate implements IntObjectPredicate<Integer> {
        @Override
        public boolean apply(int key, Integer value) {
            return true;
        }
    }

    private static class NoOpIntProcedure implements IntProcedure {
        @Override
        public void apply(int value) {
            // no-op
        }
    }

    private static class NoOpIntPredicate implements IntPredicate {
        @Override
        public boolean apply(int value) {
            return true;
        }
    }

    private static class NoOpObjectProcedure implements ObjectProcedure<Integer> {
        @Override
        public void apply(Integer value) {
            // no-op
        }
    }

    private static class NoOpObjectPredicate implements ObjectPredicate<Integer> {
        @Override
        public boolean apply(Integer value) {
            return true;
        }
    }
}

package bench.generated.c105;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationLongShortHashMap;
import com.carrotsearch.hppc.LongShortHashMap;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.cursors.LongShortCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.procedures.LongShortProcedure;
import com.carrotsearch.hppc.predicates.LongShortPredicate;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongShortHashMapBenchmark {

    @State(org.openjdk.jmh.annotations.Scope.Benchmark)
    public static class BenchmarkState {
        public LongShortHashMap delegate;
        public SortedIterationLongShortHashMap view;
        public long sampleKey;
        public int size = 1024;

        @Setup(org.openjdk.jmh.annotations.Level.Trial)
        public void setUp() {
            delegate = new LongShortHashMap();
            for (int i = 0; i < size; i++) {
                delegate.put(i, (short) i);
            }
            sampleKey = size / 2L;
            LongComparator keyComparator = (a, b) -> Long.compare(a, b);
            view = new SortedIterationLongShortHashMap(delegate, keyComparator);
        }
    }

    @Benchmark
    public int benchmarkSize(BenchmarkState state) {
        return state.view.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty(BenchmarkState state) {
        return state.view.isEmpty();
    }

    @Benchmark
    public boolean benchmarkContainsKey(BenchmarkState state) {
        return state.view.containsKey(state.sampleKey);
    }

    @Benchmark
    public short benchmarkGet(BenchmarkState state) {
        return state.view.get(state.sampleKey);
    }

    @Benchmark
    public short benchmarkGetOrDefault(BenchmarkState state) {
        return state.view.getOrDefault(state.sampleKey, (short) -1);
    }

    @Benchmark
    public int benchmarkIterator(BenchmarkState state) {
        int count = 0;
        for (LongShortCursor c : state.view) {
            count++;
        }
        return count;
    }

    @Benchmark
    public int benchmarkForEachProcedure(BenchmarkState state) {
        final int[] sum = new int[1];
        state.view.forEach((LongShortProcedure) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public int benchmarkForEachPredicate(BenchmarkState state) {
        final int[] sum = new int[1];
        state.view.forEach((LongShortPredicate) (k, v) -> {
            sum[0] += v;
            return true;
        });
        return sum[0];
    }

    @Benchmark
    public int benchmarkKeysContains(BenchmarkState state) {
        return state.view.keys().contains(state.sampleKey) ? 1 : 0;
    }

    @Benchmark
    public int benchmarkKeysIterator(BenchmarkState state) {
        int cnt = 0;
        for (LongCursor c : state.view.keys()) {
            cnt++;
        }
        return cnt;
    }

    @Benchmark
    public int benchmarkValuesContains(BenchmarkState state) {
        short val = (short) state.sampleKey;
        return state.view.values().contains(val) ? 1 : 0;
    }

    @Benchmark
    public int benchmarkValuesIterator(BenchmarkState state) {
        int cnt = 0;
        for (ShortCursor c : state.view.values()) {
            cnt++;
        }
        return cnt;
    }

    @Benchmark
    public int benchmarkIndexOf(BenchmarkState state) {
        return state.view.indexOf(state.sampleKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists(BenchmarkState state) {
        int idx = state.view.indexOf(state.sampleKey);
        return state.view.indexExists(idx);
    }

    @Benchmark
    public short benchmarkIndexGet(BenchmarkState state) {
        int idx = state.view.indexOf(state.sampleKey);
        return state.view.indexGet(idx);
    }
}

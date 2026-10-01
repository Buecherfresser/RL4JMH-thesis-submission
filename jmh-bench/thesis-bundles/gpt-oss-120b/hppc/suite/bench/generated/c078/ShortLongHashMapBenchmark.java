package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortLongHashMap;
import com.carrotsearch.hppc.cursors.ShortLongCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.procedures.ShortLongProcedure;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortLongHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ShortLongHashMap map;
        short[] keys;
        int size = 1024;
        int index = 0;
        Random random = new Random(0);

        @Setup(Level.Trial)
        public void setUp() {
            map = new ShortLongHashMap(size);
            keys = new short[size];
            for (int i = 0; i < size; i++) {
                short k = (short) (i + 1); // avoid zero key (special marker)
                long v = i;
                keys[i] = k;
                map.put(k, v);
            }
        }

        short nextKey() {
            int i = index;
            index = (i + 1) % size;
            return keys[i];
        }

        long nextValue() {
            return random.nextLong();
        }
    }

    @Benchmark
    public long benchPutOverwrite(BenchmarkState state) {
        short k = state.nextKey();
        long v = state.nextValue();
        return state.map.put(k, v);
    }

    @Benchmark
    public long benchGet(BenchmarkState state) {
        short k = state.nextKey();
        return state.map.get(k);
    }

    @Benchmark
    public boolean benchContainsKey(BenchmarkState state) {
        short k = state.nextKey();
        return state.map.containsKey(k);
    }

    @Benchmark
    public long benchAddTo(BenchmarkState state) {
        short k = state.nextKey();
        long inc = 1L;
        return state.map.addTo(k, inc);
    }

    @Benchmark
    public long benchPutOrAdd(BenchmarkState state) {
        short k = state.nextKey();
        long putVal = 5L;
        long inc = 3L;
        return state.map.putOrAdd(k, putVal, inc);
    }

    @Benchmark
    public long benchIterationSum(BenchmarkState state) {
        long sum = 0L;
        for (ShortLongCursor c : state.map) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long benchForEachSum(BenchmarkState state) {
        final long[] sum = new long[1];
        state.map.forEach((ShortLongProcedure) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public long benchKeysIterationSum(BenchmarkState state) {
        long sum = 0L;
        Iterator<ShortCursor> it = state.map.keys().iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public long benchValuesIterationSum(BenchmarkState state) {
        long sum = 0L;
        Iterator<LongCursor> it = state.map.values().iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public int benchCloneSize(BenchmarkState state) {
        ShortLongHashMap clone = state.map.clone();
        return clone.size();
    }
}

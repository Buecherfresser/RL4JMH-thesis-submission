package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ObjectLongIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectLongCursor;
import com.carrotsearch.hppc.procedures.ObjectLongProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectLongIdentityHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class MapState {
        ObjectLongIdentityHashMap<Object> map;
        Object[] keys;
        long[] values;
        int size = 1000;

        @Setup(Level.Trial)
        public void setup() {
            map = new ObjectLongIdentityHashMap<>(size);
            keys = new Object[size];
            values = new long[size];
            for (int i = 0; i < size; i++) {
                keys[i] = new Object(); // distinct identity keys
                values[i] = i * 10L;
                map.put(keys[i], values[i]);
            }
        }
    }

    @Benchmark
    public long putExisting(MapState state) {
        return state.map.put(state.keys[0], 12345L);
    }

    @Benchmark
    public long get(MapState state) {
        return state.map.get(state.keys[0]);
    }

    @Benchmark
    public boolean containsKey(MapState state) {
        return state.map.containsKey(state.keys[0]);
    }

    @Benchmark
    public long putOrAddExisting(MapState state) {
        return state.map.putOrAdd(state.keys[0], 1L, 0L);
    }

    @Benchmark
    public long addToExisting(MapState state) {
        return state.map.addTo(state.keys[0], 0L);
    }

    @Benchmark
    public long iterate(MapState state) {
        long sum = 0;
        for (ObjectLongCursor<Object> cursor : state.map) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public long forEachProcedure(MapState state) {
        long[] sum = new long[1];
        state.map.forEach((ObjectLongProcedure<Object>) (key, value) -> sum[0] += value);
        return sum[0];
    }

    @Benchmark
    public int indexOf(MapState state) {
        return state.map.indexOf(state.keys[0]);
    }

    @Benchmark
    public long indexGet(MapState state) {
        int index = state.map.indexOf(state.keys[0]);
        if (state.map.indexExists(index)) {
            return state.map.indexGet(index);
        }
        return -1L;
    }
}

package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectByteIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectByteCursor;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectByteIdentityHashMapBenchmark {

    private static int SIZE = 1024;

    @State(Scope.Benchmark)
    public static class ReadOnlyState {
        Object[] keys;
        byte[] values;
        ObjectByteIdentityHashMap<Object> map;
        Random rand;

        @Setup(Level.Trial)
        public void setUp() {
            rand = new Random(12345);
            keys = new Object[SIZE];
            values = new byte[SIZE];
            for (int i = 0; i < SIZE; i++) {
                keys[i] = new Object();
                values[i] = (byte) (i & 0xFF);
            }
            map = new ObjectByteIdentityHashMap<>(SIZE);
            for (int i = 0; i < SIZE; i++) {
                map.put(keys[i], values[i]);
            }
        }

        public Object randomKey() {
            return keys[rand.nextInt(SIZE)];
        }
    }

    @State(Scope.Benchmark)
    public static class MutatingState {
        Object[] keys;
        byte[] values;
        ObjectByteIdentityHashMap<Object> map;
        Random rand;

        @Setup(Level.Trial)
        public void setUp() {
            rand = new Random(54321);
            keys = new Object[SIZE];
            values = new byte[SIZE];
            for (int i = 0; i < SIZE; i++) {
                keys[i] = new Object();
                values[i] = (byte) ((i * 31) & 0xFF);
            }
            map = new ObjectByteIdentityHashMap<>(SIZE);
            for (int i = 0; i < SIZE; i++) {
                map.put(keys[i], values[i]);
            }
        }

        public Object randomKey() {
            return keys[rand.nextInt(SIZE)];
        }
    }

    @State(Scope.Benchmark)
    public static class EmptyState {
        ObjectByteIdentityHashMap<Object> map = new ObjectByteIdentityHashMap<>(SIZE);
        Object key = new Object();
        byte value = 42;
    }

    @Benchmark
    public byte benchmarkPut(EmptyState state) {
        return state.map.put(state.key, state.value);
    }

    @Benchmark
    public byte benchmarkGet(ReadOnlyState state) {
        Object k = state.randomKey();
        return state.map.get(k);
    }

    @Benchmark
    public boolean benchmarkContainsKey(ReadOnlyState state) {
        Object k = state.randomKey();
        return state.map.containsKey(k);
    }

    @Benchmark
    public byte benchmarkRemove(MutatingState state) {
        Object k = state.randomKey();
        return state.map.remove(k);
    }

    @Benchmark
    public int benchmarkIteration(ReadOnlyState state, Blackhole bh) {
        int sum = 0;
        for (ObjectByteCursor c : state.map) {
            sum += c.value;
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public boolean benchmarkClear(MutatingState state) {
        state.map.clear();
        return state.map.isEmpty();
    }

    @Benchmark
    public void benchmarkEnsureCapacity(EmptyState state) {
        state.map.ensureCapacity(SIZE * 2);
    }

    @Benchmark
    public byte benchmarkPutOrAdd(MutatingState state) {
        Object k = state.randomKey();
        byte putVal = 1;
        byte incVal = 2;
        return state.map.putOrAdd(k, putVal, incVal);
    }

    @Benchmark
    public byte benchmarkAddTo(MutatingState state) {
        Object k = state.randomKey();
        byte inc = 3;
        return state.map.addTo(k, inc);
    }
}

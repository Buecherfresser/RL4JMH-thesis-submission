package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.IntFloatHashMap;
import com.carrotsearch.hppc.cursors.IntFloatCursor;
import com.carrotsearch.hppc.procedures.IntFloatProcedure;
import com.carrotsearch.hppc.predicates.IntFloatPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntFloatHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class ReadState {
        IntFloatHashMap map;
        int existingKey;
        float existingValue;
        int missingKey;

        @Setup(Level.Trial)
        public void setup() {
            map = new IntFloatHashMap(1024);
            Random rnd = new Random(12345);
            for (int i = 0; i < 512; i++) {
                int k = rnd.nextInt();
                float v = rnd.nextFloat();
                map.put(k, v);
                if (i == 0) {
                    existingKey = k;
                    existingValue = v;
                }
            }
            // generate a key that is very unlikely to be present
            missingKey = rnd.nextInt() ^ 0x7fffffff;
        }
    }

    @State(Scope.Benchmark)
    public static class EmptyState {
        IntFloatHashMap map;
        int key;
        float value;

        @Setup(Level.Invocation)
        public void setup() {
            map = new IntFloatHashMap(128);
            key = 42;
            value = 3.14f;
        }
    }

    @State(Scope.Benchmark)
    public static class FilledState {
        IntFloatHashMap map;
        int key;
        float value;

        @Setup(Level.Invocation)
        public void setup() {
            map = new IntFloatHashMap(128);
            key = 42;
            value = 3.14f;
            map.put(key, value);
        }
    }

    @Benchmark
    public int benchmarkPut(EmptyState state) {
        return Float.floatToIntBits(state.map.put(state.key, state.value));
    }

    @Benchmark
    public float benchmarkGet(ReadState state) {
        return state.map.get(state.existingKey);
    }

    @Benchmark
    public float benchmarkGetOrDefault(ReadState state) {
        return state.map.getOrDefault(state.missingKey, -1.0f);
    }

    @Benchmark
    public boolean benchmarkContainsKey(ReadState state) {
        return state.map.containsKey(state.existingKey);
    }

    @Benchmark
    public float benchmarkRemove(FilledState state) {
        return state.map.remove(state.key);
    }

    @Benchmark
    public float benchmarkPutOrAdd(EmptyState state) {
        return state.map.putOrAdd(state.key, 1.0f, 2.0f);
    }

    @Benchmark
    public float benchmarkAddTo(EmptyState state) {
        return state.map.addTo(state.key, 5.0f);
    }

    @Benchmark
    public int benchmarkIndexOf(ReadState state) {
        return state.map.indexOf(state.existingKey);
    }

    @Benchmark
    public float benchmarkIndexGet(ReadState state) {
        int idx = state.map.indexOf(state.existingKey);
        return state.map.indexGet(idx);
    }

    @Benchmark
    public float benchmarkIndexReplace(ReadState state) {
        int idx = state.map.indexOf(state.existingKey);
        return state.map.indexReplace(idx, state.existingValue + 1.0f);
    }

    @Benchmark
    public void benchmarkIndexInsert(FilledState state) {
        int newKey = state.key + 1;
        int idx = state.map.indexOf(newKey);
        state.map.indexInsert(idx, newKey, 9.9f);
    }

    @Benchmark
    public float benchmarkIndexRemove(FilledState state) {
        int idx = state.map.indexOf(state.key);
        return state.map.indexRemove(idx);
    }

    @Benchmark
    public void benchmarkClear(ReadState state) {
        state.map.clear();
    }

    @Benchmark
    public int benchmarkSize(ReadState state) {
        return state.map.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty(ReadState state) {
        return state.map.isEmpty();
    }

    @Benchmark
    public void benchmarkForEachProcedure(ReadState state, Blackhole bh) {
        state.map.forEach((IntFloatProcedure) (k, v) -> bh.consume(v));
    }

    @Benchmark
    public void benchmarkForEachPredicate(ReadState state, Blackhole bh) {
        state.map.forEach((IntFloatPredicate) (k, v) -> {
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkKeyIteration(ReadState state, Blackhole bh) {
        for (IntFloatCursor c : state.map) {
            bh.consume(c.key);
        }
    }

    @Benchmark
    public void benchmarkValueIteration(ReadState state, Blackhole bh) {
        for (IntFloatCursor c : state.map) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public IntFloatHashMap benchmarkClone(ReadState state) {
        return state.map.clone();
    }

    @Benchmark
    public int benchmarkHashCode(ReadState state) {
        return state.map.hashCode();
    }

    @Benchmark
    public boolean benchmarkEquals(ReadState state) {
        return state.map.equals(state.map.clone());
    }

    @Benchmark
    public IntFloatHashMap benchmarkFromFactory(ReadState state) {
        int size = state.map.size();
        int[] keysCopy = new int[size];
        float[] valuesCopy = new float[size];
        System.arraycopy(state.map.keys, 0, keysCopy, 0, size);
        System.arraycopy(state.map.values, 0, valuesCopy, 0, size);
        return IntFloatHashMap.from(keysCopy, valuesCopy);
    }
}

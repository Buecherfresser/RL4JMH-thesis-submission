package bench.generated.c058;

import com.carrotsearch.hppc.ObjectFloatIdentityHashMap;
import com.carrotsearch.hppc.FloatCollection;
import com.carrotsearch.hppc.ObjectCollection;
import com.carrotsearch.hppc.predicates.ObjectFloatPredicate;
import com.carrotsearch.hppc.procedures.ObjectFloatProcedure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectFloatIdentityHashMapBenchmark {

    private static final int SIZE = 1000;

    // Shared key/value arrays – distinct Integer instances for identity semantics
    private static final Integer[] KEYS = new Integer[SIZE];
    private static final float[] VALUES = new float[SIZE];

    static {
        for (int i = 0; i < SIZE; i++) {
            KEYS[i] = new Integer(i);
            VALUES[i] = i;
        }
    }

    @State(Scope.Benchmark)
    public static class GeneralState {
        ObjectFloatIdentityHashMap<Integer> map;          // read-only map
        ObjectFloatIdentityHashMap<Integer> mapForPut;    // cleared before each put
        ObjectFloatIdentityHashMap<Integer> mapForRemove; // contains one key before remove
        ObjectFloatIdentityHashMap<Integer> mapForUpdate; // for update / indexReplace
        int currentIndex;
        int indexForGet;
        int indexForReplace;

        @Setup(Level.Trial)
        public void setup() {
            map = new ObjectFloatIdentityHashMap<>(SIZE);
            for (int i = 0; i < SIZE; i++) {
                map.put(KEYS[i], VALUES[i]);
            }

            mapForPut = new ObjectFloatIdentityHashMap<>(SIZE);

            mapForRemove = new ObjectFloatIdentityHashMap<>(SIZE);

            mapForUpdate = new ObjectFloatIdentityHashMap<>(SIZE);
            for (int i = 0; i < SIZE; i++) {
                mapForUpdate.put(KEYS[i], VALUES[i]);
            }

            currentIndex = 0;
            indexForGet = map.indexOf(KEYS[0]);
            indexForReplace = mapForUpdate.indexOf(KEYS[0]);
        }

        @Setup(Level.Invocation)
        public void reset() {
            mapForPut.clear();
            mapForRemove.clear();
            mapForRemove.put(KEYS[currentIndex], VALUES[currentIndex]);
            currentIndex = (currentIndex + 1) % SIZE;
        }
    }

    @State(Scope.Benchmark)
    public static class ClearState {
        ObjectFloatIdentityHashMap<Integer> map;

        @Setup(Level.Trial)
        public void setup() {
            map = new ObjectFloatIdentityHashMap<>(SIZE);
            for (int i = 0; i < SIZE; i++) {
                map.put(KEYS[i], VALUES[i]);
            }
        }

        @Setup(Level.Invocation)
        public void reset() {
            // Repopulate after the previous clear() call
            map.clear();
            for (int i = 0; i < SIZE; i++) {
                map.put(KEYS[i], VALUES[i]);
            }
        }
    }

    // --- Basic operations ---

    @Benchmark
    public void put(GeneralState state, Blackhole bh) {
        state.mapForPut.put(KEYS[state.currentIndex], VALUES[state.currentIndex]);
        bh.consume(state.mapForPut);
    }

    @Benchmark
    public void putExisting(GeneralState state, Blackhole bh) {
        state.mapForUpdate.put(KEYS[state.currentIndex], VALUES[state.currentIndex] + 1);
        bh.consume(state.mapForUpdate);
    }

    @Benchmark
    public void get(GeneralState state, Blackhole bh) {
        float v = state.map.get(KEYS[state.currentIndex]);
        bh.consume(v);
    }

    @Benchmark
    public void containsKey(GeneralState state, Blackhole bh) {
        boolean b = state.map.containsKey(KEYS[state.currentIndex]);
        bh.consume(b);
    }

    @Benchmark
    public void remove(GeneralState state, Blackhole bh) {
        float v = state.mapForRemove.remove(KEYS[state.currentIndex]);
        bh.consume(v);
    }

    @Benchmark
    public void clear(ClearState state, Blackhole bh) {
        state.map.clear();
        bh.consume(state.map);
    }

    // --- Bulk / iteration ---

    @Benchmark
    public void forEachProcedure(GeneralState state, Blackhole bh) {
        final float[] sum = new float[1];
        state.map.forEach((ObjectFloatProcedure<Integer>) (key, value) -> sum[0] += value);
        bh.consume(sum[0]);
    }

    @Benchmark
    public void forEachPredicate(GeneralState state, Blackhole bh) {
        final int[] count = new int[1];
        state.map.forEach((ObjectFloatPredicate<Integer>) (key, value) -> {
            count[0]++;
            return count[0] < 10; // stop early
        });
        bh.consume(count[0]);
    }

    @Benchmark
    public void keys(GeneralState state, Blackhole bh) {
        ObjectCollection<Integer> keys = state.map.keys();
        bh.consume(keys.size());
    }

    @Benchmark
    public void values(GeneralState state, Blackhole bh) {
        FloatCollection values = state.map.values();
        bh.consume(values.size());
    }

    // --- Size / capacity ---

    @Benchmark
    public void size(GeneralState state, Blackhole bh) {
        bh.consume(state.map.size());
    }

    @Benchmark
    public void isEmpty(GeneralState state, Blackhole bh) {
        bh.consume(state.map.isEmpty());
    }

    @Benchmark
    public void ramBytesUsed(GeneralState state, Blackhole bh) {
        bh.consume(state.map.ramBytesUsed());
    }

    @Benchmark
    public void ensureCapacity(GeneralState state, Blackhole bh) {
        state.map.ensureCapacity(SIZE * 2);
        bh.consume(state.map);
    }

    // --- Index-based access ---

    @Benchmark
    public void indexOf(GeneralState state, Blackhole bh) {
        int idx = state.map.indexOf(KEYS[state.currentIndex]);
        bh.consume(idx);
    }

    @Benchmark
    public void indexGet(GeneralState state, Blackhole bh) {
        float v = state.map.indexGet(state.indexForGet);
        bh.consume(v);
    }

    @Benchmark
    public void indexExists(GeneralState state, Blackhole bh) {
        boolean b = state.map.indexExists(state.indexForGet);
        bh.consume(b);
    }

    @Benchmark
    public void indexReplace(GeneralState state, Blackhole bh) {
        state.mapForUpdate.indexReplace(state.indexForReplace, 123.4f);
        bh.consume(state.mapForUpdate);
    }

    // --- Factory method ---

    @Benchmark
    public void from(Blackhole bh) {
        ObjectFloatIdentityHashMap<Integer> m = ObjectFloatIdentityHashMap.from(KEYS, VALUES);
        bh.consume(m);
    }
}

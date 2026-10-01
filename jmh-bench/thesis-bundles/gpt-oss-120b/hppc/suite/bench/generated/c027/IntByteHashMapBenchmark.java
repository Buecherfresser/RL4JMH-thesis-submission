package bench.generated.c027;

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
import com.carrotsearch.hppc.IntByteHashMap;
import com.carrotsearch.hppc.cursors.IntByteCursor;
import com.carrotsearch.hppc.procedures.IntByteProcedure;
import com.carrotsearch.hppc.predicates.IntBytePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntByteHashMapBenchmark {

    // Shared map filled with data for read‑only benchmarks.
    private IntByteHashMap readOnlyMap;
    private int[] keys;
    private byte[] values;
    private int existingKey;
    private int missingKey;

    // Map for mutating benchmarks that start empty.
    private IntByteHashMap emptyMap;

    // Equal map for equals() benchmark.
    private IntByteHashMap equalMap;

    @Setup
    public void setup() {
        int size = 1024;
        readOnlyMap = new IntByteHashMap(size);
        keys = new int[size];
        values = new byte[size];
        Random rnd = new Random(0x1234ABCD);
        for (int i = 0; i < size; i++) {
            int k = rnd.nextInt(Integer.MAX_VALUE) + 1; // avoid zero (special key)
            byte v = (byte) rnd.nextInt(256);
            readOnlyMap.put(k, v);
            keys[i] = k;
            values[i] = v;
        }
        existingKey = keys[0];
        missingKey = Integer.MAX_VALUE; // unlikely to be present

        emptyMap = new IntByteHashMap(size);

        equalMap = readOnlyMap.clone();
    }

    // -------------------------------------------------------------------------
    // Read‑only operations
    // -------------------------------------------------------------------------

    @Benchmark
    public byte getExisting() {
        return readOnlyMap.get(existingKey);
    }

    @Benchmark
    public byte getMissing() {
        return readOnlyMap.get(missingKey);
    }

    @Benchmark
    public boolean containsExisting() {
        return readOnlyMap.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsMissing() {
        return readOnlyMap.containsKey(missingKey);
    }

    @Benchmark
    public int indexOfExisting() {
        return readOnlyMap.indexOf(existingKey);
    }

    @Benchmark
    public int size() {
        return readOnlyMap.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return readOnlyMap.isEmpty();
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return readOnlyMap.hashCode();
    }

    @Benchmark
    public int equalsBenchmark() {
        return readOnlyMap.equals(equalMap) ? 1 : 0;
    }

    @Benchmark
    public String toStringBenchmark() {
        return readOnlyMap.toString();
    }

    @Benchmark
    public IntByteProcedure forEachProcedure() {
        return readOnlyMap.forEach((IntByteProcedure) (k, v) -> {
            // no‑op
        });
    }

    @Benchmark
    public IntBytePredicate forEachPredicate() {
        return readOnlyMap.forEach((IntBytePredicate) (k, v) -> true);
    }

    @Benchmark
    public void iterate(Blackhole bh) {
        for (IntByteCursor c : readOnlyMap) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public IntByteHashMap cloneMap() {
        return readOnlyMap.clone();
    }

    // -------------------------------------------------------------------------
    // Mutating operations – each uses its own isolated state to avoid cross‑talk.
    // -------------------------------------------------------------------------

    @State(Scope.Benchmark)
    public static class PutState {
        IntByteHashMap map = new IntByteHashMap(1024);
        int key = 1;
    }

    @Benchmark
    public byte putNew(PutState state) {
        int k = state.key++;
        return state.map.put(k, (byte) (k & 0xFF));
    }

    @State(Scope.Benchmark)
    public static class RemoveState {
        IntByteHashMap map;
        int key;

        @Setup
        public void init() {
            map = new IntByteHashMap(1024);
            for (int i = 0; i < 1024; i++) {
                map.put(i + 1, (byte) i);
            }
            key = 1; // guaranteed present
        }
    }

    @Benchmark
    public byte removeExisting(RemoveState state) {
        return state.map.remove(state.key);
    }

    @State(Scope.Benchmark)
    public static class IndexReplaceState {
        IntByteHashMap map;
        int index;

        @Setup
        public void init() {
            map = new IntByteHashMap(1024);
            for (int i = 0; i < 1024; i++) {
                map.put(i + 1, (byte) i);
            }
            int existingKey = 1;
            index = map.indexOf(existingKey);
        }
    }

    @Benchmark
    public byte indexReplace(IndexReplaceState state) {
        return state.map.indexReplace(state.index, (byte) 42);
    }

    @State(Scope.Benchmark)
    public static class IndexRemoveState {
        IntByteHashMap map;
        int index;

        @Setup
        public void init() {
            map = new IntByteHashMap(1024);
            for (int i = 0; i < 1024; i++) {
                map.put(i + 1, (byte) i);
            }
            int existingKey = 1;
            index = map.indexOf(existingKey);
        }
    }

    @Benchmark
    public byte indexRemove(IndexRemoveState state) {
        return state.map.indexRemove(state.index);
    }

    @State(Scope.Benchmark)
    public static class ClearState {
        IntByteHashMap map;

        @Setup
        public void init() {
            map = new IntByteHashMap(1024);
            for (int i = 0; i < 1024; i++) {
                map.put(i + 1, (byte) i);
            }
        }
    }

    @Benchmark
    public void clearMap(ClearState state) {
        state.map.clear();
    }

    @State(Scope.Benchmark)
    public static class PutOrAddState {
        IntByteHashMap map;
        int existingKey = 1;
        int missingKey = 2_000_000; // not present

        @Setup
        public void init() {
            map = new IntByteHashMap(1024);
            map.put(existingKey, (byte) 10);
        }
    }

    @Benchmark
    public byte putOrAddExisting(PutOrAddState state) {
        return state.map.putOrAdd(state.existingKey, (byte) 5, (byte) 3);
    }

    @Benchmark
    public byte putOrAddMissing(PutOrAddState state) {
        return state.map.putOrAdd(state.missingKey, (byte) 7, (byte) 2);
    }

    @State(Scope.Benchmark)
    public static class AddToState {
        IntByteHashMap map;
        int existingKey = 1;
        int missingKey = 3_000_000;

        @Setup
        public void init() {
            map = new IntByteHashMap(1024);
            map.put(existingKey, (byte) 20);
        }
    }

    @Benchmark
    public byte addToExisting(AddToState state) {
        return state.map.addTo(state.existingKey, (byte) 4);
    }

    @Benchmark
    public byte addToMissing(AddToState state) {
        return state.map.addTo(state.missingKey, (byte) 4);
    }
}

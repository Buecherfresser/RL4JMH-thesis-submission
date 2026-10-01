package bench.generated.c062;

import com.carrotsearch.hppc.ObjectIntIdentityHashMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmark suite for ObjectIntIdentityHashMap.
 * This map uses reference identity for keys (Object) and stores integer values.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIntIdentityHashMapBenchmark {

    // --- Test Data Setup ---
    private static final int MAP_SIZE = 1000;
    private static final int KEY_POOL_SIZE = 2000;

    // Keys are objects, so we need a concrete class for identity comparison
    private static class BenchmarkKey {
        private final int id;
        public BenchmarkKey(int id) { this.id = id; }
        // No equals/hashCode needed, as the map uses identity (==)
    }

    // --- State Fields ---
    private ObjectIntIdentityHashMap<BenchmarkKey> map;
    private BenchmarkKey[] keys;
    private int[] values;
    private BenchmarkKey[] keyPool;

    @Setup(Level.Trial)
    public void setupTrial() {
        // 1. Generate keys and values
        keys = new BenchmarkKey[MAP_SIZE];
        values = new int[MAP_SIZE];
        keyPool = new BenchmarkKey[KEY_POOL_SIZE];

        Random random = new Random(42);

        for (int i = 0; i < KEY_POOL_SIZE; i++) {
            keyPool[i] = new BenchmarkKey(i);
        }

        for (int i = 0; i < MAP_SIZE; i++) {
            // Use keys from the pool to ensure we have distinct objects
            keys[i] = keyPool[random.nextInt(KEY_POOL_SIZE)];
            values[i] = i * 2;
        }
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Reinitialize the map for each invocation to ensure consistent timing
        map = new ObjectIntIdentityHashMap<>(MAP_SIZE);
    }

    // --- Benchmarks ---

    /**
     * Benchmarks the construction of the map from pre-generated arrays.
     */
    @Benchmark
    public ObjectIntIdentityHashMap<BenchmarkKey> benchmarkFromArrays() {
        // Since this is a construction benchmark, we must create a new map instance
        // and populate it using the static factory method.
        return ObjectIntIdentityHashMap.from(keys, values);
    }

    /**
     * Benchmarks inserting a single key-value pair.
     */
    @Benchmark
    public int benchmarkPut() {
        // Use a key that is guaranteed to be in the map (keys[0])
        return map.put(keys[0], values[0]);
    }

    /**
     * Benchmarks retrieving a value for an existing key.
     */
    @Benchmark
    public int benchmarkGetExisting() {
        int result = map.get(keys[0]);
        return result;
    }

    /**
     * Benchmarks retrieving a value for a non-existent key.
     */
    @Benchmark
    public int benchmarkGetNonExisting() {
        // Use a key guaranteed not to be in the map (e.g., a new object)
        BenchmarkKey nonExistentKey = new BenchmarkKey(99999);
        int result = map.getOrDefault(nonExistentKey, -1);
        return result;
    }

    /**
     * Benchmarks checking if a key exists.
     */
    @Benchmark
    public boolean benchmarkContainsKey() {
        return map.containsKey(keys[0]);
    }

    /**
     * Benchmarks removing an existing key.
     * NOTE: Assuming map.remove returns the value (int) associated with the key.
     */
    @Benchmark
    public int benchmarkRemoveExisting() {
        return map.remove(keys[0]);
    }

    /**
     * Benchmarks adding a key-value pair that already exists (should update/overwrite).
     */
    @Benchmark
    public int benchmarkPutOverwrite() {
        // Put the same key again, expecting the value to be updated
        return map.put(keys[0], 999);
    }

    /**
     * Benchmarks adding a key-value pair that does not exist.
     */
    @Benchmark
    public int benchmarkPutNew() {
        // Use a key guaranteed not to be in the map
        BenchmarkKey newKey = new BenchmarkKey(99999);
        return map.put(newKey, 1);
    }

    /**
     * Benchmarks the size retrieval operation.
     */
    @Benchmark
    public int benchmarkSize() {
        return map.size();
    }

    /**
     * Benchmarks clearing the map.
     */
    @Benchmark
    public void benchmarkClear() {
        map.clear();
    }
}

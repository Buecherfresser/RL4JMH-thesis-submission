package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ObjectLongIdentityHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectLongIdentityHashMapBenchmark {

    // Concrete type for KType
    private static final int MAP_SIZE = 10000;
    private static final long[] VALUES = new long[MAP_SIZE];
    private ObjectLongIdentityHashMap<Integer> map;

    @Setup
    public void setup() {
        Random random = new Random(42);
        List<Integer> keysList = new ArrayList<>(MAP_SIZE);
        
        // 1. Setup keys and values
        for (int i = 0; i < MAP_SIZE; i++) {
            int key = random.nextInt(MAP_SIZE * 2); // Generate distinct keys
            keysList.add(key);
            VALUES[i] = random.nextLong();
        }

        // 2. Build the map using the static factory method.
        // We convert List<Integer> to Integer[] to satisfy the KType[] requirement.
        Integer[] keysArray = keysList.toArray(new Integer[0]);
        map = ObjectLongIdentityHashMap.from(keysArray, VALUES);
    }

    @Benchmark
    public void testPutGet() {
        // Test insertion
        int key = new Random().nextInt(MAP_SIZE * 2);
        long value = System.nanoTime();
        map.put(key, value);

        // Test lookup
        map.get(key);
    }

    @Benchmark
    public void testGetExisting() {
        // Pick a key known to exist from setup (e.g., key 100, assuming it was generated)
        int key = 100; 
        map.get(key);
    }

    @Benchmark
    public void testContainsKey() {
        // Test containment check for an existing key
        int key = 100;
        map.containsKey(key);
    }

    @Benchmark
    public void testRemove() {
        // Test removal of an existing key
        int key = 100;
        map.remove(key);
    }

    @Benchmark
    public void testSize() {
        // Test size retrieval
        map.size();
    }

    @Benchmark
    public void testIsEmpty() {
        // Test empty status (should be false after setup)
        map.isEmpty();
    }

    @Benchmark
    public void testClear() {
        // Test clearing the map
        map.clear();
    }

    @Benchmark
    public void testStaticFrom() {
        // Test the static factory method with fresh inputs
        ObjectLongIdentityHashMap<Integer> newMap = ObjectLongIdentityHashMap.from(
            new Integer[MAP_SIZE], 
            VALUES
        );
        newMap.size();
    }
}

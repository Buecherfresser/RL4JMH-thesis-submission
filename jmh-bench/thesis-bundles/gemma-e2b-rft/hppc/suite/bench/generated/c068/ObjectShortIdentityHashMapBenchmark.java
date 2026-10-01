package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Random;

import com.carrotsearch.hppc.ObjectShortIdentityHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectShortIdentityHashMapBenchmark {

    private ObjectShortIdentityHashMap<Integer> map;
    private Integer[] keys;
    private short[] values;
    private int mapSize;

    @Setup
    public void setup() {
        Random random = new Random(42);
        // Define a fixed size for the map
        this.mapSize = 5000;

        // 1. Generate distinct Integer keys
        this.keys = new Integer[mapSize];
        for (int i = 0; i < mapSize; i++) {
            // Use distinct objects to test identity hashing
            keys[i] = new Integer(i);
        }

        // 2. Generate short values
        this.values = new short[mapSize];
        for (int i = 0; i < mapSize; i++) {
            values[i] = (short) (i % 32);
        }

        // 3. Initialize the map using the static factory method
        this.map = ObjectShortIdentityHashMap.from(keys, values);
    }

    @Benchmark
    public void testPutGet(Blackhole bh) {
        // Test put operation
        int index = (int) (Math.random() * mapSize);
        Integer key = keys[index];
        short value = values[index];

        map.put(key, value);

        // Test get operation
        short retrievedValue = map.get(key);
        bh.consume(retrievedValue);
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        // Test getOrDefault operation (for a key that exists)
        int index = (int) (Math.random() * mapSize);
        Integer key = keys[index];

        // Ensure the key is present (it should be, based on setup)
        short value = map.getOrDefault(key, (short) 0);
        bh.consume(value);
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        // Test containsKey operation (for an existing key)
        int index = (int) (Math.random() * mapSize);
        Integer key = keys[index];

        boolean contains = map.containsKey(key);
        bh.consume(contains);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Test remove operation
        int index = (int) (Math.random() * mapSize);
        Integer key = keys[index];

        map.remove(key);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size operation
        bh.consume(map.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        // Test isEmpty operation (should be false after setup)
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void testPutAll(Blackhole bh) {
        // Test putAll operation with a subset of data
        int subsetSize = mapSize / 10;
        Integer[] subsetKeys = Arrays.copyOfRange(keys, 0, subsetSize);
        short[] subsetValues = Arrays.copyOfRange(values, 0, subsetSize);

        ObjectShortIdentityHashMap<Integer> tempMap = new ObjectShortIdentityHashMap<>();
        tempMap.putAll(ObjectShortIdentityHashMap.from(subsetKeys, subsetValues));
        bh.consume(tempMap.size());
    }

    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Test the static factory method from(KType[] keys, short[] values)
        int testSize = 1000;
        Integer[] testKeys = new Integer[testSize];
        short[] testValues = new short[testSize];
        for (int i = 0; i < testSize; i++) {
            testKeys[i] = new Integer(i);
            testValues[i] = (short) (i % 10);
        }

        ObjectShortIdentityHashMap<Integer> result = ObjectShortIdentityHashMap.from(testKeys, testValues);
        bh.consume(result.size());
    }
}

package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.Random;

import com.carrotsearch.hppc.ObjectIntIdentityHashMap;
import com.carrotsearch.hppc.ObjectIntAssociativeContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIntIdentityHashMapBenchmark {

    private ObjectIntIdentityHashMap<Integer> map;
    private Integer[] keys;
    private int[] values;
    private int mapSize;

    @Setup
    public void setup() {
        Random random = new Random(42);
        int numElements = 10000;

        keys = new Integer[numElements];
        values = new int[numElements];

        for (int i = 0; i < numElements; i++) {
            // Create distinct Integer objects to test identity hashing
            keys[i] = new Integer(random.nextInt());
            values[i] = random.nextInt(100000);
        }

        // Initialize the map with the generated data
        map = ObjectIntIdentityHashMap.from(keys, values);
        this.mapSize = numElements;
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test insertion of a new element
        int keyIndex = (int) (Math.random() * mapSize);
        int newKey = keys[keyIndex];
        int newValue = 99999;

        map.put(newKey, newValue);
        bh.consume(null); // Consume null as the method is void
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test lookup of an existing element
        int keyIndex = (int) (Math.random() * mapSize);
        int existingKey = keys[keyIndex];

        int result = map.get(existingKey);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Test lookup of a non-existing element
        int keyIndex = (int) (Math.random() * mapSize);
        // Use a key guaranteed not to be in the setup keys
        Integer nonExistingKey = new Integer(999999);
        int defaultValue = -1;

        int result = map.getOrDefault(nonExistingKey, defaultValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Test checking for existence
        int keyIndex = (int) (Math.random() * mapSize);
        int existingKey = keys[keyIndex];

        boolean contains = map.containsKey(existingKey);
        bh.consume(contains);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Test removal of an existing element
        int keyIndex = (int) (Math.random() * mapSize);
        int keyToRemove = keys[keyIndex];

        map.remove(keyToRemove);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size retrieval
        int size = map.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clearing the map
        map.clear();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        // Test bulk insertion by iterating over a subset
        int subsetSize = mapSize / 10;

        for (int i = 0; i < subsetSize; i++) {
            int key = keys[i * 10];
            int value = values[i * 10];
            map.put(key, value);
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Test construction via the static factory method using Integer[] keys
        int size = 100;
        Integer[] smallKeys = new Integer[size];
        int[] smallValues = new int[size];
        for (int i = 0; i < size; i++) {
            smallKeys[i] = i;
            smallValues[i] = i * 2;
        }

        ObjectIntIdentityHashMap<Integer> newMap = ObjectIntIdentityHashMap.from(smallKeys, smallValues);
        bh.consume(newMap);
    }
}

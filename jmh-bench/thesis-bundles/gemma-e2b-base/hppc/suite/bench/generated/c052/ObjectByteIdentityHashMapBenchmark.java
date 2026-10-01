package bench.generated.c052;

import com.carrotsearch.hppc.ObjectByteIdentityHashMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectByteIdentityHashMapBenchmark {

    private ObjectByteIdentityHashMap<Integer> map;
    private List<Integer> keys;
    private byte[] values;
    private int mapSize;
    private int initialCapacity;

    @Setup
    public void setup() {
        Random random = new Random(42);
        // Setup a moderately sized map for general operations
        this.initialCapacity = 1000;
        this.mapSize = 5000;
        this.map = new ObjectByteIdentityHashMap<>();
        this.keys = new ArrayList<>(mapSize);
        this.values = new byte[mapSize];

        // Populate the map with unique keys and values
        for (int i = 0; i < mapSize; i++) {
            int key = random.nextInt(mapSize * 2); // Generate diverse keys
            keys.add(key);
            values[i] = (byte) (i % 256);
            map.put(key, values[i]);
        }
    }

    @Benchmark
    public void testPutOperation(Blackhole bh) {
        int key = keys.get(0);
        byte value = values[0];
        map.put(key, value);
        bh.consume(map);
    }

    @Benchmark
    public void testGetOperation(Blackhole bh) {
        int key = keys.get(mapSize / 2);
        bh.consume(map.get(key));
    }

    @Benchmark
    public void testContainsKeyOperation(Blackhole bh) {
        int key = keys.get(mapSize / 4);
        bh.consume(map.containsKey(key));
    }

    @Benchmark
    public void testRemoveOperation(Blackhole bh) {
        int key = keys.get(0);
        map.remove(key);
        bh.consume(map);
    }

    @Benchmark
    public void testSizeOperation(Blackhole bh) {
        bh.consume(map.size());
    }

    @Benchmark
    public void testIsEmptyOperation(Blackhole bh) {
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Create a small set of inputs for the static factory test
        Integer[] keysArray = new Integer[100];
        byte[] valuesArray = new byte[100];
        for (int i = 0; i < 100; i++) {
            keysArray[i] = i;
            valuesArray[i] = (byte) (i % 256);
        }

        ObjectByteIdentityHashMap<Integer> newMap = ObjectByteIdentityHashMap.from(keysArray, valuesArray);
        bh.consume(newMap);
    }

    @Benchmark
    public void testPutAllOperation(Blackhole bh) {
        // Create a temporary container to test putAll
        List<Integer> tempKeys = new ArrayList<>();
        byte[] tempValues = new byte[1000];
        for (int i = 0; i < 1000; i++) {
            tempKeys.add(i);
            tempValues[i] = (byte) (i % 256);
        }

        ObjectByteIdentityHashMap<Integer> tempMap = ObjectByteIdentityHashMap.from(tempKeys.toArray(new Integer[0]), tempValues);

        map.putAll(tempMap);
        bh.consume(map);
    }
}

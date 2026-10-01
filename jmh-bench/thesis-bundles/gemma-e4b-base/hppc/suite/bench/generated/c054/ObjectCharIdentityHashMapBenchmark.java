package bench.generated.c054;

import com.carrotsearch.hppc.ObjectCharIdentityHashMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectCharIdentityHashMapBenchmark {

    private ObjectCharIdentityHashMap<String> map;
    private String[] keys;
    private char[] values;
    private String testKey;
    private final int MAP_SIZE = 1000;

    @Setup
    public void setup() {
        // 1. Generate inputs
        keys = new String[MAP_SIZE];
        values = new char[MAP_SIZE];
        Random random = new Random(42);

        for (int i = 0; i < MAP_SIZE; i++) {
            // Use simple strings for keys
            keys[i] = "Key" + i;
            // Use random char values
            values[i] = (char) (random.nextInt(256));
        }

        // Select a key for lookup tests
        testKey = keys[MAP_SIZE / 2];

        // 2. Initialize the map (pre-populate it for read tests)
        // We use the static factory method to build the initial state
        map = ObjectCharIdentityHashMap.from(keys, values);
    }

    @TearDown
    public void tearDown() {
        // Clean up resources if necessary
        if (map != null) {
            map.release();
        }
    }

    /**
     * Benchmarks the insertion of a single key-value pair.
     */
    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // We put a new element. The map instance is reused.
        int result = map.put(testKey + "_new", 'A');
        bh.consume(result);
    }

    /**
     * Benchmarks retrieving a value by key.
     */
    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Read operation on the pre-populated map
        char value = map.get(testKey);
        bh.consume(value);
    }

    /**
     * Benchmarks checking if a key exists.
     */
    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Read operation
        boolean contains = map.containsKey(testKey);
        bh.consume(contains);
    }

    /**
     * Benchmarks the size retrieval operation.
     */
    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Read operation
        int size = map.size();
        bh.consume(size);
    }

    /**
     * Benchmarks the isEmpty check.
     */
    @Benchmark
    public void benchmarkIsEmpty(Blackhole bh) {
        // Read operation
        boolean isEmpty = map.isEmpty();
        bh.consume(isEmpty);
    }

    /**
     * Benchmarks the static factory method construction from arrays.
     * Since this is a static method that creates a new object, we measure the construction time.
     */
    @Benchmark
    public ObjectCharIdentityHashMap<String> benchmarkFromStaticFactory(Blackhole bh) {
        // Create fresh copies of inputs to ensure the factory method runs on clean data
        String[] keysCopy = new String[MAP_SIZE];
        char[] valuesCopy = new char[MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            keysCopy[i] = keys[i];
            valuesCopy[i] = values[i];
        }

        ObjectCharIdentityHashMap<String> newMap = ObjectCharIdentityHashMap.from(keysCopy, valuesCopy);
        bh.consume(newMap);
        return newMap;
    }
}

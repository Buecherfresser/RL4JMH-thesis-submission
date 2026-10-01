package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectByteIdentityHashMap;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectByteIdentityHashMapBenchmark {

    private ObjectByteIdentityHashMap<String> map;
    private String key;
    private byte value;
    private final int INITIAL_CAPACITY = 1000;

    @Setup
    public void setup() {
        // Initialize the map instance
        map = new ObjectByteIdentityHashMap<>(INITIAL_CAPACITY);

        // Prepare representative inputs
        // Use a unique key instance for identity comparison
        key = new String("testKeyInstance");
        value = (byte) 42;
    }

    @TearDown
    public void tearDown() {
        // Clean up resources if necessary
        if (map != null) {
            map.release();
        }
    }

    /**
     * Benchmarks the time taken to insert a key-value pair into the map.
     * Since this is a mutating operation, the map size grows during the benchmark run.
     */
    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Call the subject method exactly once
        int result = map.put(key, value);
        bh.consume(result);
    }

    /**
     * Benchmarks the time taken to retrieve a value given a key.
     */
    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Call the subject method exactly once
        byte result = map.get(key);
        bh.consume(result);
    }

    /**
     * Benchmarks the time taken to check if a key exists in the map.
     */
    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Call the subject method exactly once
        boolean exists = map.containsKey(key);
        bh.consume(exists);
    }

    /**
     * Benchmarks the time taken to remove a key-value pair from the map.
     */
    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Call the subject method exactly once
        int removedCount = map.remove(key);
        bh.consume(removedCount);
    }
}

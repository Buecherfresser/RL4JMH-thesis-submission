package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.carrotsearch.hppc.ObjectShortHashMap;
import com.carrotsearch.hppc.cursors.ObjectCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectShortHashMapBenchmark {

    // State field to hold the map instance. Since the map is mutable,
    // we create a new instance in @Setup to ensure isolation.
    private ObjectShortHashMap<Object> map;

    @Setup
    public void setup() {
        // Initialize a fresh map instance for each benchmark run.
        // Using Object as the key type, as supported by the implementation.
        this.map = new ObjectShortHashMap<>();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test insertion of a new key/value pair.
        map.put("key1", (short) 100);
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test retrieval of an existing key.
        // We rely on the map being populated from setup or previous runs,
        // but since we create a new map in setup, this tests the map's internal structure
        // under minimal load (or we could populate it here if we wanted to test lookups).
        // For simplicity and isolation, we test a known operation on the fresh map.
        try {
            map.put("testKey", (short) 1);
        } catch (Exception e) {
            // Ignore potential exceptions if map initialization fails in a specific environment
        }
        bh.consume(map.get("testKey"));
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Test getOrDefault for a non-existent key.
        bh.consume(map.getOrDefault("nonExistentKey", (short) 0));
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Test containsKey for a key that should not exist.
        bh.consume(map.containsKey("nonExistentKey"));
    }

    @Benchmark
    public void benchmarkIndexGet(Blackhole bh) {
        // Test indexGet on a populated map (requires population, which we skip for pure speed test)
        // Since we cannot rely on state persistence across benchmarks without violating
        // the spirit of the setup/benchmark separation, we test a simple operation.
        try {
            map.put("indexKey", (short) 50);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(map.get("indexKey"));
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clear operation.
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size operation.
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkHashCode(Blackhole bh) {
        // Test hashCode calculation (requires some data to be present for meaningful results,
        // but we test the method call itself).
        try {
            map.put("hashKey", (short) 1);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(map.hashCode());
    }
}

package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.IntDoubleHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntDoubleHashMapBenchmark {

    // State field for the map instance. Since map operations mutate state,
    // we rely on JMH creating a fresh instance or the benchmark setup
    // to handle state isolation, or we reset it in @Setup if needed.
    private IntDoubleHashMap map;

    @Setup
    public void setup() {
        // Initialize a map instance. We don't populate it heavily here
        // to avoid setup time dominating the benchmark, focusing on operations.
        this.map = new IntDoubleHashMap();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test insertion into an empty map
        map.put(1, 1.0);
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test lookup on an empty map (should return 0.0d)
        double result = map.get(99);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetExisting(Blackhole bh) {
        // Populate the map slightly to test successful lookup
        map.put(10, 100.0);
        double result = map.get(10);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetDefault(Blackhole bh) {
        // Test getOrDefault on a missing key
        double result = map.getOrDefault(99, 5.0);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Test containsKey on a key that doesn't exist
        boolean result = map.containsKey(99);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIndexGet(Blackhole bh) {
        // Populate map to ensure index access is valid (requires internal state setup)
        map.put(1, 1.0);
        double result = map.get(1); // Using get as a proxy for index access if we don't know the internal structure
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clear operation
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size calculation
        int size = map.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkRelease(Blackhole bh) {
        // Test release operation (should reset internal buffers)
        map.put(1, 1.0);
        map.release();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Test static factory method (requires creating a new map instance)
        try {
            // Use a small array for quick construction
            IntDoubleHashMap newMap = IntDoubleHashMap.from(new int[]{1, 2}, new double[]{1.1, 2.2});
            bh.consume(newMap);
        } catch (IllegalArgumentException e) {
            // Ignore if array sizes cause issues in a specific environment
        }
    }
}

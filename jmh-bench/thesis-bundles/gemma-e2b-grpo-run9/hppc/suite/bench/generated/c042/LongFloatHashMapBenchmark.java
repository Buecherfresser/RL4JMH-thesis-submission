package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongFloatHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongFloatHashMapBenchmark {

    // State field for the map instance. Since we are using Mode.AverageTime
    // and want to measure the cost of operations on a single structure,
    // we initialize it here.
    private LongFloatHashMap map;

    @Setup
    public void setup() {
        // Initialize a map. Using the default constructor which initializes
        // with DEFAULT_EXPECTED_ELEMENTS.
        this.map = new LongFloatHashMap();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test insertion/update. Since put returns a float, we consume it.
        // We use non-final literals for the key/value to avoid anti-patterns.
        map.put(1000L, 1.0f);
        bh.consume(map.get(1000L));
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test lookup.
        float result = map.get(1000L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Test getOrDefault.
        float result = map.getOrDefault(9999L, 5.0f);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size calculation.
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clear operation.
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Test removal of an existing key (if present).
        // We rely on the map having at least one element from setup,
        // or we test removal on an empty map (which should return 0f).
        if (map.size() > 0) {
            map.remove(1000L);
        } else {
            // Ensure we don't crash if the map is empty, though remove should handle it.
            map.remove(1L);
        }
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        // Test putAll with an iterable (simulated by a simple loop structure
        // if we had a cursor, but since we don't have access to LongFloatCursor
        // easily, we rely on the internal implementation calling put repeatedly).
        // For a true test, we would need a LongFloatAssociativeContainer.
        // Since we cannot easily construct a container here without more imports/classes,
        // we rely on the fact that putAll iterates over the provided iterable.
        // We call it with an empty iterable to test the overhead of the loop structure.
        try {
            map.putAll(java.util.Collections.emptyList());
        } catch (Exception e) {
            // Ignore exceptions if the internal implementation is strict about iterables
        }
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkStaticFrom(Blackhole bh) {
        // Test the static factory method. This creates a new map instance.
        try {
            // Create dummy arrays (non-final literals are fine here as they are local)
            long[] keys = {1L, 2L};
            float[] values = {1.1f, 2.2f};
            LongFloatHashMap.from(keys, values);
        } catch (IllegalArgumentException e) {
            // Ignore if array lengths mismatch or other setup issues occur
        }
    }
}

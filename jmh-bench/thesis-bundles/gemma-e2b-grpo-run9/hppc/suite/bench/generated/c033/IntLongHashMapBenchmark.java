package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.IntLongHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntLongHashMapBenchmark {

    // State field to hold the map instance. Since we want to measure the cost
    // of operations on a fresh map, we initialize it here, relying on JMH
    // to handle state isolation between benchmark methods if they are not
    // explicitly synchronized or if we rely on the method being stateless
    // relative to the benchmark run.
    private IntLongHashMap map;

    @Setup
    public void setup() {
        // Initialize a fresh map instance for each benchmark run.
        // This ensures that internal state (like keys/values arrays) is fresh.
        this.map = new IntLongHashMap();
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test put operation. We use a key/value pair that is unlikely to exist
        // to minimize complex internal collision handling, focusing on insertion path.
        map.put(100, 123456789L);
        bh.consume(map.get(100));
    }

    @Benchmark
    public void testGetExisting(Blackhole bh) {
        // Ensure the map has something to look up (by putting it first)
        map.put(1, 1L);
        bh.consume(map.get(1));
    }

    @Benchmark
    public void testGetNonExisting(Blackhole bh) {
        // Test lookup for a key that definitely doesn't exist.
        bh.consume(map.get(999));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size calculation.
        bh.consume(map.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation.
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void testPutAll(Blackhole bh) {
        // Test putAll using an empty iterable (should return 0 change).
        bh.consume(map.putAll(java.util.Collections.emptyList()));
    }

    @Benchmark
    public void testStaticFactory(Blackhole bh) {
        // Test the static factory method.
        try {
            IntLongHashMap.from(new int[]{1, 2}, new long[]{10L, 20L});
        } catch (IllegalArgumentException e) {
            // Ignore expected exceptions if input validation fails, though unlikely here.
        }
        bh.consume(null); // Consume null as the method returns void/doesn't return a value we can consume easily
    }
}

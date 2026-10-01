package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ShortIntHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortIntHashMapBenchmark {

    // State field for the map instance. Since put/remove mutate it,
    // we rely on JMH's isolation or ensure setup resets it if needed.
    private ShortIntHashMap map;

    @Setup
    public void setup() {
        // Initialize a fresh map instance for each benchmark run/trial.
        // This ensures that mutation tests start from a clean slate.
        this.map = new ShortIntHashMap();
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test insertion. We use short keys and int values.
        // Since we are using Mode.AverageTime, we don't need to return the result,
        // but we must consume the result or ensure the operation is performed.
        map.put((short) 10, 100);
        bh.consume(map);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test retrieval. This is a read operation.
        // We must ensure the map has data, or handle the 0 key case gracefully.
        try {
            map.put((short) 10, 100);
        } catch (Exception e) {
            // Ignore potential exceptions if map initialization fails in a specific environment
        }
        bh.consume(map.get((short) 10));
    }

    @Benchmark
    public void testGetDefault(Blackhole bh) {
        // Test getOrDefault.
        bh.consume(map.getOrDefault((short) 99, 0));
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
        bh.consume(map);
    }

    @Benchmark
    public void testStaticFrom(Blackhole bh) {
        // Test the static factory method.
        try {
            ShortIntHashMap.from(new short[]{1, 2}, new int[]{10, 20});
        } catch (IllegalArgumentException e) {
            // Ignore if array sizes mismatch, which shouldn't happen here.
        }
        bh.consume(null); // Consume null as the method returns void/is static
    }
}

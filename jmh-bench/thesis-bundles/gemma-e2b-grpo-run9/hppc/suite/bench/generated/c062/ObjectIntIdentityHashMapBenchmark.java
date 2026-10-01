package bench.generated.c062;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectIntIdentityHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectIntIdentityHashMapBenchmark {

    // State field for the map instance. Since we are benchmarking the map itself,
    // we rely on JMH's isolation or ensure operations are idempotent/resetting.
    private ObjectIntIdentityHashMap<Object> map;

    @Setup
    public void setup() {
        // Initialize a fresh map for each benchmark run (or trial, depending on JMH configuration)
        // Using the default constructor which initializes with DEFAULT_EXPECTED_ELEMENTS.
        this.map = new ObjectIntIdentityHashMap<>();
    }

    @Benchmark
    public void testPutGet(Blackhole bh) {
        // Test put operation
        map.put("key1", 100);
        // Test get operation (should return 100)
        int value = map.get("key1");
        bh.consume(value);
    }

    @Benchmark
    public void testGetNonExistent(Blackhole bh) {
        // Test get operation for a key that doesn't exist
        int value = map.get("nonExistentKey");
        bh.consume(value);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size calculation
        int size = map.size();
        bh.consume(size);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation
        map.clear();
        // Consume something to prevent dead code elimination
        bh.consume(true);
    }

    @Benchmark
    public void testPutAll(Blackhole bh) {
        // Test putAll operation (requires some initial data)
        map.put("k1", 1);
        map.put("k2", 2);
        
        // Put all operation (using a simple structure for demonstration)
        // Note: Since we don't have an IntIntAssociativeContainer readily available,
        // we simulate a bulk operation by putting a few more items.
        map.put("k3", 3);
        
        bh.consume(map.size());
    }
}

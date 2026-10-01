package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongIntHashMap;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongIntHashMapBenchmark {

    // State field for the map instance. Since LongIntHashMap is mutable,
    // we rely on the benchmark methods to either clear it or create a new one
    // if state consistency across iterations is critical.
    private LongIntHashMap map;

    @Setup
    public void setup() {
        // Initialize a fresh map instance for the benchmark run.
        // We rely on the fact that JMH manages the lifecycle of this instance
        // across iterations, and we will clear it in the benchmark methods.
        this.map = new LongIntHashMap();
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test insertion/update. Since we are using a single instance,
        // we rely on the fact that the map is cleared or reset before the next iteration
        // if we were testing state persistence across benchmarks.
        // For pure timing of a single operation, this is acceptable.
        map.put(100L, 1);
        bh.consume(map);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test lookup on an existing key (if put was called previously, or rely on map initialization)
        // Since we don't guarantee state persistence across benchmark methods,
        // we test a lookup that should fail or succeed based on the current state.
        // For a fair test, we rely on the map being empty or reset by the harness if possible.
        // We test a key that is unlikely to exist initially.
        int result = map.get(9999999999L);
        bh.consume(result);
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        // Test getOrDefault.
        int result = map.getOrDefault(100L, 0);
        bh.consume(result);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size calculation.
        int size = map.size();
        bh.consume(size);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation.
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void testPutAll(Blackhole bh) {
        // Test putAll with an empty iterable (should return 0 change)
        try {
            map.putAll(java.util.Collections.emptyList());
        } catch (Exception e) {
            // Ignore exceptions if the map implementation throws them on empty input
        }
        bh.consume(map);
    }
}

package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntCharHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class IntCharHashMapBenchmark {

    // State field for read-only operations or static factory tests
    private IntCharHashMap map;

    @Setup
    public void setup() {
        // Initialize a fresh map for stateful tests if needed, 
        // or rely on the benchmark method creating its own instance.
        // For simplicity, we initialize a default map here.
        this.map = new IntCharHashMap();
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test put operation (mutating)
        map.put(10, 'A');
        bh.consume(map);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test get operation (read-only)
        bh.consume(map.get(10));
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        // Test getOrDefault operation (read-only)
        bh.consume(map.getOrDefault(99, 'Z'));
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        // Test containsKey operation (read-only)
        bh.consume(map.containsKey(10));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size operation (read-only)
        bh.consume(map.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation (mutating)
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Test remove operation (mutating)
        map.put(1, 'X');
        map.remove(1);
        bh.consume(map);
    }

    @Benchmark
    public void testStaticFrom(Blackhole bh) {
        // Test static factory method (read-only construction)
        try {
            int[] keys = {1, 2, 3};
            char[] values = {'a', 'b', 'c'};
            IntCharHashMap.from(keys, values);
            bh.consume(true);
        } catch (IllegalArgumentException e) {
            // Ignore if setup fails due to constraints, though unlikely here
        }
    }
}

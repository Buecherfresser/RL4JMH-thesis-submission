package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.CharDoubleHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CharDoubleHashMapBenchmark {

    // State field to hold the map instance. Since CharDoubleHashMap is mutable,
    // we will instantiate it inside the benchmark method or use static factories
    // to ensure isolation between benchmark runs.

    @Benchmark
    public void testPutGet(Blackhole bh) {
        // Create a fresh instance for each benchmark run to avoid state pollution
        CharDoubleHashMap map = new CharDoubleHashMap();
        
        // Test put
        map.put('a', 1.0);
        map.put('b', 2.0);

        // Test get
        double valueA = map.get('a');
        bh.consume(valueA);

        // Test putOrAdd (update existing key)
        map.putOrAdd('a', 5.0, 1.0); // Should result in 1.0 + 1.0 = 2.0 (if we assume initial put was 1.0)
        double valueAUpdated = map.get('a');
        bh.consume(valueAUpdated);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        CharDoubleHashMap map = new CharDoubleHashMap();
        bh.consume(map.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        CharDoubleHashMap map = new CharDoubleHashMap();
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        CharDoubleHashMap map = new CharDoubleHashMap();
        map.put('x', 1.0);
        
        // Test clear (void method must consume Blackhole)
        map.clear();
        
        bh.consume(map.size());
    }

    @Benchmark
    public void testRelease(Blackhole bh) {
        CharDoubleHashMap map = new CharDoubleHashMap();
        map.put('x', 1.0);
        
        // Test release (void method must consume Blackhole)
        map.release();
        
        bh.consume(map.size());
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        // Setup a map with some data to test cloning
        CharDoubleHashMap original = new CharDoubleHashMap();
        original.put('a', 1.0);
        
        try {
            CharDoubleHashMap cloned = original.clone();
            // Check if clone is functional (e.g., check size)
            bh.consume(cloned.size());
        } catch (RuntimeException e) {
            // Ignore if clone fails due to internal constraints, but we don't consume anything
        }
    }

    @Benchmark
    public void testStaticFactoryFrom(Blackhole bh) {
        // Test the static factory method
        char[] keys = {'a', 'b', 'c'};
        double[] values = {1.0, 2.0, 3.0};
        
        try {
            CharDoubleHashMap map = CharDoubleHashMap.from(keys, values);
            bh.consume(map.size());
        } catch (IllegalArgumentException e) {
            // Ignore if factory fails due to internal constraints
        }
    }
}

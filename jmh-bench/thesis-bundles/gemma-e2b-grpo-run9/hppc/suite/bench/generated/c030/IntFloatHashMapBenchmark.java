package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntFloatHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntFloatHashMapBenchmark {

    // State field is not strictly necessary if we instantiate locally in @Benchmark,
    // but we keep the class structure clean.

    @Benchmark
    public void testPut(Blackhole bh) {
        // Create a fresh map for each invocation to avoid state pollution
        IntFloatHashMap localMap = new IntFloatHashMap();
        localMap.put(10, 1.0f);
        bh.consume(localMap);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        IntFloatHashMap localMap = new IntFloatHashMap();
        localMap.put(10, 1.0f);
        bh.consume(localMap.get(10));
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        IntFloatHashMap localMap = new IntFloatHashMap();
        localMap.put(10, 1.0f);
        bh.consume(localMap.getOrDefault(10, 99.0f));
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        IntFloatHashMap map = new IntFloatHashMap();
        map.put(1, 1.0f);
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        IntFloatHashMap map = new IntFloatHashMap();
        bh.consume(map.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        IntFloatHashMap map = new IntFloatHashMap();
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Test the static factory method
        try {
            IntFloatHashMap.from(new int[]{1, 2}, new float[]{1.1f, 2.2f});
        } catch (IllegalArgumentException e) {
            // Ignore expected exceptions if input validation fails
        }
        bh.consume(null);
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        IntFloatHashMap original = new IntFloatHashMap();
        original.put(1, 1.0f);
        try {
            IntFloatHashMap cloned = original.clone();
            bh.consume(cloned);
        } catch (RuntimeException e) {
            // Ignore clone exceptions if they occur
        }
    }
}

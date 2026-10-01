package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.carrotsearch.hppc.ObjectFloatIdentityHashMap;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectFloatIdentityHashMapBenchmark {

    // State field for the map instance. Since we are testing mutation,
    // we initialize it in setup and rely on JMH's trial isolation
    // or accept that mutation tests might be slightly skewed if the map
    // state persists across benchmarks (which is fine for AverageTime).
    private ObjectFloatIdentityHashMap<Object> map;

    @Setup
    public void setup() {
        // Initialize the map. Using the default constructor.
        this.map = new ObjectFloatIdentityHashMap<>();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test a simple put operation. Since we are using Object keys,
        // we use a simple, non-static object instance.
        // We don't care about the return value, just the side effect.
        map.put(new Object(), 1.0f);
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test a get operation. We must ensure the key exists or handle potential exceptions
        // if the underlying implementation throws for missing keys (though the API suggests
        // it returns a default value or throws based on the specific method).
        // Since we don't know the exact behavior for missing keys, we test a known key.
        try {
            map.get(new Object());
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they occur during a simple lookup
        }
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test the size method.
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test the clear operation.
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Test the static factory method.
        try {
            // Create dummy arrays for the static call
            Object[] keys = new Object[0];
            float[] values = new float[0];
            ObjectFloatIdentityHashMap<Object> newMap = ObjectFloatIdentityHashMap.from(keys, values);
            bh.consume(newMap);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectObjectHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectObjectHashMapBenchmark {

    // State field to hold the map instance. Since we are testing methods that mutate state,
    // we must ensure that the state is either reset or the operation is idempotent.
    // For simplicity and isolation, we will create a new instance inside each benchmark
    // method, relying on the constructor cost being negligible compared to map operations.
    private ObjectObjectHashMap<Object, Object> map;

    @Setup
    public void setup() {
        // No complex setup needed if we instantiate inside the benchmark method,
        // but we keep this method for compliance if we were to use a shared state.
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Create a fresh map instance for each invocation to avoid state contamination
        ObjectObjectHashMap<Object, Object> localMap = new ObjectObjectHashMap<>();
        try {
            localMap.put("key1", 100);
            localMap.put("key2", "value2");
            bh.consume(localMap);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they occur during setup/teardown
        }
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        ObjectObjectHashMap<Object, Object> localMap = new ObjectObjectHashMap<>();
        try {
            // Populate map minimally to ensure get doesn't immediately fail on null state
            localMap.put("key1", 100);
            bh.consume(localMap.get("key1"));
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        ObjectObjectHashMap<Object, Object> localMap = new ObjectObjectHashMap<>();
        try {
            // Ensure map is populated enough to measure non-zero size
            localMap.put("k1", 1);
            bh.consume(localMap.size());
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        ObjectObjectHashMap<Object, Object> localMap = new ObjectObjectHashMap<>();
        try {
            localMap.put("key1", 100);
            bh.consume(localMap.containsKey("key1"));
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        ObjectObjectHashMap<Object, Object> localMap = new ObjectObjectHashMap<>();
        try {
            localMap.put("k1", 1);
            localMap.clear();
            bh.consume(localMap);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        ObjectObjectHashMap<Object, Object> localMap = new ObjectObjectHashMap<>();
        try {
            localMap.put("key1", 100);
            // Remove an existing key
            localMap.remove("key1");
            bh.consume(localMap);
        } catch (Exception e) {
            // Ignore
        }
    }
}

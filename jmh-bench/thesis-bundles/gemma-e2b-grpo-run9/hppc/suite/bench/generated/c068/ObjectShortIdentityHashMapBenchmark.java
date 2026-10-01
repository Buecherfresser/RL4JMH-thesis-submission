package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectShortIdentityHashMap;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectShortIdentityHashMapBenchmark {

    // State field for the map instance. Since we are benchmarking instance methods,
    // we initialize it here. We use Object as the generic type KType.
    private ObjectShortIdentityHashMap<Object> map;

    @Setup
    public void setup() {
        // Initialize the map. Using the default constructor.
        // This setup cost is acceptable as it runs once per trial.
        this.map = new ObjectShortIdentityHashMap<>();
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test a read-only operation.
        // We consume the result to prevent dead code elimination.
        bh.consume(this.map.size());
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test a read-only operation.
        // We consume the result.
        try {
            // Attempt to get a key that likely doesn't exist, which is a safe read.
            this.map.get(new Object());
        } catch (Exception e) {
            // Ignore exceptions if the underlying implementation throws on missing keys,
            // though for this benchmark, we focus on the path taken.
        }
        bh.consume(null); // Consume null as we don't return a value here
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test a mutating operation (void return type).
        // Must take Blackhole and consume the result.
        this.map.clear();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test a mutating operation.
        // We use a non-final object to ensure the identity hash is calculated correctly
        // and the map state is modified.
        try {
            this.map.put(new Object(), (short) 1);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPutMultiple(Blackhole bh) {
        // Test a mutating operation with multiple calls.
        try {
            this.map.put(new Object(), (short) 1);
            this.map.put(new Object(), (short) 2);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}

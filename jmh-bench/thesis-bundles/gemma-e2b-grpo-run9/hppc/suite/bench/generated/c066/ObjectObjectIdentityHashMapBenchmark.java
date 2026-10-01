package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectObjectIdentityHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectObjectIdentityHashMapBenchmark {

    // State field for the map instance. Since we are benchmarking the class itself,
    // we initialize it here. For mutating operations, this state will be mutated
    // across iterations, which is acceptable for measuring the cost of the operation
    // on a single instance, though not ideal for measuring pure insertion cost.
    private ObjectObjectIdentityHashMap<Object, Object> map;

    @Setup
    public void setup() {
        // Initialize a fresh map instance for each benchmark run/iteration cycle
        // to ensure we are testing the operation on a clean slate,
        // although JMH manages the lifecycle.
        this.map = new ObjectObjectIdentityHashMap<>();
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test a read-only operation
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Test a read-only operation
        // Since the map is empty initially (or reset by JMH setup), this should be fast.
        bh.consume(map.containsKey(new Object()));
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test a mutating operation. We use a new object reference for the key
        // to ensure identity hashing is tested, though the map implementation
        // handles null keys specially.
        map.put(new Object(), new Object());
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test a read operation. We rely on the map being populated from previous benchmarks
        // or the setup state if we were testing a persistent map.
        // Since we are testing the method call itself, we just call it.
        try {
            map.get(new Object());
        } catch (Exception e) {
            // Ignore exceptions if the map state is inconsistent due to JMH threading,
            // though this is generally discouraged in benchmarks.
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test a mutating operation that clears the map
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkFromStatic(Blackhole bh) {
        // Test the static factory method. This is a static call, so it doesn't rely on 'this.map'.
        try {
            // We use empty arrays as inputs for the static method call
            ObjectObjectIdentityHashMap.from(new Object[0], new Object[0]);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}

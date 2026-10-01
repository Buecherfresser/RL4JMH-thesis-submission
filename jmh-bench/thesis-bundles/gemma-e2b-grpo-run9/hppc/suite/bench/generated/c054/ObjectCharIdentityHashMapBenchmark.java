package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectCharIdentityHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectCharIdentityHashMapBenchmark {

    // State field for the map instance.
    private ObjectCharIdentityHashMap<Object> map;

    @Setup
    public void setup() {
        // Initialize a map instance.
        this.map = new ObjectCharIdentityHashMap<>();
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test a read operation.
        try {
            // Use a key that is likely valid or safe for the map implementation.
            bh.consume(map.get(0));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size() operation.
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clear() operation.
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test put operation.
        try {
            // Assuming the library handles the int key and char value correctly.
            map.put(0, (char) 1);
            bh.consume(map);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkGetAfterPut(Blackhole bh) {
        // Test a read operation after a mutation.
        try {
            map.put(0, (char) 1);
            bh.consume(map.get(0));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

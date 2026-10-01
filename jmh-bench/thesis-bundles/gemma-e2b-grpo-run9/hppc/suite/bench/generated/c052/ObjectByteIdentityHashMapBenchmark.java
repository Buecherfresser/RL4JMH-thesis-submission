package bench.generated.c052;

import com.carrotsearch.hppc.ObjectByteIdentityHashMap;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectByteIdentityHashMapBenchmark {

    // State field for the map instance.
    private ObjectByteIdentityHashMap<Object> map;

    @Setup
    public void setup() {
        // Initialize the map.
        this.map = new ObjectByteIdentityHashMap<>();
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Read operation. Must consume the result.
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Read operation.
        try {
            map.get(new Object());
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Write operation (mutating).
        map.put(new Object(), (byte) 0x01);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Mutating operation.
        map.clear();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Test the static factory method.
        try {
            // Create small, temporary arrays for the static call.
            ObjectByteIdentityHashMap<Object> tempMap = ObjectByteIdentityHashMap.from(
                new Object[]{},
                new byte[]{0x00}
            );
            bh.consume(tempMap);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

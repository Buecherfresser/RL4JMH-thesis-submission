package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectLongHashMap;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectLongHashMapBenchmark {

    // State field for the map instance. Since ObjectLongHashMap is mutable,
    // we rely on JMH's isolation between benchmark methods, or ensure
    // operations are idempotent/safe.
    private ObjectLongHashMap<Object> map;

    @Setup
    public void setup() {
        // Initialize a fresh map for each benchmark run if possible,
        // or rely on the state being reset by JMH if we use a new instance
        // inside the benchmark method. For simplicity here, we initialize once.
        this.map = new ObjectLongHashMap<>();
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test a read operation. Since the map is mutable, this read is safe.
        // We use a null key, which should return 0L if not present.
        long result = map.get(null);
        bh.consume(result);
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test a write operation. This modifies the state of 'map'.
        // We use a non-null key and a non-zero value.
        map.put("testKey", 12345L);
        // Consume the return value (which is 0L for successful put)
        bh.consume(map.put("testKey", 99999L));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size calculation.
        bh.consume(map.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation. This modifies the state of 'map'.
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void testStaticFactory(Blackhole bh) {
        // Test the static factory method. This creates and returns a new map.
        try {
            ObjectLongHashMap<Object> newMap = ObjectLongHashMap.from(new Object[0], new long[0]);
            bh.consume(newMap);
        } catch (Exception e) {
            // Ignore exceptions during benchmark if they are expected in edge cases
        }
    }
}

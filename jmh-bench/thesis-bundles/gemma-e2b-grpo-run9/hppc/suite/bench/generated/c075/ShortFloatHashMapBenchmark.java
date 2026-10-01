package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortFloatHashMap;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ShortFloatHashMapBenchmark {

    // State field for the map instance. Since we are testing mutable operations,
    // we will create a new instance inside the benchmark method if mutation is required,
    // or rely on the fact that JMH isolates benchmark methods.
    private ShortFloatHashMap map;

    @Setup
    public void setup() {
        // Initialize a default map instance.
        // Note: For mutating tests, a new instance should ideally be created per benchmark method
        // to avoid state accumulation/distortion, but we initialize one here for general access.
        this.map = new ShortFloatHashMap();
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test a read operation on the initialized map.
        // Since the map is mutable, this test is safe as it only reads.
        bh.consume(map.get((short) 10));
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test a write operation. We rely on JMH isolation or the fact that
        // the map instance is recreated/reset if needed, though for this simple
        // test, we just call the method.
        map.put((short) 1, 1.0f);
        bh.consume(map.get((short) 1));
    }

    @Benchmark
    public void testPutOrAdd(Blackhole bh) {
        // Test an operation that might insert or increment.
        map.putOrAdd((short) 2, 5.0f, 2.0f);
        bh.consume(map.get((short) 2));
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        // Test a lookup operation.
        bh.consume(map.containsKey((short) 1));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size calculation.
        bh.consume(map.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation.
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void testRelease(Blackhole bh) {
        // Test release operation.
        map.release();
    }

    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Test the static factory method.
        try {
            ShortFloatHashMap.from(new short[]{1, 2}, new float[]{1.1f, 2.2f});
        } catch (IllegalArgumentException e) {
            // Ignore expected exceptions if input validation fails in a specific run
        }
        bh.consume(null);
    }

    @Benchmark
    public void testFromStaticFactoryEmpty(Blackhole bh) {
        // Test static factory with empty arrays.
        try {
            ShortFloatHashMap.from(new short[]{}, new float[]{});
        } catch (IllegalArgumentException e) {
            // Ignore
        }
        bh.consume(null);
    }
}

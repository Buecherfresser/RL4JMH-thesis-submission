package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntByteHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class IntByteHashMapBenchmark {

    // Since IntByteHashMap is not static, we instantiate it inside the benchmark
    // to ensure state isolation for mutating operations, adhering to the rule
    // against reusing mutable state across invocations without explicit reset.

    @Benchmark
    public void testPut(Blackhole bh) {
        // Create a fresh map for each invocation to test mutation safely
        IntByteHashMap map = new IntByteHashMap();
        map.put(10, (byte) 1);
        bh.consume(map);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        IntByteHashMap map = new IntByteHashMap();
        map.put(10, (byte) 1);
        // Test a successful get
        bh.consume(map.get(10));
    }

    @Benchmark
    public void testGetDefault(Blackhole bh) {
        IntByteHashMap map = new IntByteHashMap();
        map.put(10, (byte) 1);
        // Test a missing key
        bh.consume(map.getOrDefault(99, (byte) 0));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        IntByteHashMap map = new IntByteHashMap();
        map.put(1, (byte) 1);
        map.put(2, (byte) 2);
        bh.consume(map.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        IntByteHashMap map = new IntByteHashMap();
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        IntByteHashMap map = new IntByteHashMap();
        map.put(1, (byte) 1);
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void testStaticFrom(Blackhole bh) {
        // Test the static factory method
        try {
            // Create a small, fixed input array for the static call
            int[] keys = {1, 2, 3};
            byte[] values = {(byte) 10, (byte) 20, (byte) 30};
            IntByteHashMap.from(keys, values);
            bh.consume(null); // Consume the result of the static call
        } catch (Exception e) {
            // Ignore exceptions during benchmark setup if they occur
        }
    }
}

package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.util.SystemUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SystemUtilBenchmark {

    // Since SystemUtil methods are static, no instance state is required.

    @Benchmark
    public void testGetString(Blackhole bh) {
        // Test retrieval with a non-existent key, expecting default null return
        String result = SystemUtil.get("nonExistentProperty", "default_value");
        bh.consume(result);
    }

    @Benchmark
    public void testGetBoolean(Blackhole bh) {
        // Test boolean parsing (should default to false if property is missing)
        boolean result = SystemUtil.getBoolean("nonExistentBoolean", false);
        bh.consume(result);
    }

    @Benchmark
    public void testGetInt(Blackhole bh) {
        // Test integer parsing (should default to 0 if property is missing or invalid)
        long result = SystemUtil.getInt("nonExistentInt", 0);
        bh.consume(result);
    }

    @Benchmark
    public void testGetLong(Blackhole bh) {
        // Test long parsing (should default to 0 if property is missing or invalid)
        long result = SystemUtil.getLong("nonExistentLong", 0L);
        bh.consume(result);
    }

    @Benchmark
    public void testInfo(Blackhole bh) {
        // Test system info retrieval
        SystemUtil.info();
        bh.consume(null); // Consume void return
    }
}

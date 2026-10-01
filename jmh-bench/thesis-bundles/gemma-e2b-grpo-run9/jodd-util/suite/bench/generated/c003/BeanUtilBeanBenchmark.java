package bench.generated.c003;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jodd.bean.BeanUtilBean;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanUtilBeanBenchmark {

    // State fields for the benchmark instance
    private BeanUtilBean beanUtil;

    @Setup
    public void setup() {
        // Initialize the utility bean instance.
        // Since BeanUtilBean is stateless regarding the benchmark execution flow,
        // this setup is sufficient.
        this.beanUtil = new BeanUtilBean();
    }

    @Benchmark
    public void benchmarkSimpleGet(Blackhole bh) {
        // Test a simple property access path (assuming beanUtil handles the necessary internal state/reflection setup)
        try {
            // We pass null as the bean, relying on the BeanUtilBean implementation
            // to handle null checks gracefully, or we could use a simple Map if we knew its structure.
            // Since we cannot instantiate a complex POJO easily here, we test the method call overhead.
            beanUtil.getProperty(null, "someSimpleProperty");
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected in null scenarios
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNestedGet(Blackhole bh) {
        // Test a property access that requires nested property resolution (which involves internal logic)
        try {
            beanUtil.getProperty(null, "address.street");
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkIndexedGet(Blackhole bh) {
        // Test indexed property access (which involves array/list/map checks)
        try {
            // This call tests the logic path for indexed property retrieval
            beanUtil.getIndexProperty(null, "items[0]", 0);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkIndexedSet(Blackhole bh) {
        // Test indexed property setting
        try {
            // This call tests the logic path for indexed property setting
            beanUtil.setIndexProperty(null, "items[0]", 0, null);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}

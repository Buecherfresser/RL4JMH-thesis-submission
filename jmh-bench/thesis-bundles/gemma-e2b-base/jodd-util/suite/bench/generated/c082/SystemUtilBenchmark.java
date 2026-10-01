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

    // State fields for inputs
    private String propertyName;
    private String defaultValueString;
    private int intDefaultValue;
    private long longDefaultValue;

    @Setup
    public void setup() {
        // Setup inputs for testing SystemUtil methods
        this.propertyName = "test.system.property";
        this.defaultValueString = "default_value";
        this.intDefaultValue = 100;
        this.longDefaultValue = 9999999999L;
    }

    // --- Benchmarks for String Getters ---

    @Benchmark
    public void get_withDefaultString(Blackhole bh) {
        String result = SystemUtil.get(propertyName, defaultValueString);
        bh.consume(result);
    }

    @Benchmark
    public void get_noDefault(Blackhole bh) {
        String result = SystemUtil.get(propertyName);
        bh.consume(result);
    }

    // --- Benchmarks for Boolean Getters ---

    @Benchmark
    public void getBoolean_trueCase(Blackhole bh) {
        // Testing case sensitivity and value mapping for true
        boolean result = SystemUtil.getBoolean("test.bool.flag", false);
        bh.consume(result);
    }

    @Benchmark
    public void getBoolean_falseCase(Blackhole bh) {
        // Testing case sensitivity and value mapping for false
        boolean result = SystemUtil.getBoolean("test.bool.flag", true);
        bh.consume(result);
    }

    @Benchmark
    public void getBoolean_invalidCase(Blackhole bh) {
        // Testing default return for invalid input
        boolean result = SystemUtil.getBoolean("test.bool.flag", true);
        bh.consume(result);
    }

    // --- Benchmarks for Integer Getters ---

    @Benchmark
    public void getInt_valid(Blackhole bh) {
        // Assuming system property is set to a valid integer string for this test
        // If not set, it should return the default value (intDefaultValue)
        long result = SystemUtil.getInt(propertyName, intDefaultValue);
        bh.consume(result);
    }

    @Benchmark
    public void getInt_invalid(Blackhole bh) {
        // Assuming system property is set to a non-integer string
        // It should catch NumberFormatException and return the default value
        long result = SystemUtil.getInt("non_integer_prop", intDefaultValue);
        bh.consume(result);
    }

    // --- Benchmarks for Long Getters ---

    @Benchmark
    public void getLong_valid(Blackhole bh) {
        // Assuming system property is set to a valid long string for this test
        // If not set, it should return the default value (longDefaultValue)
        long result = SystemUtil.getLong(propertyName, longDefaultValue);
        bh.consume(result);
    }

    @Benchmark
    public void getLong_invalid(Blackhole bh) {
        // Assuming system property is set to a non-long string
        // It should catch NumberFormatException and return the default value
        long result = SystemUtil.getLong("non_long_prop", longDefaultValue);
        bh.consume(result);
    }

    // --- Benchmark for Info Method ---

    @Benchmark
    public void info_call(Blackhole bh) {
        SystemUtil.info();
        // Since info() returns a static object, we consume it to prevent dead code elimination
        bh.consume(SystemUtil.info());
    }
}

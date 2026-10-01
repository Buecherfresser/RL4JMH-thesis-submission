package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BooleanConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BooleanConverterBenchmark {

    private BooleanConverter converter;

    // Inputs for TRUE conversion
    private Object trueStringInput;
    private Object trueBooleanInput;

    // Inputs for FALSE conversion
    private Object falseStringInput;
    private Object falseBooleanInput;

    // Inputs for edge cases
    private Object emptyStringInput;
    private Object nullInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new BooleanConverter();

        // TRUE cases
        trueStringInput = " Yes "; // Test trimming and case insensitivity
        trueBooleanInput = Boolean.TRUE;

        // FALSE cases
        falseStringInput = " No "; // Test trimming and case insensitivity
        falseBooleanInput = Boolean.FALSE;

        // Edge cases
        emptyStringInput = "";
        nullInput = null;
    }

    @Benchmark
    public void convert_TrueString(Blackhole bh) {
        Boolean result = converter.convert(trueStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_FalseString(Blackhole bh) {
        Boolean result = converter.convert(falseStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_EmptyString(Blackhole bh) {
        Boolean result = converter.convert(emptyStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_NullInput(Blackhole bh) {
        Boolean result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_BooleanTrue(Blackhole bh) {
        Boolean result = converter.convert(trueBooleanInput);
        bh.consume(result);
    }

    @Benchmark
    public void convert_BooleanFalse(Blackhole bh) {
        Boolean result = converter.convert(falseBooleanInput);
        bh.consume(result);
    }
}

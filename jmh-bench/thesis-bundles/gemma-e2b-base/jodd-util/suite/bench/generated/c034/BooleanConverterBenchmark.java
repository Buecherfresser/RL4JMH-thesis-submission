package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BooleanConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BooleanConverterBenchmark {

    private BooleanConverter converter;

    // Inputs for testing different conversion paths
    private Object nullInput;
    private Boolean trueBooleanInput;
    private Boolean falseBooleanInput;
    private String trueStringInput;
    private String falseStringInput;
    private String exceptionStringInput;

    @Setup
    public void setup() {
        converter = new BooleanConverter();

        nullInput = null;
        trueBooleanInput = Boolean.TRUE;
        falseBooleanInput = Boolean.FALSE;
        trueStringInput = "YES";
        falseStringInput = "no";
        exceptionStringInput = "maybe";
    }

    @Benchmark
    public void testNullConversion(Blackhole bh) {
        Boolean result = converter.convert(nullInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanTrueConversion(Blackhole bh) {
        Boolean result = converter.convert(trueBooleanInput);
        bh.consume(result);
    }

    @Benchmark
    public void testBooleanFalseConversion(Blackhole bh) {
        Boolean result = converter.convert(falseBooleanInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringTrueConversion(Blackhole bh) {
        Boolean result = converter.convert(trueStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringFalseConversion(Blackhole bh) {
        Boolean result = converter.convert(falseStringInput);
        bh.consume(result);
    }

    @Benchmark
    public void testStringExceptionConversion(Blackhole bh) {
        // This path should throw TypeConversionException
        try {
            converter.convert(exceptionStringInput);
        } catch (TypeConversionException e) {
            // Expected behavior, consume the exception or just ensure the path is hit
        }
    }
}

package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.impl.IntegerConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerConverterBenchmark {

    private IntegerConverter converter;

    // Test inputs for string conversion path
    private final String standardPositiveString = "12345";
    private final String standardNegativeString = "-9876";
    private final String stringWithWhitespace = "  42  ";
    private final String stringStartingWithPlus = "+500";
    private final String nullString = null;
    private final String invalidString = "abc";

    // Test inputs for Number path
    private final Double doubleValue = 3.14159;
    private final Long longValue = 987654321L;
    private final Boolean trueBoolean = true;
    private final Boolean falseBoolean = false;

    @Setup
    public void setup() {
        converter = new IntegerConverter();
    }

    @Benchmark
    public void convert_StandardPositiveString(Blackhole bh) {
        Integer result = converter.convert(standardPositiveString);
        bh.consume(result);
    }

    @Benchmark
    public void convert_StandardNegativeString(Blackhole bh) {
        Integer result = converter.convert(standardNegativeString);
        bh.consume(result);
    }

    @Benchmark
    public void convert_StringWithWhitespace(Blackhole bh) {
        Integer result = converter.convert(stringWithWhitespace);
        bh.consume(result);
    }

    @Benchmark
    public void convert_StringStartingWithPlus(Blackhole bh) {
        Integer result = converter.convert(stringStartingWithPlus);
        bh.consume(result);
    }

    @Benchmark
    public void convert_NullInput(Blackhole bh) {
        Integer result = converter.convert(nullString);
        bh.consume(result);
    }

    @Benchmark
    public void convert_DoubleInput(Blackhole bh) {
        Integer result = converter.convert(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void convert_LongInput(Blackhole bh) {
        Integer result = converter.convert(longValue);
        bh.consume(result);
    }

    @Benchmark
    public void convert_BooleanTrue(Blackhole bh) {
        Integer result = converter.convert(trueBoolean);
        bh.consume(result);
    }

    @Benchmark
    public void convert_BooleanFalse(Blackhole bh) {
        Integer result = converter.convert(falseBoolean);
        bh.consume(result);
    }

    @Benchmark
    public void convert_InvalidString(Blackhole bh) {
        // This test is expected to throw TypeConversionException
        try {
            converter.convert(invalidString);
        } catch (TypeConversionException e) {
            // Consume the exception or just let the benchmark finish
        }
    }
}

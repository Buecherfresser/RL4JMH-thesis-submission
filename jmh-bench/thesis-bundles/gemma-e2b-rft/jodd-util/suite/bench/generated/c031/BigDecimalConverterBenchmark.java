package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jodd.typeconverter.impl.BigDecimalConverter;
import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigDecimalConverterBenchmark {

    private BigDecimalConverter converter;
    private BigDecimal existingBigDecimal;

    @Setup
    public void setup() {
        converter = new BigDecimalConverter();

        // Setup inputs for testing
        existingBigDecimal = new BigDecimal("500.75");
    }

    @Benchmark
    public void testNullConversion(Blackhole bh) {
        BigDecimal result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void testExistingBigDecimalConversion(Blackhole bh) {
        BigDecimal result = converter.convert(existingBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void testStandardStringConversion(Blackhole bh) {
        String input = "123.45";
        BigDecimal result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void testStringWithWhitespaceTrimming(Blackhole bh) {
        String input = "  -987.65  ";
        BigDecimal result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void testLargeNumberStringConversion(Blackhole bh) {
        String input = "12345678901234567890.12345678901234567890";
        BigDecimal result = converter.convert(input);
        bh.consume(result);
    }

    @Benchmark
    public void testInvalidStringConversion(Blackhole bh) {
        // This test verifies the exception path.
        String input = "not_a_number";
        try {
            converter.convert(input);
        } catch (TypeConversionException e) {
            // Consume null as the result of the successful path is not applicable here.
            bh.consume(null);
        }
    }
}

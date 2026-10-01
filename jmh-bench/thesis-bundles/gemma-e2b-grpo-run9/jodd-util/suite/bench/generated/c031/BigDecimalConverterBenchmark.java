package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.BigDecimalConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigDecimalConverterBenchmark {

    // Since BigDecimalConverter is stateless, we can instantiate it once.
    private BigDecimalConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance.
        this.converter = new BigDecimalConverter();
    }

    @Benchmark
    public void convertNull(Blackhole bh) {
        // Test null handling path
        try {
            BigDecimal result = converter.convert(null);
            // Consume result to prevent dead code elimination
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for this specific null test if they occur unexpectedly
        }
    }

    @Benchmark
    public BigDecimal convertBigDecimal(Blackhole bh) {
        // Test existing BigDecimal path
        BigDecimal input = new BigDecimal("123.45");
        try {
            BigDecimal result = converter.convert(input);
            bh.consume(result);
            return result;
        } catch (Exception e) {
            // Should not happen for valid BigDecimal input
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public void convertStringToBigDecimal(Blackhole bh) {
        // Test the conversion path requiring string parsing and BigDecimal creation
        String input = "9876543210.123456789";
        try {
            BigDecimal result = converter.convert(input);
            bh.consume(result);
        } catch (Exception e) {
            // Catch potential TypeConversionException if input is malformed
        }
    }
}

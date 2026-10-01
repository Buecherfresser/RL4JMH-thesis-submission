package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.DoubleConverter;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleConverterBenchmark {

    private DoubleConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter instance. Since it's stateless, this is fine.
        this.converter = new DoubleConverter();
    }

    @Benchmark
    public Double convertNull(Blackhole bh) {
        // Test case: null input
        Double result = converter.convert(null);
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Double convertDouble(Blackhole bh) {
        // Test case: actual Double object
        Double input = 123.45;
        Double result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double convertInteger(Blackhole bh) {
        // Test case: Integer (Number type)
        Integer input = 42;
        Double result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double convertBoolean(Blackhole bh) {
        // Test case: Boolean (should convert to 1.0 or 0.0)
        Boolean input = true;
        Double result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double convertPositiveString(Blackhole bh) {
        // Test case: String convertible to Double (positive, no sign)
        String input = "123.45";
        Double result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double convertNegativeString(Blackhole bh) {
        // Test case: String convertible to Double (negative, leading minus sign)
        String input = "-98.76";
        Double result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Double convertStringWithPlus(Blackhole bh) {
        // Test case: String starting with plus sign (should strip it)
        String input = "+10.0";
        Double result = converter.convert(input);
        bh.consume(result);
        return result;
    }
}

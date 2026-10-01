package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.FloatConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatConverterBenchmark {

    private FloatConverter converter;

    @Setup
    public void setup() {
        // Initialize the converter once per benchmark run
        this.converter = new FloatConverter();
    }

    @Benchmark
    public Float convertNull(Blackhole bh) {
        // Test case: null input
        Float result = converter.convert(null);
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Float convertFloat(Blackhole bh) {
        // Test case: Float input
        Float input = 3.14f;
        Float result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Float convertInteger(Blackhole bh) {
        // Test case: Integer input (Number)
        Integer input = 100;
        Float result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Float convertBoolean(Blackhole bh) {
        // Test case: Boolean input (true -> 1.0f, false -> 0.0f)
        Boolean input = true;
        Float result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Float convertStringValid(Blackhole bh) {
        // Test case: String input that converts cleanly (" 123.45 ")
        String input = " 123.45 ";
        Float result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Float convertStringInvalid(Blackhole bh) {
        // Test case: String input that causes NumberFormatException
        String input = "not a float";
        try {
            converter.convert(input);
        } catch (TypeConversionException e) {
            // Expected path
        }
        // Consume null result or lack thereof
        bh.consume(null);
        return null;
    }
}

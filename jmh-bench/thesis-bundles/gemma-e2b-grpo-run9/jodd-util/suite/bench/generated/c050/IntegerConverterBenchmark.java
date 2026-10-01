package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
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

    @Setup
    public void setup() {
        // Initialize the converter instance. Since it appears stateless, this is safe.
        this.converter = new IntegerConverter();
    }

    @Benchmark
    public IntegerConverter benchmarkNull(Blackhole bh) {
        // Test case 1: null input
        try {
            Integer result = converter.convert(null);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for null case if they occur unexpectedly
        }
        return null;
    }

    @Benchmark
    public IntegerConverter benchmarkInteger(Blackhole bh) {
        // Test case 2: Integer input
        try {
            Integer result = converter.convert(12345);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public IntegerConverter benchmarkNumber(Blackhole bh) {
        // Test case 3: Number input (Double)
        try {
            Integer result = converter.convert(123.45);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public IntegerConverter benchmarkBooleanTrue(Blackhole bh) {
        // Test case 4a: Boolean true
        try {
            Integer result = converter.convert(true);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public IntegerConverter benchmarkBooleanFalse(Blackhole bh) {
        // Test case 4b: Boolean false
        try {
            Integer result = converter.convert(false);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
        return null;
    }

    @Benchmark
    public void benchmarkStringPositive(Blackhole bh) {
        // Test case 5a: Simple positive string
        try {
            converter.convert("12345");
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkStringNegative(Blackhole bh) {
        // Test case 5b: Negative string
        try {
            converter.convert("-987");
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkStringWithWhitespace(Blackhole bh) {
        // Test case 5c: String with whitespace
        try {
            converter.convert("  456  ");
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkStringWithPlusSign(Blackhole bh) {
        // Test case 5d: String starting with plus sign
        try {
            converter.convert("+100");
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkStringNonInteger(Blackhole bh) {
        // Test case 6: Input that should throw NumberFormatException
        try {
            converter.convert("abc");
        } catch (TypeConversionException e) {
            // Expected exception path
        } catch (Exception e) {
            // Catching broader exceptions if TypeConversionException isn't thrown directly
        }
    }
}

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

    // The subject under test
    private BooleanConverter converter;

    @Setup
    public void setup() {
        this.converter = new BooleanConverter();
    }

    @Benchmark
    public void testConvertNull(Blackhole bh) {
        // Test case: null input
        Boolean result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertBooleanTrue(Blackhole bh) {
        // Test case: actual Boolean.TRUE
        Boolean result = converter.convert(Boolean.TRUE);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertBooleanFalse(Blackhole bh) {
        // Test case: actual Boolean.FALSE
        Boolean result = converter.convert(Boolean.FALSE);
        bh.consume(result);
    }

    @Benchmark
    public void testConvertStringTrue(Blackhole bh) {
        // Test case: string that converts to true ("yes")
        Boolean result = converter.convert("yes");
        bh.consume(result);
    }

    @Benchmark
    public void testConvertStringTrueCaseInsensitive(Blackhole bh) {
        // Test case: string that converts to true ("TRUE")
        Boolean result = converter.convert("TRUE");
        bh.consume(result);
    }

    @Benchmark
    public void testConvertStringTrueWithWhitespace(Blackhole bh) {
        // Test case: string with whitespace ("  on  ")
        Boolean result = converter.convert("  on  ");
        bh.consume(result);
    }

    @Benchmark
    public void testConvertStringFalse(Blackhole bh) {
        // Test case: string that converts to false ("no")
        Boolean result = converter.convert("no");
        bh.consume(result);
    }

    @Benchmark
    public void testConvertStringFalseCaseInsensitive(Blackhole bh) {
        // Test case: string that converts to false ("off")
        Boolean result = converter.convert("OFF");
        bh.consume(result);
    }

    @Benchmark
    public void testConvertInvalidString(Blackhole bh) {
        // Test case: string that should throw TypeConversionException
        try {
            converter.convert("maybe");
        } catch (TypeConversionException e) {
            // Expected exception
        }
        bh.consume(null);
    }
}

package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.impl.BigIntegerConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigIntegerConverterBenchmark {

    private BigIntegerConverter converter;

    // Inputs for testing different branches of the convert method
    private BigInteger bigIntValue;
    private Long longValue;
    private String validBigIntegerString;
    private String invalidBigIntegerString;

    @Setup
    public void setup() {
        converter = new BigIntegerConverter();

        // Case 1: BigInteger input
        bigIntValue = new BigInteger("12345678901234567890");

        // Case 2: Number input (Long)
        longValue = 9876543210L;

        // Case 3: String input (Valid)
        validBigIntegerString = "9876543210";

        // Case 4: String input (Invalid, to test exception path)
        invalidBigIntegerString = "not_a_big_integer";
    }

    @Benchmark
    public void convert_BigInteger(Blackhole bh) {
        BigInteger result = converter.convert(bigIntValue);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Long(Blackhole bh) {
        BigInteger result = converter.convert(longValue);
        bh.consume(result);
    }

    @Benchmark
    public void convert_ValidString(Blackhole bh) {
        BigInteger result = converter.convert(validBigIntegerString);
        bh.consume(result);
    }

    @Benchmark
    public void convert_InvalidString(Blackhole bh) {
        try {
            converter.convert(invalidBigIntegerString);
        } catch (TypeConversionException e) {
            // Exception caught, but we must consume something as this is a void benchmark.
        }
        bh.consume(null);
    }
}

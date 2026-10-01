package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BigIntegerConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigIntegerConverterBenchmark {

    private BigIntegerConverter converter;

    @Setup
    public void setup() {
        this.converter = new BigIntegerConverter();
    }

    @Benchmark
    public void testConvertNull(Blackhole bh) {
        // Test case 1: null input
        BigInteger result = converter.convert(null);
        bh.consume(result);
    }

    @Benchmark
    public BigInteger testConvertBigInteger(Blackhole bh) {
        // Test case 2: BigInteger input
        BigInteger input = new BigInteger("12345678901234567890");
        BigInteger result = converter.convert(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public BigInteger testConvertLong(Blackhole bh) {
        // Test case 3: Number input (Long)
        // The implementation uses longValue(), so we test a long value.
        Long inputLong = 9876543210L;
        BigInteger result = converter.convert(inputLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public BigInteger testConvertString(Blackhole bh) {
        // Test case 4: String input requiring parsing
        // Use a string that represents a large number.
        String inputString = "123456789012345678901234567890";
        try {
            BigInteger result = converter.convert(inputString);
            bh.consume(result);
        } catch (Exception e) {
            // Catch potential TypeConversionException if the string is malformed,
            // though this specific string should succeed.
            bh.consume(null);
        }
        return null;
    }
}

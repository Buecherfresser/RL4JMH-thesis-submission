package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BigIntegerConverter;
import java.math.BigInteger;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Double;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigIntegerConverterBenchmark {

    private BigIntegerConverter converter;

    // Inputs for testing different conversion paths
    private BigInteger bigIntegerInput;
    private Long longInput;
    private Integer integerInput;
    private Double doubleInput;
    private String stringInputValid;
    private String stringInputTrimmed;

    @Setup(Level.Trial)
    public void setup() {
        converter = new BigIntegerConverter();

        // 1. BigInteger input (identity check)
        bigIntegerInput = new BigInteger("123456789012345678901234567890");

        // 2. Number inputs
        longInput = 9876543210L;
        integerInput = 12345;
        // Note: Double conversion relies on longValue(), which truncates.
        doubleInput = 123456789.99;

        // 3. String inputs
        stringInputValid = "98765432101234567890";
        stringInputTrimmed = "  12345  ";
    }

    @Benchmark
    public void convertFromBigInteger(Blackhole bh) {
        BigInteger result = converter.convert(bigIntegerInput);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromLong(Blackhole bh) {
        BigInteger result = converter.convert(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromInteger(Blackhole bh) {
        BigInteger result = converter.convert(integerInput);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromDouble(Blackhole bh) {
        // Testing conversion from Double, which uses longValue()
        BigInteger result = converter.convert(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromStringValid(Blackhole bh) {
        BigInteger result = converter.convert(stringInputValid);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromStringTrimmed(Blackhole bh) {
        // Testing conversion from String that requires trimming
        BigInteger result = converter.convert(stringInputTrimmed);
        bh.consume(result);
    }

    @Benchmark
    public void convertFromNull(Blackhole bh) {
        // Testing null input
        BigInteger result = converter.convert(null);
        bh.consume(result);
    }
}

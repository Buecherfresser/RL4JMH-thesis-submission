package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.lang.Long;
import java.util.concurrent.TimeUnit;

import jodd.typeconverter.TypeConversionException;
import jodd.typeconverter.TypeConverter;
import jodd.typeconverter.impl.BigIntegerConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigIntegerConverterBenchmark {

    private BigIntegerConverter converter;
    private BigInteger bigIntValue;
    private Long longValue;
    private String largeNumberString;

    @Setup
    public void setup() {
        converter = new BigIntegerConverter();
        
        // Setup inputs
        bigIntValue = new BigInteger("12345678901234567890");
        longValue = 9876543210L;
        largeNumberString = "9223372036854775807"; // Max Long value as string
    }

    @Benchmark
    public void convert_BigInteger(Blackhole bh) {
        BigInteger result = converter.convert(bigIntValue);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Long(Blackhole bh) {
        // Input is a Number (Long), should use longValue() path
        BigInteger result = converter.convert(longValue);
        bh.consume(result);
    }

    @Benchmark
    public void convert_LargeString(Blackhole bh) {
        // Input is a String, should use new BigInteger(String) path
        BigInteger result = converter.convert(largeNumberString);
        bh.consume(result);
    }

    @Benchmark
    public void convert_Null(Blackhole bh) {
        // Test null handling path
        BigInteger result = converter.convert(null);
        bh.consume(result);
    }
}

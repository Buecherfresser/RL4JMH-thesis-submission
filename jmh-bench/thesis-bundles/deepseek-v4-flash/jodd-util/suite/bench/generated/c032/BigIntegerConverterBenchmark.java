package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.math.BigInteger;
import jodd.typeconverter.impl.BigIntegerConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigIntegerConverterBenchmark {

    private BigIntegerConverter converter;
    private BigInteger bigInteger;
    private Integer integer;
    private Long longValue;
    private Double doubleValue;
    private String stringValue;
    private String stringWithSpaces;

    @Setup(Level.Trial)
    public void setup() {
        converter = new BigIntegerConverter();
        bigInteger = new BigInteger("123456789012345678901234567890");
        integer = 123456789;
        longValue = 1234567890123456789L;
        doubleValue = 1234567890.123456789;
        stringValue = "123456789012345678901234567890";
        stringWithSpaces = "  123456789012345678901234567890  ";
    }

    @Benchmark
    public BigInteger convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public BigInteger convertBigInteger() {
        return converter.convert(bigInteger);
    }

    @Benchmark
    public BigInteger convertInteger() {
        return converter.convert(integer);
    }

    @Benchmark
    public BigInteger convertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public BigInteger convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public BigInteger convertString() {
        return converter.convert(stringValue);
    }

    @Benchmark
    public BigInteger convertStringTrimmed() {
        return converter.convert(stringWithSpaces);
    }
}

package bench.generated.c032;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
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
    private BigInteger bigIntInput;
    private Long longInput;
    private String stringInput;
    private String stringWithSpaceInput;
    private Object nullInput;

    @Setup(Level.Trial)
    public void setup() {
        converter = new BigIntegerConverter();
        bigIntInput = new BigInteger("12345678901234567890");
        longInput = Long.valueOf(123456789L);
        stringInput = "98765432109876543210";
        stringWithSpaceInput = "   11223344556677889900   ";
        nullInput = null;
    }

    @Benchmark
    public BigInteger convertFromBigInteger() {
        return converter.convert(bigIntInput);
    }

    @Benchmark
    public BigInteger convertFromLong() {
        return converter.convert(longInput);
    }

    @Benchmark
    public BigInteger convertFromString() {
        return converter.convert(stringInput);
    }

    @Benchmark
    public BigInteger convertFromStringWithWhitespace() {
        return converter.convert(stringWithSpaceInput);
    }

    @Benchmark
    public BigInteger convertFromNull() {
        return converter.convert(nullInput);
    }
}

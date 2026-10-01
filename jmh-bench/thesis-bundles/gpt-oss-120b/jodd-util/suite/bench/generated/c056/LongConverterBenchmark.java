package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.LongConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongConverterBenchmark {

    private LongConverter converter;

    private Object nullValue;
    private Long longValue;
    private Integer intValue;
    private Double doubleValue;
    private Boolean trueBool;
    private Boolean falseBool;
    private String plusString;
    private String whitespaceString;
    private String negativeString;
    private String zeroString;
    private String invalidString;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new LongConverter();

        nullValue = null;
        longValue = Long.valueOf(123456789L);
        intValue = Integer.valueOf(42);
        doubleValue = Double.valueOf(3.14159);
        trueBool = Boolean.TRUE;
        falseBool = Boolean.FALSE;
        plusString = "+987654321";
        whitespaceString = "   55555   ";
        negativeString = "-7777777";
        zeroString = "0";
        invalidString = "not-a-number";
    }

    @Benchmark
    public Long benchmarkConvertNull() {
        return converter.convert(nullValue);
    }

    @Benchmark
    public Long benchmarkConvertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public Long benchmarkConvertInteger() {
        return converter.convert(intValue);
    }

    @Benchmark
    public Long benchmarkConvertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public Long benchmarkConvertBooleanTrue() {
        return converter.convert(trueBool);
    }

    @Benchmark
    public Long benchmarkConvertBooleanFalse() {
        return converter.convert(falseBool);
    }

    @Benchmark
    public Long benchmarkConvertStringPlus() {
        return converter.convert(plusString);
    }

    @Benchmark
    public Long benchmarkConvertStringWhitespace() {
        return converter.convert(whitespaceString);
    }

    @Benchmark
    public Long benchmarkConvertStringNegative() {
        return converter.convert(negativeString);
    }

    @Benchmark
    public Long benchmarkConvertStringZero() {
        return converter.convert(zeroString);
    }

    @Benchmark
    public Long benchmarkConvertInvalidString(Blackhole bh) {
        try {
            return converter.convert(invalidString);
        } catch (TypeConversionException ex) {
            // consume the exception to avoid dead-code elimination
            bh.consume(ex);
            return null;
        }
    }
}

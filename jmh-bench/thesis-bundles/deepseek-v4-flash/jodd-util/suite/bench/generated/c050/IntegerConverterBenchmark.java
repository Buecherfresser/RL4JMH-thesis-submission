package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.IntegerConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntegerConverterBenchmark {

    private IntegerConverter converter;
    private Integer intValue;
    private Long longValue;
    private Double doubleValue;
    private Boolean boolTrue;
    private Boolean boolFalse;
    private String strPositive;
    private String strNegative;
    private String strWithPlus;
    private String strWithWhitespace;
    private String strNull;

    @Setup(Level.Trial)
    public void setup() {
        converter = new IntegerConverter();
        intValue = 42;
        longValue = 123456789L;
        doubleValue = 3.14;
        boolTrue = Boolean.TRUE;
        boolFalse = Boolean.FALSE;
        strPositive = "12345";
        strNegative = "-6789";
        strWithPlus = "+1000";
        strWithWhitespace = "  999  ";
        strNull = null;
    }

    @Benchmark
    public Integer convertInteger() {
        return converter.convert(intValue);
    }

    @Benchmark
    public Integer convertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public Integer convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public Integer convertBooleanTrue() {
        return converter.convert(boolTrue);
    }

    @Benchmark
    public Integer convertBooleanFalse() {
        return converter.convert(boolFalse);
    }

    @Benchmark
    public Integer convertStringPositive() {
        return converter.convert(strPositive);
    }

    @Benchmark
    public Integer convertStringNegative() {
        return converter.convert(strNegative);
    }

    @Benchmark
    public Integer convertStringWithPlus() {
        return converter.convert(strWithPlus);
    }

    @Benchmark
    public Integer convertStringWithWhitespace() {
        return converter.convert(strWithWhitespace);
    }

    @Benchmark
    public Integer convertNull() {
        return converter.convert(strNull);
    }
}

package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ShortConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortConverterBenchmark {

    private ShortConverter converter;
    private Object nullInput;
    private Short shortInput;
    private Integer intInput;
    private Long longInput;
    private Double doubleInput;
    private Boolean trueBool;
    private Boolean falseBool;
    private String plusString;
    private String plainString;
    private String whitespaceString;
    private String negativeString;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new ShortConverter();
        nullInput = null;
        shortInput = Short.valueOf((short) 123);
        intInput = Integer.valueOf(12345);
        longInput = Long.valueOf(12345L);
        doubleInput = Double.valueOf(12345.67);
        trueBool = Boolean.TRUE;
        falseBool = Boolean.FALSE;
        plusString = "+123";
        plainString = "123";
        whitespaceString = "   456   ";
        negativeString = "-789";
    }

    @Benchmark
    public Short convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public Short convertShort() {
        return converter.convert(shortInput);
    }

    @Benchmark
    public Short convertInteger() {
        return converter.convert(intInput);
    }

    @Benchmark
    public Short convertLong() {
        return converter.convert(longInput);
    }

    @Benchmark
    public Short convertDouble() {
        return converter.convert(doubleInput);
    }

    @Benchmark
    public Short convertTrueBoolean() {
        return converter.convert(trueBool);
    }

    @Benchmark
    public Short convertFalseBoolean() {
        return converter.convert(falseBool);
    }

    @Benchmark
    public Short convertPlusString() {
        return converter.convert(plusString);
    }

    @Benchmark
    public Short convertPlainString() {
        return converter.convert(plainString);
    }

    @Benchmark
    public Short convertWhitespaceString() {
        return converter.convert(whitespaceString);
    }

    @Benchmark
    public Short convertNegativeString() {
        return converter.convert(negativeString);
    }
}

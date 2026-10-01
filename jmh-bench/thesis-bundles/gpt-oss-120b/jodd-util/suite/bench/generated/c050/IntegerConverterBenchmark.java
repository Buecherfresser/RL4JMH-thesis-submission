package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
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

    // inputs
    private Object nullInput;
    private Integer integerInput;
    private Long longInput;
    private Boolean trueBoolean;
    private Boolean falseBoolean;
    private String plusString;
    private String plainString;
    private String whitespaceString;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new IntegerConverter();

        nullInput = null;
        integerInput = Integer.valueOf(42);
        longInput = Long.valueOf(123456789L);
        trueBoolean = Boolean.TRUE;
        falseBoolean = Boolean.FALSE;
        plusString = "+987";
        plainString = "654";
        whitespaceString = "   321   ";
    }

    @Benchmark
    public Integer convertNull() {
        return converter.convert(nullInput);
    }

    @Benchmark
    public Integer convertInteger() {
        return converter.convert(integerInput);
    }

    @Benchmark
    public Integer convertLongNumber() {
        return converter.convert(longInput);
    }

    @Benchmark
    public Integer convertTrueBoolean() {
        return converter.convert(trueBoolean);
    }

    @Benchmark
    public Integer convertFalseBoolean() {
        return converter.convert(falseBoolean);
    }

    @Benchmark
    public Integer convertPlusString() {
        return converter.convert(plusString);
    }

    @Benchmark
    public Integer convertPlainString() {
        return converter.convert(plainString);
    }

    @Benchmark
    public Integer convertWhitespaceString() {
        return converter.convert(whitespaceString);
    }
}

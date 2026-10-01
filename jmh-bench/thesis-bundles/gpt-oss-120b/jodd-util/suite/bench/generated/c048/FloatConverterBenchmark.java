package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.FloatConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatConverterBenchmark {

    private FloatConverter converter;

    private Object nullValue;
    private Float floatValue;
    private Integer intValue;
    private Double doubleValue;
    private Boolean boolTrue;
    private Boolean boolFalse;
    private String plainString;
    private String plusString;
    private String whitespaceString;

    @Setup(Level.Trial)
    public void setup() {
        converter = new FloatConverter();

        nullValue = null;
        floatValue = Float.valueOf(1.23f);
        intValue = Integer.valueOf(42);
        doubleValue = Double.valueOf(3.14159);
        boolTrue = Boolean.TRUE;
        boolFalse = Boolean.FALSE;
        plainString = "2.71828";
        plusString = "+9.81";
        whitespaceString = "   6.022e23   ";
    }

    @Benchmark
    public Float convertNull() {
        return converter.convert(nullValue);
    }

    @Benchmark
    public Float convertFloat() {
        return converter.convert(floatValue);
    }

    @Benchmark
    public Float convertInteger() {
        return converter.convert(intValue);
    }

    @Benchmark
    public Float convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public Float convertBooleanTrue() {
        return converter.convert(boolTrue);
    }

    @Benchmark
    public Float convertBooleanFalse() {
        return converter.convert(boolFalse);
    }

    @Benchmark
    public Float convertStringPlain() {
        return converter.convert(plainString);
    }

    @Benchmark
    public Float convertStringPlus() {
        return converter.convert(plusString);
    }

    @Benchmark
    public Float convertStringWhitespace() {
        return converter.convert(whitespaceString);
    }
}

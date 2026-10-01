package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ByteConverter;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteConverterBenchmark {

    private ByteConverter converter;

    private Object nullObj;
    private Byte byteObj;
    private Integer intObj;
    private Boolean trueBool;
    private Boolean falseBool;
    private String plusString;
    private String plainString;
    private String negativeString;
    private String whitespaceString;

    @Setup(Level.Trial)
    public void setUp() {
        converter = new ByteConverter();

        nullObj = null;
        byteObj = Byte.valueOf((byte) 7);
        intObj = Integer.valueOf(123);
        trueBool = Boolean.TRUE;
        falseBool = Boolean.FALSE;
        plusString = "+42";
        plainString = "55";
        negativeString = "-8";
        whitespaceString = "   9   ";
    }

    @Benchmark
    public Byte convertNull() {
        return converter.convert(nullObj);
    }

    @Benchmark
    public Byte convertByte() {
        return converter.convert(byteObj);
    }

    @Benchmark
    public Byte convertInteger() {
        return converter.convert(intObj);
    }

    @Benchmark
    public Byte convertBooleanTrue() {
        return converter.convert(trueBool);
    }

    @Benchmark
    public Byte convertBooleanFalse() {
        return converter.convert(falseBool);
    }

    @Benchmark
    public Byte convertStringPlus() {
        return converter.convert(plusString);
    }

    @Benchmark
    public Byte convertStringPlain() {
        return converter.convert(plainString);
    }

    @Benchmark
    public Byte convertStringNegative() {
        return converter.convert(negativeString);
    }

    @Benchmark
    public Byte convertStringWhitespace() {
        return converter.convert(whitespaceString);
    }
}

package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
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
    private Byte byteValue;
    private Integer intValue;
    private Long longValue;
    private Double doubleValue;
    private Boolean boolTrue;
    private Boolean boolFalse;
    private String stringNumber;
    private String stringPlus;
    private String stringSpaces;
    private String stringNegative;

    @Setup(Level.Trial)
    public void setup() {
        converter = new ByteConverter();
        byteValue = (byte) 42;
        intValue = 123;
        longValue = 456L;
        doubleValue = 78.9;
        boolTrue = Boolean.TRUE;
        boolFalse = Boolean.FALSE;
        stringNumber = "123";
        stringPlus = "+123";
        stringSpaces = "  123  ";
        stringNegative = "-123";
    }

    @Benchmark
    public Byte convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Byte convertByte() {
        return converter.convert(byteValue);
    }

    @Benchmark
    public Byte convertInteger() {
        return converter.convert(intValue);
    }

    @Benchmark
    public Byte convertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public Byte convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public Byte convertBooleanTrue() {
        return converter.convert(boolTrue);
    }

    @Benchmark
    public Byte convertBooleanFalse() {
        return converter.convert(boolFalse);
    }

    @Benchmark
    public Byte convertStringNumber() {
        return converter.convert(stringNumber);
    }

    @Benchmark
    public Byte convertStringPlus() {
        return converter.convert(stringPlus);
    }

    @Benchmark
    public Byte convertStringSpaces() {
        return converter.convert(stringSpaces);
    }

    @Benchmark
    public Byte convertStringNegative() {
        return converter.convert(stringNegative);
    }
}

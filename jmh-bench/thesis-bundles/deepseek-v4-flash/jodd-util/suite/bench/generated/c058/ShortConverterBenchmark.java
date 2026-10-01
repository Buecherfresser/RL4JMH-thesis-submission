package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.ShortConverter;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortConverterBenchmark {

    private ShortConverter converter;
    private Short shortValue;
    private Integer intValue;
    private Long longValue;
    private Float floatValue;
    private Double doubleValue;
    private Byte byteValue;
    private Boolean boolTrue;
    private Boolean boolFalse;
    private String stringNumber;
    private String stringPlus;
    private String stringNegative;
    private String stringTrimmed;
    private String invalidString;

    @Setup(Level.Trial)
    public void setup() {
        converter = new ShortConverter();
        shortValue = Short.valueOf((short) 12345);
        intValue = Integer.valueOf(12345);
        longValue = Long.valueOf(12345L);
        floatValue = Float.valueOf(12345.0f);
        doubleValue = Double.valueOf(12345.0);
        byteValue = Byte.valueOf((byte) 123);
        boolTrue = Boolean.TRUE;
        boolFalse = Boolean.FALSE;
        stringNumber = "12345";
        stringPlus = "+12345";
        stringNegative = "-12345";
        stringTrimmed = "  12345  ";
        invalidString = "not-a-number";
    }

    @Benchmark
    public Short convertShort() {
        return converter.convert(shortValue);
    }

    @Benchmark
    public Short convertInteger() {
        return converter.convert(intValue);
    }

    @Benchmark
    public Short convertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public Short convertFloat() {
        return converter.convert(floatValue);
    }

    @Benchmark
    public Short convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public Short convertByte() {
        return converter.convert(byteValue);
    }

    @Benchmark
    public Short convertBooleanTrue() {
        return converter.convert(boolTrue);
    }

    @Benchmark
    public Short convertBooleanFalse() {
        return converter.convert(boolFalse);
    }

    @Benchmark
    public Short convertString() {
        return converter.convert(stringNumber);
    }

    @Benchmark
    public Short convertStringPlus() {
        return converter.convert(stringPlus);
    }

    @Benchmark
    public Short convertStringNegative() {
        return converter.convert(stringNegative);
    }

    @Benchmark
    public Short convertStringTrimmed() {
        return converter.convert(stringTrimmed);
    }

    @Benchmark
    public Short convertNull() {
        return converter.convert(null);
    }

    @Benchmark
    public Short convertInvalidString() {
        try {
            return converter.convert(invalidString);
        } catch (TypeConversionException ex) {
            return null;
        }
    }
}

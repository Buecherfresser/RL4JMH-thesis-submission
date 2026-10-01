package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BigDecimalConverter;
import java.math.BigDecimal;
import jodd.typeconverter.TypeConversionException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigDecimalConverterBenchmark {

    private BigDecimalConverter converter;

    private Object nullValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private String stringWithWhitespace;
    private Object customObject;
    private String invalidString;

    @Setup(Level.Trial)
    public void setup() {
        converter = new BigDecimalConverter();

        nullValue = null;
        bigDecimalValue = new BigDecimal("12345.6789");
        stringValue = "98765.4321";
        stringWithWhitespace = "   55555.5555   ";
        customObject = new CustomToStringObject("33333.3333");
        invalidString = "not-a-number";
    }

    @Benchmark
    public BigDecimal convertNull() {
        return converter.convert(nullValue);
    }

    @Benchmark
    public BigDecimal convertBigDecimal() {
        return converter.convert(bigDecimalValue);
    }

    @Benchmark
    public BigDecimal convertString() {
        return converter.convert(stringValue);
    }

    @Benchmark
    public BigDecimal convertStringWithWhitespace() {
        return converter.convert(stringWithWhitespace);
    }

    @Benchmark
    public BigDecimal convertCustomObject() {
        return converter.convert(customObject);
    }

    @Benchmark
    public BigDecimal convertInvalidString(Blackhole bh) {
        try {
            return converter.convert(invalidString);
        } catch (TypeConversionException ex) {
            bh.consume(ex);
            return null;
        }
    }

    private static class CustomToStringObject {
        private final String value;

        CustomToStringObject(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}

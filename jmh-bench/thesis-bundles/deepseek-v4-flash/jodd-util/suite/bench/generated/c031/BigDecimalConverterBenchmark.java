package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import jodd.typeconverter.impl.BigDecimalConverter;
import java.math.BigDecimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BigDecimalConverterBenchmark {

    private BigDecimalConverter converter;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private Integer integerValue;
    private Double doubleValue;
    private Long longValue;
    private Float floatValue;
    private Short shortValue;
    private Byte byteValue;
    private StringBuilder stringBuilderValue;
    private StringBuffer stringBufferValue;

    @Setup(Level.Trial)
    public void setup() {
        converter = new BigDecimalConverter();
        bigDecimalValue = new BigDecimal("123456789.123456789");
        stringValue = "123456789.123456789";
        integerValue = 123456789;
        doubleValue = 123456789.123456789;
        longValue = 123456789L;
        floatValue = 123456789.123456789f;
        shortValue = 12345;
        byteValue = 123;
        stringBuilderValue = new StringBuilder("123456789.123456789");
        stringBufferValue = new StringBuffer("123456789.123456789");
    }

    @Benchmark
    public BigDecimal convertNull() {
        return converter.convert(null);
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
    public BigDecimal convertInteger() {
        return converter.convert(integerValue);
    }

    @Benchmark
    public BigDecimal convertDouble() {
        return converter.convert(doubleValue);
    }

    @Benchmark
    public BigDecimal convertLong() {
        return converter.convert(longValue);
    }

    @Benchmark
    public BigDecimal convertFloat() {
        return converter.convert(floatValue);
    }

    @Benchmark
    public BigDecimal convertShort() {
        return converter.convert(shortValue);
    }

    @Benchmark
    public BigDecimal convertByte() {
        return converter.convert(byteValue);
    }

    @Benchmark
    public BigDecimal convertStringBuilder() {
        return converter.convert(stringBuilderValue);
    }

    @Benchmark
    public BigDecimal convertStringBuffer() {
        return converter.convert(stringBufferValue);
    }
}

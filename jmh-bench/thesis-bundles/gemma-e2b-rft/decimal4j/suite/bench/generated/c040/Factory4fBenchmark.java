package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory4f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.mutable.MutableDecimal4f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory4fBenchmark {

    // State fields for inputs
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private long unscaledValue;
    private int scale;

    private Decimal4f immutableDecimal4f;
    private MutableDecimal4f mutableDecimal4f;
    private Decimal4f[] decimal4fArray;

    @Setup
    public void setup() {
        // Setup basic numeric inputs
        this.longValue = 123456789L;
        this.floatValue = 3.14159f;
        this.doubleValue = 123.456789;
        this.bigIntValue = new BigInteger("9876543210123456789");
        this.bigDecimalValue = new BigDecimal("1234567890123456789.123456789");
        this.stringValue = "123456789";
        this.roundingMode = RoundingMode.HALF_UP;
        this.unscaledValue = 9876543210L;
        this.scale = 4;

        // Pre-calculate results for comparison/setup if needed, though we focus on factory calls
        this.immutableDecimal4f = Factory4f.INSTANCE.valueOf(longValue);
        this.mutableDecimal4f = Factory4f.INSTANCE.newMutable();
        this.decimal4fArray = new Decimal4f[10];
    }

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(longValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void parseString(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.parse(stringValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfFloatWithRounding(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(floatValue, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(doubleValue, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOfUnscaled(unscaledValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScale(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOfUnscaled(unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOfUnscaled(unscaledValue, scale, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void newMutable(Blackhole bh) {
        MutableDecimal4f result = Factory4f.INSTANCE.newMutable();
        bh.consume(result);
    }

    @Benchmark
    public void newArray(Blackhole bh) {
        Decimal4f[] result = Factory4f.INSTANCE.newArray(10);
        bh.consume(result);
    }
}

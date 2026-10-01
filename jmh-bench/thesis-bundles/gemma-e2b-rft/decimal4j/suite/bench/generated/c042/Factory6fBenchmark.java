package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory6f;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.scale.Scale6f;
import org.decimal4j.scale.ScaleMetrics;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory6fBenchmark {

    // State fields for inputs built in @Setup
    private long longValue;
    private float floatValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private long unscaledValue;
    private int scale;

    // Setup method to prepare inputs
    @Setup
    public void setup() {
        // Setup standard values
        this.longValue = 123456789L;
        this.floatValue = 3.14159f;
        this.doubleValue = 123.456789;
        this.bigIntValue = new BigInteger("9876543210");
        this.bigDecimalValue = new BigDecimal("123456789.123456789");
        this.stringValue = "123456789";
        this.roundingMode = RoundingMode.HALF_UP;
        this.unscaledValue = 9876543210L;
        this.scale = 6;
    }

    // --- ValueOf Benchmarks ---

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOf(longValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfFloat(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOf(floatValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfFloatWithRounding(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOf(floatValue, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOf(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOf(doubleValue, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigInteger(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOf(bigIntValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOf(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimal(Blackhole bh) {
        // Using a simple constant Decimal<?> for testing the generic path
        Decimal<?> input = Decimal6f.valueOf(1L);
        Decimal6f result = Factory6f.INSTANCE.valueOf(input);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimalWithRounding(Blackhole bh) {
        Decimal<?> input = Decimal6f.valueOf(1L);
        Decimal6f result = Factory6f.INSTANCE.valueOf(input, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void parseString(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.parse(stringValue);
        bh.consume(result);
    }

    @Benchmark
    public void parseStringWithRounding(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.parse(stringValue, roundingMode);
        bh.consume(result);
    }

    // --- ValueOfUnscaled Benchmarks ---

    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOfUnscaled(unscaledValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScale(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOfUnscaled(unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        Decimal6f result = Factory6f.INSTANCE.valueOfUnscaled(unscaledValue, scale, roundingMode);
        bh.consume(result);
    }

    // --- Factory/Array Benchmarks ---

    @Benchmark
    public void newMutable(Blackhole bh) {
        MutableDecimal6f result = Factory6f.INSTANCE.newMutable();
        bh.consume(result);
    }

    @Benchmark
    public void newMutableArray(Blackhole bh) {
        int length = 10;
        MutableDecimal6f[] result = Factory6f.INSTANCE.newMutableArray(length);
        bh.consume(result);
    }
}

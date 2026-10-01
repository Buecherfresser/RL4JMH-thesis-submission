package bench.generated.c040;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory4f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.mutable.MutableDecimal4f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory4fBenchmark {

    // --- State Fields ---
    private long longValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private String parseString;
    private long unscaledValue;
    private int scale;
    private RoundingMode roundingMode;
    private int arrayLength = 100;

    private Decimal<?> decimalValue;
    private MutableDecimal4f mutableDecimal;

    @Setup
    public void setup() {
        // Setup primitive/long inputs
        this.longValue = 123456789L;
        this.doubleValue = 3.1415926535;
        this.bigIntValue = new BigInteger("9876543210123456789");
        this.bigDecimalValue = new BigDecimal("1234567890123456789.123456789");
        this.stringValue = "123456789";
        this.parseString = "123456789";
        this.unscaledValue = 9876543210L;
        this.scale = 4;
        this.roundingMode = RoundingMode.HALF_UP;
        
        // Setup a representative Decimal<?> object
        this.decimalValue = Factory4f.INSTANCE.valueOf(longValue);

        // Pre-create instances for mutable operations
        this.mutableDecimal = Factory4f.INSTANCE.newMutable();
    }

    // --- ValueOf Benchmarks ---

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
    public void valueOfString(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.parse(stringValue);
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
    public void valueOfFloatWithRounding(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(Float.valueOf(1.2345f), roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(doubleValue, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimalWithRounding(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(bigDecimalValue, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimalWithRounding(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.valueOf(decimalValue, roundingMode);
        bh.consume(result);
    }

    // --- Parse Benchmarks ---

    @Benchmark
    public void parseString(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.parse(parseString);
        bh.consume(result);
    }

    @Benchmark
    public void parseStringWithRounding(Blackhole bh) {
        Decimal4f result = Factory4f.INSTANCE.parse(parseString, roundingMode);
        bh.consume(result);
    }

    // --- Factory/Creation Benchmarks ---

    @Benchmark
    public void newMutable(Blackhole bh) {
        MutableDecimal4f result = Factory4f.INSTANCE.newMutable();
        bh.consume(result);
    }

    @Benchmark
    public void newArray(Blackhole bh) {
        Decimal4f[] result = Factory4f.INSTANCE.newArray(arrayLength);
        bh.consume(result);
    }

    @Benchmark
    public void newMutableArray(Blackhole bh) {
        MutableDecimal4f[] result = Factory4f.INSTANCE.newMutableArray(arrayLength);
        bh.consume(result);
    }
}

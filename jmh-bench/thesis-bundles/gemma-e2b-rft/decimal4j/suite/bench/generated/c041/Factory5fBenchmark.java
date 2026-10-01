package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory5f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.mutable.MutableDecimal5f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory5fBenchmark {

    // --- State Fields for Inputs ---
    private long longValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private long unscaledValue;
    private int scale;
    private int arrayLength;

    private Decimal5f immutableDecimal;
    private MutableDecimal5f mutableDecimal;

    @Setup
    public void setup() {
        // Setup standard inputs
        longValue = 1234567890123L;
        doubleValue = 3.1415926535;
        bigIntValue = new BigInteger("9876543210987654321");
        bigDecimalValue = new BigDecimal("1234567890123.456789");
        stringValue = "1234567890123";
        roundingMode = RoundingMode.HALF_UP;
        unscaledValue = 9876543210L;
        scale = 5;
        arrayLength = 100;

        // Pre-calculate some results for comparison if needed, though we focus on factory calls
        immutableDecimal = Factory5f.INSTANCE.valueOf(longValue);
        mutableDecimal = Factory5f.INSTANCE.newMutable();
    }

    // --- Immutable ValueOf Benchmarks ---

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOf(longValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOf(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigInteger(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOf(bigIntValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOf(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOf(doubleValue, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimalWithRounding(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOf(bigDecimalValue, roundingMode);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimal(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOf(immutableDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimalWithRounding(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOf(immutableDecimal, roundingMode);
        bh.consume(result);
    }

    // --- Parsing Benchmarks ---

    @Benchmark
    public void parseString(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.parse(stringValue);
        bh.consume(result);
    }

    @Benchmark
    public void parseStringWithRounding(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.parse(stringValue, roundingMode);
        bh.consume(result);
    }

    // --- Unscaled ValueOf Benchmarks ---

    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOfUnscaled(unscaledValue);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScale(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOfUnscaled(unscaledValue, scale);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        Decimal5f result = Factory5f.INSTANCE.valueOfUnscaled(unscaledValue, scale, roundingMode);
        bh.consume(result);
    }

    // --- Mutable and Array Creation Benchmarks ---

    @Benchmark
    public void newMutable(Blackhole bh) {
        MutableDecimal5f result = Factory5f.INSTANCE.newMutable();
        bh.consume(result);
    }

    @Benchmark
    public void newArray(Blackhole bh) {
        Decimal5f[] result = Factory5f.INSTANCE.newArray(arrayLength);
        bh.consume(result);
    }

    @Benchmark
    public void newMutableArray(Blackhole bh) {
        MutableDecimal5f[] result = Factory5f.INSTANCE.newMutableArray(arrayLength);
        bh.consume(result);
    }
}

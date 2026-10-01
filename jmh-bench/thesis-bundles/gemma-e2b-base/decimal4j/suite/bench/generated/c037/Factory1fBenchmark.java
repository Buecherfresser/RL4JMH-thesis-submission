package bench.generated.c037;

import org.decimal4j.factory.Factory1f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.api.Decimal;

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
public class Factory1fBenchmark {

    // State fields for inputs
    private long longValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private RoundingMode roundingMode;
    private long unscaledValue;
    private int scale;
    private int arrayLength;

    // Results storage
    private Decimal1f immutableResult;
    private MutableDecimal1f mutableResult;
    private MutableDecimal1f[] arrayResult;

    @Setup
    public void setup() {
        // Setup standard inputs
        longValue = 123456789L;
        doubleValue = 3.1415926535;
        bigIntValue = new BigInteger("9876543210123456789");
        bigDecimalValue = new BigDecimal("123456789.123456789");
        stringValue = "123456789";
        roundingMode = RoundingMode.HALF_UP;
        unscaledValue = 9876543210L;
        scale = 1;
        arrayLength = 100;
    }

    // --- Immutable ValueOf Benchmarks ---

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOf(longValue);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOf(doubleValue);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOf(bigDecimalValue);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfString(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.parse(stringValue);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOf(doubleValue, roundingMode);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfBigDecimalWithRounding(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOf(bigDecimalValue, roundingMode);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfBigInteger(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOf(bigIntValue);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfDecimal(Blackhole bh) {
        // Using a simple Decimal<?> representation for testing generic handling
        Decimal<?> dummyDecimal = Factory1f.INSTANCE.valueOf(longValue);
        immutableResult = Factory1f.INSTANCE.valueOf(dummyDecimal);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfDecimalWithRounding(Blackhole bh) {
        Decimal<?> dummyDecimal = Factory1f.INSTANCE.valueOf(longValue);
        immutableResult = Factory1f.INSTANCE.valueOf(dummyDecimal, roundingMode);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void parseStringWithRounding(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.parse(stringValue, roundingMode);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfUnscaledLong(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOfUnscaled(unscaledValue);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScale(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOfUnscaled(unscaledValue, scale);
        bh.consume(immutableResult);
    }

    @Benchmark
    public void valueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        immutableResult = Factory1f.INSTANCE.valueOfUnscaled(unscaledValue, scale, roundingMode);
        bh.consume(immutableResult);
    }

    // --- Mutable and Array Creation Benchmarks ---

    @Benchmark
    public void newMutable(Blackhole bh) {
        mutableResult = Factory1f.INSTANCE.newMutable();
        bh.consume(mutableResult);
    }

    @Benchmark
    public void newMutableArray(Blackhole bh) {
        arrayResult = Factory1f.INSTANCE.newMutableArray(arrayLength);
        bh.consume(arrayResult);
    }
}

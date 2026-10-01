package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory3f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.mutable.MutableDecimal3f;
import org.decimal4j.scale.Scale3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Factory3fBenchmark {

    private Decimal3f immutableDecimal;
    private MutableDecimal3f mutableDecimal;

    @Setup
    public void setup() {
        // Setup immutable decimal using a long value
        long value = 123456789L;
        this.immutableDecimal = Factory3f.INSTANCE.valueOf(value);

        // Setup mutable decimal
        this.mutableDecimal = Factory3f.INSTANCE.newMutable();
    }

    // --- Immutable ValueOf Benchmarks ---

    @Benchmark
    public void valueOfLong(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.valueOf(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDouble(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.valueOf(3.14159);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfBigDecimal(Blackhole bh) {
        BigDecimal bd = new BigDecimal("123456789.123");
        Decimal3f result = Factory3f.INSTANCE.valueOf(bd);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfFloat(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.valueOf(1.2345f);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDoubleWithRounding(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.valueOf(1.234567890123456789);
        Decimal3f rounded = Factory3f.INSTANCE.valueOf(1.234567890123456789, RoundingMode.HALF_UP);
        bh.consume(rounded);
    }

    @Benchmark
    public void valueOfString(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.parse("123456789");
        bh.consume(result);
    }

    @Benchmark
    public void valueOfStringWithRounding(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.parse("1.2345", RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimal(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.valueOf(immutableDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void valueOfDecimalWithRounding(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.valueOf(immutableDecimal, RoundingMode.HALF_DOWN);
        bh.consume(result);
    }

    // --- Mutable ValueOf Benchmarks ---

    @Benchmark
    public void newMutable(Blackhole bh) {
        MutableDecimal3f result = Factory3f.INSTANCE.newMutable();
        bh.consume(result);
    }

    @Benchmark
    public void newMutableFromLong(Blackhole bh) {
        MutableDecimal3f result = Factory3f.INSTANCE.newMutable();
        result.setUnscaled(987654321L);
        bh.consume(result);
    }

    @Benchmark
    public void setUnscaled(Blackhole bh) {
        MutableDecimal3f result = Factory3f.INSTANCE.newMutable();
        result.setUnscaled(123456789L);
        bh.consume(result);
    }

    // --- Factory/Type Benchmarks ---

    @Benchmark
    public void getScaleMetrics(Blackhole bh) {
        Scale3f scaleMetrics = Factory3f.INSTANCE.getScaleMetrics();
        bh.consume(scaleMetrics);
    }

    @Benchmark
    public void getScale(Blackhole bh) {
        int scale = Factory3f.INSTANCE.getScale();
        bh.consume(scale);
    }

    @Benchmark
    public void immutableType(Blackhole bh) {
        Class<Decimal3f> type = Factory3f.INSTANCE.immutableType();
        bh.consume(type);
    }

    @Benchmark
    public void mutableType(Blackhole bh) {
        Class<MutableDecimal3f> type = Factory3f.INSTANCE.mutableType();
        bh.consume(type);
    }

    // --- Array/Collection Benchmarks ---

    @Benchmark
    public void newArray(Blackhole bh) {
        Decimal3f[] result = Factory3f.INSTANCE.newArray(10);
        bh.consume(result);
    }

    @Benchmark
    public void newMutableArray(Blackhole bh) {
        MutableDecimal3f[] result = Factory3f.INSTANCE.newMutableArray(5);
        bh.consume(result);
    }

    // --- Parsing Benchmarks ---

    @Benchmark
    public void parseString(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.parse("987654321");
        bh.consume(result);
    }

    @Benchmark
    public void parseStringWithRounding(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.parse("1.2345", RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void parseStringWithRoundingAndScale(Blackhole bh) {
        Decimal3f result = Factory3f.INSTANCE.parse("123456789", RoundingMode.HALF_DOWN);
        bh.consume(result);
    }
}

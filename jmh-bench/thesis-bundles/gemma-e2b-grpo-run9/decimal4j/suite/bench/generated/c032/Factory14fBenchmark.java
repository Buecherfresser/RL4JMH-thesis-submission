package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory14f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.scale.Scale14f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory14fBenchmark {

    // Since Factory14f is an enum with a static INSTANCE, we don't need an instance field.
    // We will rely on calling methods directly on Factory14f.INSTANCE.

    @Setup
    public void setup() {
        // Setup can be empty as Factory14f is stateless (singleton enum)
    }

    // --- Benchmarks for Immutable ValueOf methods ---

    @Benchmark
    public Decimal14f benchmarkValueOfLong(Blackhole bh) {
        // Test valueOf(long value)
        Decimal14f result = Factory14f.INSTANCE.valueOf(123456789L);
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDouble(Blackhole bh) {
        // Test valueOf(double value)
        Decimal14f result = Factory14f.INSTANCE.valueOf(3.1415926535);
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test valueOf(BigDecimal value)
        BigDecimal bd = new BigDecimal("123.4567890123456789");
        Decimal14f result = Factory14f.INSTANCE.valueOf(bd);
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Decimal14f benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        // Test valueOf(double value, RoundingMode roundingMode)
        Decimal14f result = Factory14f.INSTANCE.valueOf(1.23456789, RoundingMode.HALF_UP);
        bh.consume(result);
        return null;
    }

    // --- Benchmarks for Mutable Type Creation ---

    @Benchmark
    public MutableDecimal14f benchmarkNewMutable(Blackhole bh) {
        // Test newMutable()
        MutableDecimal14f result = Factory14f.INSTANCE.newMutable();
        bh.consume(result);
        return null;
    }

    // --- Benchmarks for Factory Methods (Type retrieval) ---

    @Benchmark
    public Scale14f benchmarkGetScaleMetrics(Blackhole bh) {
        // Test getScaleMetrics()
        Scale14f scaleMetrics = Factory14f.INSTANCE.getScaleMetrics();
        bh.consume(scaleMetrics);
        return null;
    }

    @Benchmark
    public Class<Decimal14f> benchmarkImmutableType(Blackhole bh) {
        // Test immutableType()
        Class<Decimal14f> type = Factory14f.INSTANCE.immutableType();
        bh.consume(type);
        return null;
    }

    // --- Benchmarks for Parsing ---

    @Benchmark
    public Decimal14f benchmarkParseString(Blackhole bh) {
        // Test parse(String value)
        Decimal14f result = Factory14f.INSTANCE.parse("12345678901234567890123456789012");
        bh.consume(result);
        return null;
    }

    @Benchmark
    public Decimal14f benchmarkParseStringWithRounding(Blackhole bh) {
        // Test parse(String value, RoundingMode roundingMode)
        Decimal14f result = Factory14f.INSTANCE.parse("1.23456789", RoundingMode.HALF_EVEN);
        bh.consume(result);
        return null;
    }

    // --- Benchmarks for Array Creation ---

    @Benchmark
    public Decimal14f[] benchmarkNewArray(Blackhole bh) {
        // Test newArray(int length)
        Decimal14f[] result = Factory14f.INSTANCE.newArray(10);
        bh.consume(result);
        return null;
    }
}

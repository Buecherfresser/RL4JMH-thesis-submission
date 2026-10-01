package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.decimal4j.factory.Factory10f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory10fBenchmark {

    // Since Factory10f is an enum singleton, we rely on calling Factory10f.INSTANCE.method()

    @Setup
    public void setup() {
        // Initialize any necessary state here. For this enum, it's empty.
    }

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        // Test immutable valueOf(long)
        Factory10f.INSTANCE.valueOf(123456789L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        // Test immutable valueOf(double)
        Factory10f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        // Test immutable valueOf(BigDecimal)
        Factory10f.INSTANCE.valueOf(BigDecimal.TEN);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        // Test immutable valueOf(float)
        Factory10f.INSTANCE.valueOf((float) 1.23f);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        // Test immutable valueOf(double, RoundingMode)
        Factory10f.INSTANCE.valueOf(1.0, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        // Test immutable parse(String)
        Factory10f.INSTANCE.parse("12345");
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkParseStringWithRounding(Blackhole bh) {
        // Test immutable parse(String, RoundingMode)
        Factory10f.INSTANCE.parse("12345", RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaled(Blackhole bh) {
        // Test immutable valueOfUnscaled(long)
        Factory10f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithScale(Blackhole bh) {
        // Test immutable valueOfUnscaled(long, int scale)
        Factory10f.INSTANCE.valueOfUnscaled(12345L, 5);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        // Test mutableType() which returns a new instance
        Factory10f.INSTANCE.mutableType();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkNewMutableArray(Blackhole bh) {
        // Test newMutableArray(int length) which returns an array
        Factory10f.INSTANCE.newMutableArray(10);
        bh.consume(null);
    }
}

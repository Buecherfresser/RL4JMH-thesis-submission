package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.factory.Factory6f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory6fBenchmark {

    // Since Factory6f is an enum singleton, we rely on Factory6f.INSTANCE

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        // Test a simple valueOf call
        Factory6f.INSTANCE.valueOf(12345L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        // Test a double valueOf call
        Factory6f.INSTANCE.valueOf(3.14159);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        // Test a BigDecimal valueOf call
        Factory6f.INSTANCE.valueOf(BigDecimal.TEN);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfDoubleWithRounding(Blackhole bh) {
        // Test a double valueOf with rounding mode
        Factory6f.INSTANCE.valueOf(1.23456, RoundingMode.HALF_UP);
        bh.consume(null);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        // Test parse method
        Factory6f.INSTANCE.parse("123.45");
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaled(Blackhole bh) {
        // Test valueOfUnscaled
        Factory6f.INSTANCE.valueOfUnscaled(987654321L);
        bh.consume(null);
    }

    @Benchmark
    public void testValueOfUnscaledWithScale(Blackhole bh) {
        // Test valueOfUnscaled with scale
        Factory6f.INSTANCE.valueOfUnscaled(12345L, 6);
        bh.consume(null);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        // Test mutable type creation
        Factory6f.INSTANCE.newMutable();
        bh.consume(null);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Test array creation
        Factory6f.INSTANCE.newArray(5);
        bh.consume(null);
    }
}

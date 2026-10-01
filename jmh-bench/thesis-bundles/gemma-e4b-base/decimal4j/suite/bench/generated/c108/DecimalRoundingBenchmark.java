package bench.generated.c108;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.TruncatedPart;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DecimalRoundingBenchmark {

    // Inputs for calculateRoundingIncrement
    private int sign;
    private long truncatedValue;
    private TruncatedPart truncatedPart;

    // Subject instances for testing specific rounding modes
    private DecimalRounding halfUpRounding;
    private DecimalRounding uncheckedRounding;
    private DecimalRounding floorRounding;

    @Setup(Level.Trial)
    public void setup() {
        // Representative inputs for calculation
        sign = 1;
        truncatedValue = 12345L;
        truncatedPart = TruncatedPart.EQUAL_TO_HALF;

        // Specific rounding mode instances
        halfUpRounding = DecimalRounding.HALF_UP;
        uncheckedRounding = DecimalRounding.UNNECESSARY;
        floorRounding = DecimalRounding.FLOOR;
    }

    @Benchmark
    public void testGetRoundingMode_HalfUp(Blackhole bh) {
        RoundingMode mode = halfUpRounding.getRoundingMode();
        bh.consume(mode);
    }

    @Benchmark
    public void testGetRoundingMode_Unchecked(Blackhole bh) {
        RoundingMode mode = uncheckedRounding.getRoundingMode();
        bh.consume(mode);
    }

    @Benchmark
    public void testGetRoundingMode_Floor(Blackhole bh) {
        RoundingMode mode = floorRounding.getRoundingMode();
        bh.consume(mode);
    }

    @Benchmark
    public void testCalculateRoundingIncrement_HalfUp(Blackhole bh) {
        int increment = halfUpRounding.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testCalculateRoundingIncrement_Unchecked(Blackhole bh) {
        // This test case is expected to throw ArithmeticException if the part is non-zero.
        // We must wrap it in a try-catch block to prevent JMH from failing the benchmark run,
        // while still measuring the execution path.
        try {
            int increment = uncheckedRounding.calculateRoundingIncrement(sign, truncatedValue, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO);
            bh.consume(increment);
        } catch (ArithmeticException e) {
            // Expected behavior for UNNECESSARY when rounding is needed.
            bh.consume(e);
        }
    }

    @Benchmark
    public void testCalculateRoundingIncrement_Floor(Blackhole bh) {
        int increment = floorRounding.calculateRoundingIncrement(sign, truncatedValue, truncatedPart);
        bh.consume(increment);
    }

    @Benchmark
    public void testValueOf_RoundingModeUP(Blackhole bh) {
        DecimalRounding dr = DecimalRounding.valueOf(RoundingMode.UP);
        bh.consume(dr);
    }

    @Benchmark
    public void testValueOf_RoundingModeHALF_EVEN(Blackhole bh) {
        DecimalRounding dr = DecimalRounding.valueOf(RoundingMode.HALF_EVEN);
        bh.consume(dr);
    }

    @Benchmark
    public void testValueOf_RoundingModeDOWN(Blackhole bh) {
        DecimalRounding dr = DecimalRounding.valueOf(RoundingMode.DOWN);
        bh.consume(dr);
    }
}

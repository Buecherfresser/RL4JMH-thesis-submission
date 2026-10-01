package bench.generated.c105;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetic;
import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetic;
import org.decimal4j.arithmetic.UncheckedScaleNfRoundingArithmetic;
import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;
import org.decimal4j.scale.Scale9f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Scale9fBenchmark {

    // Inputs for arithmetic operations
    private long dividend;
    private long factor;
    private long overflowFactor;
    private int mulloFactor;

    // State for arithmetic selection tests
    private DecimalArithmetic defaultArithmetic;
    private DecimalArithmetic checkedArithmetic;
    private DecimalArithmetic roundingDownArithmetic;

    @Setup
    public void setup() {
        // Setup inputs for arithmetic tests
        this.dividend = 123456789L;
        this.factor = 1000000000L;
        this.overflowFactor = Long.MAX_VALUE / 2; // A value likely to cause overflow when multiplied by factor
        this.mulloFactor = 12345;

        // Setup arithmetic instances
        this.defaultArithmetic = Scale9f.INSTANCE.getDefaultArithmetic();
        this.checkedArithmetic = Scale9f.INSTANCE.getDefaultCheckedArithmetic();
        this.roundingDownArithmetic = Scale9f.INSTANCE.getRoundingDownArithmetic();
    }

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        long result = Scale9f.INSTANCE.multiplyByScaleFactor(factor);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByScaleFactorExact(Blackhole bh) {
        try {
            long result = Scale9f.INSTANCE.multiplyByScaleFactorExact(overflowFactor);
            bh.consume(result);
        } catch (ArithmeticException e) {
            // If overflow occurs, we still consume something, though this path is less common in average time tests
            bh.consume(null);
        }
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        long result = Scale9f.INSTANCE.mulloByScaleFactor(mulloFactor);
        bh.consume(result);
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        long result = Scale9f.INSTANCE.divideByScaleFactor(dividend);
        bh.consume(result);
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        long unsignedDividend = Long.MAX_VALUE;
        long result = Scale9f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividend);
        bh.consume(result);
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        long result = Scale9f.INSTANCE.moduloByScaleFactor(dividend);
        bh.consume(result);
    }

    @Benchmark
    public void getScale(Blackhole bh) {
        int scale = Scale9f.INSTANCE.getScale();
        bh.consume(scale);
    }

    @Benchmark
    public void getScaleFactor(Blackhole bh) {
        long factor = Scale9f.INSTANCE.getScaleFactor();
        bh.consume(factor);
    }

    @Benchmark
    public void getDefaultArithmetic(Blackhole bh) {
        DecimalArithmetic arith = Scale9f.INSTANCE.getDefaultArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void getCheckedArithmetic(Blackhole bh) {
        DecimalArithmetic arith = Scale9f.INSTANCE.getDefaultCheckedArithmetic();
        bh.consume(arith);
    }

    @Benchmark
    public void getRoundingDownArithmetic(Blackhole bh) {
        DecimalArithmetic arith = Scale9f.INSTANCE.getRoundingDownArithmetic();
        bh.consume(arith);
    }
}

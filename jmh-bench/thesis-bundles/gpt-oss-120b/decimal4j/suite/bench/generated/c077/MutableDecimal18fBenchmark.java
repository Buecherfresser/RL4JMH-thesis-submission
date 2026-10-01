package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal18f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.exact.Multipliable18f;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.api.DecimalArithmetic;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal18fBenchmark {

    private MutableDecimal18f baseOne;
    private MutableDecimal18f baseTwo;
    private long unscaledValue;

    @Setup(Level.Trial)
    public void setup() {
        baseOne = MutableDecimal18f.one();
        baseTwo = MutableDecimal18f.two();
        unscaledValue = 123456789012345678L;
    }

    // -------------------------------------------------------------------------
    // Static factory methods
    // -------------------------------------------------------------------------

    @Benchmark
    public MutableDecimal18f benchZero() {
        return MutableDecimal18f.zero();
    }

    @Benchmark
    public MutableDecimal18f benchOne() {
        return MutableDecimal18f.one();
    }

    @Benchmark
    public MutableDecimal18f benchTwo() {
        return MutableDecimal18f.two();
    }

    @Benchmark
    public MutableDecimal18f benchUnscaled() {
        return MutableDecimal18f.unscaled(unscaledValue);
    }

    // -------------------------------------------------------------------------
    // Instance creation / cloning
    // -------------------------------------------------------------------------

    @Benchmark
    public MutableDecimal18f benchClone() {
        return baseOne.clone();
    }

    // -------------------------------------------------------------------------
    // Conversion methods
    // -------------------------------------------------------------------------

    @Benchmark
    public Decimal18f benchToImmutable() {
        return baseOne.clone().toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal18f benchToMutable() {
        return baseOne.clone().toMutableDecimal();
    }

    @Benchmark
    public Multipliable18f benchMultiplyExact() {
        return baseOne.clone().multiplyExact();
    }

    // -------------------------------------------------------------------------
    // Accessor methods
    // -------------------------------------------------------------------------

    @Benchmark
    public int benchGetScale() {
        return baseOne.getScale();
    }

    @Benchmark
    public Scale18f benchGetScaleMetrics() {
        return baseOne.getScaleMetrics();
    }

    @Benchmark
    public Factory18f benchGetFactory() {
        return baseOne.getFactory();
    }

    // -------------------------------------------------------------------------
    // Arithmetic operations (mutating, each works on a fresh clone)
    // -------------------------------------------------------------------------

    @Benchmark
    public MutableDecimal18f benchAdd() {
        MutableDecimal18f a = baseOne.clone();
        return a.add(baseTwo);
    }

    @Benchmark
    public MutableDecimal18f benchSubtract() {
        MutableDecimal18f a = baseTwo.clone();
        return a.subtract(baseOne);
    }

    @Benchmark
    public MutableDecimal18f benchMultiply() {
        MutableDecimal18f a = baseOne.clone();
        return a.multiply(baseTwo);
    }

    @Benchmark
    public MutableDecimal18f benchDivide() {
        MutableDecimal18f a = baseTwo.clone();
        return a.divide(baseOne);
    }

    @Benchmark
    public MutableDecimal18f benchRemainder() {
        MutableDecimal18f a = baseTwo.clone();
        return a.remainder(baseOne);
    }

    @Benchmark
    public MutableDecimal18f benchNegate() {
        MutableDecimal18f a = baseOne.clone();
        return a.negate();
    }

    @Benchmark
    public MutableDecimal18f benchAbs() {
        MutableDecimal18f a = baseOne.clone();
        return a.abs();
    }

    @Benchmark
    public MutableDecimal18f benchInvert() {
        MutableDecimal18f a = baseOne.clone();
        return a.invert();
    }

    @Benchmark
    public MutableDecimal18f benchSquare() {
        MutableDecimal18f a = baseOne.clone();
        return a.square();
    }

    @Benchmark
    public MutableDecimal18f benchSqrt() {
        MutableDecimal18f a = baseTwo.clone();
        return a.sqrt();
    }

    @Benchmark
    public MutableDecimal18f benchPow() {
        MutableDecimal18f a = baseOne.clone();
        return a.pow(3);
    }

    @Benchmark
    public MutableDecimal18f benchShiftLeft() {
        MutableDecimal18f a = baseOne.clone();
        return a.shiftLeft(2);
    }

    @Benchmark
    public MutableDecimal18f benchShiftRight() {
        MutableDecimal18f a = baseOne.clone();
        return a.shiftRight(2);
    }

    @Benchmark
    public MutableDecimal18f benchRound() {
        MutableDecimal18f a = baseOne.clone();
        return a.round(2, RoundingMode.HALF_UP);
    }
}

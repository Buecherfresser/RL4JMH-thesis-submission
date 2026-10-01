package bench.generated;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.factory.Factories;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.factory.Factory13f;
import org.decimal4j.factory.Factory15f;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.factory.Factory2f;
import org.decimal4j.factory.Factory5f;
import org.decimal4j.generic.GenericDecimalFactory;
import org.decimal4j.generic.GenericImmutableDecimal;
import org.decimal4j.generic.GenericMutableDecimal;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.immutable.Decimal13f;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.mutable.MutableDecimal13f;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.mutable.MutableDecimal18f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.mutable.MutableDecimal4f;
import org.decimal4j.mutable.MutableDecimal5f;
import org.decimal4j.mutable.MutableDecimal6f;
import org.decimal4j.scale.Scale10f;
import org.decimal4j.scale.Scale13f;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import org.decimal4j.truncate.CheckedRounding;
import org.decimal4j.truncate.DecimalRounding;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncatedPart;
import org.decimal4j.truncate.UncheckedRounding;
import org.decimal4j.util.DoubleRounder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Gold reference JMH suite for the decimal4j mutation track.
 *
 * Covers a broad cross-section of the library: conversion into and out of the
 * fixed-point representation (from `long`, `float`, `double`, `BigInteger`,
 * `BigDecimal`, `String` and raw unscaled values), the immutable and mutable
 * value types across several scales, the generic runtime-scale variants, the
 * exact `multiplyExact().by(...)` widening products, rescaling, the scale-metric
 * primitives, the unchecked and checked arithmetic back-ends, the
 * rounding/overflow policy enums and the `DoubleRounder` utility.
 *
 * Inputs are generated once per trial with a fixed seed. Each benchmark loops
 * over a batch of values so the per-operation cost dominates the loop overhead.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class Decimal4jBenchmark {

    /** Values per measured operation. */
    private static final int BATCH = 2_048;

    private long[] longValues;
    private double[] doubleValues;
    private float[] floatValues;
    private BigDecimal[] bigDecimals;
    private BigInteger[] bigIntegers;
    /** Values small enough for scale 18, whose range is only about ±9.22. */
    private BigDecimal[] smallDecimals;
    private BigInteger[] smallIntegers;

    private Decimal5f[] scale5;
    private Decimal2f[] scale2;

    @Setup(Level.Trial)
    public void setUp() {
        final Random random = new Random(20240115L);

        longValues = new long[BATCH];
        doubleValues = new double[BATCH];
        floatValues = new float[BATCH];
        bigDecimals = new BigDecimal[BATCH];
        bigIntegers = new BigInteger[BATCH];
        smallDecimals = new BigDecimal[BATCH];
        smallIntegers = new BigInteger[BATCH];
        scale5 = new Decimal5f[BATCH];
        scale2 = new Decimal2f[BATCH];

        for (int i = 0; i < BATCH; i++) {
            longValues[i] = random.nextInt(1_000_000);
            doubleValues[i] = random.nextDouble() * 1_000;
            floatValues[i] = random.nextFloat() * 1_000f;
            bigDecimals[i] = BigDecimal.valueOf(random.nextInt(1_000_000), 3);
            bigIntegers[i] = BigInteger.valueOf(random.nextInt(1_000));
            smallDecimals[i] = BigDecimal.valueOf(random.nextInt(9_000), 3);
            smallIntegers[i] = BigInteger.valueOf(random.nextInt(9));
            scale5[i] = Decimal5f.valueOf(doubleValues[i]);
            scale2[i] = Decimal2f.valueOf(doubleValues[i]);
        }
    }

    // ---- conversion in -----------------------------------------------------

    @Benchmark
    public void fromFloatAndDouble(final Blackhole bh) {
        final DecimalArithmetic scale0 = Scales.getScaleMetrics(0)
                .getCheckedArithmetic(RoundingMode.HALF_UP);
        final DecimalArithmetic scale5f = Scale5f.INSTANCE.getArithmetic(RoundingMode.HALF_EVEN);
        for (int i = 0; i < BATCH; i++) {
            bh.consume(scale0.fromFloat(floatValues[i]));
            bh.consume(scale5f.fromDouble(doubleValues[i]));
        }
    }

    @Benchmark
    public void fromUnscaled(final Blackhole bh) {
        final DecimalArithmetic scale0 = Scales.getScaleMetrics(0)
                .getArithmetic(RoundingMode.HALF_UP);
        for (int i = 0; i < BATCH; i++) {
            bh.consume(scale0.fromUnscaled(longValues[i], 4));
        }
    }

    @Benchmark
    public void fromBigDecimalUnchecked(final Blackhole bh) {
        final DecimalArithmetic arithmetic =
                Scale10f.INSTANCE.getArithmetic(RoundingMode.HALF_UP);
        for (int i = 0; i < BATCH; i++) {
            bh.consume(arithmetic.fromBigDecimal(bigDecimals[i]));
        }
    }

    @Benchmark
    public void fromBigDecimalChecked(final Blackhole bh) {
        final DecimalArithmetic arithmetic =
                Scale10f.INSTANCE.getCheckedArithmetic(RoundingMode.HALF_UP);
        for (int i = 0; i < BATCH; i++) {
            bh.consume(arithmetic.fromBigDecimal(bigDecimals[i]));
        }
    }

    @Benchmark
    public void valueOfBigInteger(final Blackhole bh) {
        for (int i = 0; i < BATCH; i++) {
            bh.consume(Decimal2f.valueOf(bigIntegers[i]));
            bh.consume(Decimal5f.valueOf(bigIntegers[i]));
            bh.consume(Decimal10f.valueOf(bigIntegers[i]));
            bh.consume(Decimal13f.valueOf(bigIntegers[i]));
            bh.consume(Decimal15f.valueOf(smallIntegers[i]));
            bh.consume(Decimal18f.valueOf(smallIntegers[i]));
        }
    }

    @Benchmark
    public void factoryValueOfBigDecimal(final Blackhole bh) {
        for (int i = 0; i < BATCH; i++) {
            bh.consume(Factory2f.INSTANCE.valueOf(bigDecimals[i]));
            bh.consume(Factory5f.INSTANCE.valueOf(bigDecimals[i]));
            bh.consume(Factory10f.INSTANCE.valueOf(bigDecimals[i]));
            bh.consume(Factory13f.INSTANCE.valueOf(bigDecimals[i]));
            bh.consume(Factory15f.INSTANCE.valueOf(smallDecimals[i]));
            bh.consume(Factory18f.INSTANCE.valueOf(smallDecimals[i]));
        }
    }

    @Benchmark
    public void mutableFromUnscaledAndZero(final Blackhole bh) {
        for (int i = 0; i < BATCH; i++) {
            bh.consume(MutableDecimal10f.unscaled(longValues[i]));
            bh.consume(MutableDecimal13f.unscaled(longValues[i]));
            bh.consume(MutableDecimal18f.unscaled(longValues[i]));
            bh.consume(MutableDecimal2f.zero());
            bh.consume(MutableDecimal5f.zero());
            bh.consume(MutableDecimal15f.zero());
        }
    }

    // ---- arithmetic --------------------------------------------------------

    @Benchmark
    public Decimal5f addAndSubtract() {
        Decimal5f sum = Decimal5f.ZERO;
        for (int i = 0; i < BATCH; i++) {
            sum = sum.add(scale5[i]).subtract(scale5[BATCH - 1 - i]);
        }
        return sum;
    }

    /** The raw unscaled back-ends, unchecked and checked, side by side. */
    @Benchmark
    public void subtractOnArithmeticBackends(final Blackhole bh) {
        final DecimalArithmetic unchecked = Scale5f.INSTANCE.getDefaultArithmetic();
        final DecimalArithmetic checked = Scale5f.INSTANCE.getDefaultCheckedArithmetic();
        long uncheckedAcc = 0;
        long checkedAcc = 0;
        for (int i = 0; i < BATCH; i++) {
            uncheckedAcc = unchecked.subtract(uncheckedAcc, scale5[i].unscaledValue());
            checkedAcc = checked.subtract(checkedAcc, scale5[i].unscaledValue() % 1_000);
        }
        bh.consume(uncheckedAcc);
        bh.consume(checkedAcc);
        bh.consume(unchecked.getScale());
        bh.consume(unchecked.deriveArithmetic(RoundingMode.FLOOR));
    }

    @Benchmark
    public Decimal5f multiplyUnscaledWithPolicy() {
        Decimal5f product = Decimal5f.ONE;
        for (int i = 0; i < BATCH; i++) {
            product = product.multiplyUnscaled(
                    1 + (scale5[i].unscaledValue() % 7), CheckedRounding.HALF_UP);
        }
        return product;
    }

    /** Exact widening products: `a.multiplyExact().by(b)` grows the scale. */
    @Benchmark
    public void multiplyExact(final Blackhole bh) {
        for (int i = 0; i < BATCH; i++) {
            final Decimal2f a2 = scale2[i];
            final Decimal5f a5 = scale5[i];
            bh.consume(a2.multiplyExact().by(Decimal8f.ONE));
            bh.consume(a5.multiplyExact().by(MutableDecimal6f.one()));
            bh.consume(Decimal7f.valueOf(longValues[i] % 100)
                    .multiplyExact().by(MutableDecimal4f.one()));
            bh.consume(Decimal10f.valueOf(longValues[i] % 100)
                    .multiplyExact().by(Decimal4f.ONE));
            bh.consume(Decimal13f.valueOf(longValues[i] % 100)
                    .multiplyExact().by(MutableDecimal2f.one()));
            bh.consume(Decimal15f.valueOf(smallIntegers[i])
                    .multiplyExact().by(MutableDecimal1f.one()));
            bh.consume(Decimal18f.valueOf(smallIntegers[i])
                    .multiplyExact().by(Decimal0f.ONE));
        }
    }

    // ---- rescaling and mutation -------------------------------------------

    @Benchmark
    public void rescale(final Blackhole bh) {
        for (int i = 0; i < BATCH; i++) {
            bh.consume(scale5[i].scale(Scale2f.INSTANCE));
            bh.consume(scale5[i].scale(Scale13f.INSTANCE));
            bh.consume(Decimal2f.valueOf(smallDecimals[i]).scale(Scale18f.INSTANCE));
        }
    }

    @Benchmark
    public MutableDecimal5f mutateInPlace() {
        final MutableDecimal5f value = MutableDecimal5f.zero();
        for (int i = 0; i < BATCH; i++) {
            value.setOne();
            value.add(scale5[i]);
        }
        return value;
    }

    // ---- generic (runtime scale) -------------------------------------------

    @Benchmark
    public void genericFactory(final Blackhole bh) {
        final GenericDecimalFactory<Scale5f> factory =
                new GenericDecimalFactory<>(Scale5f.INSTANCE);
        for (int i = 0; i < BATCH; i++) {
            final GenericImmutableDecimal<Scale5f> immutable = factory.valueOf(bigDecimals[i]);
            bh.consume(immutable.add(immutable));
            bh.consume(immutable.toBigDecimal());
            // Rounding to the value's own scale is a no-op that returns itself.
            bh.consume(immutable.round(immutable.getScale()));
        }
        bh.consume(factory.newArray(16));
        bh.consume(factory.newMutableArray(16));
        final GenericMutableDecimal<Scale5f> mutable = factory.newMutable();
        bh.consume(mutable.setOne());
        // divideAndRemainder allocates its two-element result through createArray.
        bh.consume(mutable.divideAndRemainder(factory.valueOf(BigDecimal.valueOf(3))));
        bh.consume(Factories.getGenericDecimalFactory(5));
    }

    // ---- scale metrics -----------------------------------------------------

    @Benchmark
    public void scaleFactorPrimitives(final Blackhole bh) {
        final ScaleMetrics[] metrics = {
            Scale2f.INSTANCE, Scale5f.INSTANCE, Scale10f.INSTANCE,
            Scale13f.INSTANCE, Scale15f.INSTANCE, Scale18f.INSTANCE,
        };
        for (int i = 0; i < BATCH; i++) {
            final long value = longValues[i];
            for (final ScaleMetrics scale : metrics) {
                bh.consume(scale.divideUnsignedByScaleFactor(value));
            }
        }
        bh.consume(Scales.findByScaleFactor(100L));
    }

    // ---- rounding and overflow policies ------------------------------------

    @Benchmark
    public void roundingPolicies(final Blackhole bh) {
        for (final DecimalRounding rounding : DecimalRounding.VALUES) {
            if (rounding == DecimalRounding.UNNECESSARY) {
                continue;
            }
            bh.consume(rounding.calculateRoundingIncrement(
                    1, 123L, TruncatedPart.GREATER_THAN_HALF));
            bh.consume(rounding.calculateRoundingIncrement(
                    -1, 123L, TruncatedPart.EQUAL_TO_HALF));
        }
        for (final TruncatedPart part : TruncatedPart.values()) {
            bh.consume(part.isGreaterThanZero());
        }
        for (final CheckedRounding checked : CheckedRounding.VALUES) {
            bh.consume(checked.toUncheckedRounding());
            bh.consume(checked.getOverflowMode().isChecked());
        }
        for (final UncheckedRounding unchecked : UncheckedRounding.VALUES) {
            bh.consume(unchecked.toCheckedRounding());
        }
        bh.consume(OverflowMode.UNCHECKED.isChecked());
    }

    // ---- double rounding ---------------------------------------------------

    @Benchmark
    public double roundDoubles() {
        final DoubleRounder rounder = new DoubleRounder(5);
        double sum = rounder.getPrecision();
        for (int i = 0; i < BATCH; i++) {
            sum += rounder.round(doubleValues[i]);
            sum += DoubleRounder.round(doubleValues[i], 3, RoundingMode.HALF_EVEN);
        }
        return sum;
    }

    // ---- conversion out ----------------------------------------------------

    @Benchmark
    public void convertOut(final Blackhole bh) {
        for (int i = 0; i < BATCH; i++) {
            final Decimal<Scale5f> value = scale5[i];
            bh.consume(value.doubleValue());
            bh.consume(value.longValue());
            bh.consume(value.toBigDecimal());
            bh.consume(value.toString());
        }
    }
}

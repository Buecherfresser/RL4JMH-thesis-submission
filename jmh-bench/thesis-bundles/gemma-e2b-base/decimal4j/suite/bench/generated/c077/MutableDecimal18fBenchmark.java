package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.base.AbstractMutableDecimal;
import org.decimal4j.exact.Multipliable18f;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.mutable.MutableDecimal18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal18fBenchmark {

    // State fields for inputs
    private MutableDecimal18f mutableDecimal;
    private MutableDecimal18f otherDecimal;
    private BigDecimal bigDecimalValue;
    private double doubleValue;
    private long longValue;

    // Constants for operations
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int PRECISION = 5;

    @Setup
    public void setup() {
        // Initialize base mutable decimal
        mutableDecimal = MutableDecimal18f.zero();
        otherDecimal = MutableDecimal18f.one();

        // Initialize complex inputs
        longValue = 123456789012345L;
        bigDecimalValue = new BigDecimal("123456789012345.6789");
        doubleValue = 123456789012345.6789;
    }

    // --- Factory Benchmarks ---

    @Benchmark
    public void benchmark_zero_factory(Blackhole bh) {
        MutableDecimal18f result = MutableDecimal18f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_one_factory(Blackhole bh) {
        MutableDecimal18f result = MutableDecimal18f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_half_factory(Blackhole bh) {
        MutableDecimal18f result = MutableDecimal18f.half();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_tenth_factory(Blackhole bh) {
        MutableDecimal18f result = MutableDecimal18f.tenth();
        bh.consume(result);
    }

    // --- Mutator Benchmarks ---

    @Benchmark
    public void benchmark_set_double(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.set(doubleValue, ROUNDING_MODE);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_set_bigdecimal(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.set(bigDecimalValue, ROUNDING_MODE);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_set_unscaled(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.setUnscaled(longValue, 0);
        bh.consume(result);
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public void benchmark_add(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.add(otherDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_subtract(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.subtract(otherDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_multiply(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.multiply(otherDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_divide(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.divide(otherDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_remainder(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.remainder(otherDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_negate(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.negate();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_abs(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.abs();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_invert(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.invert();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_square(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.square();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_sqrt(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.sqrt();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_pow_int(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.pow(2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_avg(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.avg(otherDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_shift_left(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.shiftLeft(2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_round(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.round(PRECISION);
        bh.consume(result);
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public void benchmark_to_immutable(Blackhole bh) {
        Decimal18f result = mutableDecimal.toImmutableDecimal();
        bh.consume(result);
    }

    @Benchmark
    public void benchmark_to_mutable(Blackhole bh) {
        MutableDecimal18f result = mutableDecimal.toMutableDecimal();
        bh.consume(result);
    }
}

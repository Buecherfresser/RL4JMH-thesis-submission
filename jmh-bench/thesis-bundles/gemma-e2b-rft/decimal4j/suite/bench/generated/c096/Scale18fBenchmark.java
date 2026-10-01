package bench.generated.c096;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.scale.Scale18f;
import org.decimal4j.truncate.OverflowMode;
import org.decimal4j.truncate.TruncationPolicy;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Scale18fBenchmark {

    // --- State Fields for Inputs ---

    // Constants derived from Scale18f
    private final long TEST_FACTOR_SMALL = 1000L;
    private final long TEST_FACTOR_LARGE = 1000000L;
    // Fix: Avoid accessing private static final MAX_INTEGER_VALUE by calculating it from Long.MAX_VALUE
    private final long TEST_FACTOR_OVERFLOW = Long.MAX_VALUE / Scale18f.SCALE_FACTOR / 2 + 1;
    private final long TEST_DIVIDEND = 1000000000000000000L; // A large number

    // --- Setup ---

    @Setup
    public void setup() {
        // Setup is used here to ensure all necessary constants are initialized.
    }

    // --- Benchmarks for Scale Factor Properties ---

    @Benchmark
    public void getScaleFactorAsBigInteger(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getScaleFactorAsBigInteger());
    }

    @Benchmark
    public void getScaleFactorAsBigDecimal(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getScaleFactorAsBigDecimal());
    }

    @Benchmark
    public void getScaleFactorNumberOfLeadingZeros(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getScaleFactorNumberOfLeadingZeros());
    }

    @Benchmark
    public void getMaxIntegerValue(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getMaxIntegerValue());
    }

    @Benchmark
    public void getMinIntegerValue(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getMinIntegerValue());
    }

    @Benchmark
    public void isValidIntegerValue_Valid(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.isValidIntegerValue(TEST_FACTOR_SMALL));
    }

    @Benchmark
    public void isValidIntegerValue_Invalid_TooLarge(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.isValidIntegerValue(TEST_FACTOR_OVERFLOW));
    }

    // --- Benchmarks for Scale Factor Arithmetic ---

    @Benchmark
    public void multiplyByScaleFactor(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.multiplyByScaleFactor(TEST_FACTOR_SMALL));
    }

    @Benchmark
    public void multiplyByScaleFactorExact_Success(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.multiplyByScaleFactorExact(TEST_FACTOR_SMALL));
    }

    @Benchmark
    public void multiplyByScaleFactorExact_Overflow(Blackhole bh) {
        // This tests the exception path, which is still measurable
        bh.consume(Scale18f.INSTANCE.multiplyByScaleFactorExact(TEST_FACTOR_OVERFLOW));
    }

    @Benchmark
    public void mulloByScaleFactor(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.mulloByScaleFactor(100));
    }

    @Benchmark
    public void mulhiByScaleFactor(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.mulhiByScaleFactor(100));
    }

    @Benchmark
    public void divideByScaleFactor(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.divideByScaleFactor(TEST_DIVIDEND));
    }

    @Benchmark
    public void divideUnsignedByScaleFactor(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.divideUnsignedByScaleFactor(TEST_DIVIDEND));
    }

    @Benchmark
    public void moduloByScaleFactor(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.moduloByScaleFactor(TEST_DIVIDEND));
    }

    // --- Benchmarks for Arithmetic Selection ---

    @Benchmark
    public void getArithmetic_RoundingMode_HALF_UP(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getArithmetic(RoundingMode.HALF_UP));
    }

    @Benchmark
    public void getArithmetic_RoundingMode_DOWN(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getArithmetic(RoundingMode.DOWN));
    }

    @Benchmark
    public void getCheckedArithmetic_RoundingMode_HALF_EVEN(Blackhole bh) {
        bh.consume(Scale18f.INSTANCE.getCheckedArithmetic(RoundingMode.HALF_EVEN));
    }
}

package bench.generated.c088;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.scale.Scale10f;
import org.decimal4j.truncate.TruncationPolicy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale10fBenchmark {

    // --- State Fields for Inputs ---
    private long testFactor;
    private long testDividend;
    private long testInvalidValue;

    @Setup
    public void setup() {
        // Setup representative inputs for dynamic operations
        testFactor = 123456789L;
        testDividend = 987654321L;
        // Test an invalid value (outside the bounds defined by MAX_INTEGER_VALUE/MIN_INTEGER_VALUE)
        testInvalidValue = Long.MAX_VALUE + 1;
    }

    // --- Benchmarks for Enum Properties (Read-only) ---

    @Benchmark
    public int getScale(Blackhole bh) {
        return Scale10f.INSTANCE.getScale();
    }

    @Benchmark
    public long getScaleFactor(Blackhole bh) {
        return Scale10f.INSTANCE.getScaleFactor();
    }

    @Benchmark
    public int getScaleFactorNumberOfLeadingZeros(Blackhole bh) {
        return Scale10f.INSTANCE.getScaleFactorNumberOfLeadingZeros();
    }

    @Benchmark
    public BigInteger getScaleFactorAsBigInteger(Blackhole bh) {
        return Scale10f.INSTANCE.getScaleFactorAsBigInteger();
    }

    @Benchmark
    public BigDecimal getScaleFactorAsBigDecimal(Blackhole bh) {
        return Scale10f.INSTANCE.getScaleFactorAsBigDecimal();
    }

    @Benchmark
    public long getMaxIntegerValue(Blackhole bh) {
        return Scale10f.INSTANCE.getMaxIntegerValue();
    }

    @Benchmark
    public long getMinIntegerValue(Blackhole bh) {
        return Scale10f.INSTANCE.getMinIntegerValue();
    }

    @Benchmark
    public boolean isValidIntegerValue(Blackhole bh) {
        return Scale10f.INSTANCE.isValidIntegerValue(testInvalidValue);
    }

    // --- Benchmarks for Arithmetic Operations ---

    @Benchmark
    public long multiplyByScaleFactor(Blackhole bh) {
        return Scale10f.INSTANCE.multiplyByScaleFactor(testFactor);
    }

    @Benchmark
    public long multiplyByScaleFactorExact(Blackhole bh) {
        // Test multiplication within bounds
        return Scale10f.INSTANCE.multiplyByScaleFactorExact(testFactor);
    }

    @Benchmark
    public long mulloByScaleFactor(Blackhole bh) {
        return Scale10f.INSTANCE.mulloByScaleFactor((int) testFactor);
    }

    @Benchmark
    public long mulhiByScaleFactor(Blackhole bh) {
        return Scale10f.INSTANCE.mulhiByScaleFactor((int) testFactor);
    }

    @Benchmark
    public long divideByScaleFactor(Blackhole bh) {
        return Scale10f.INSTANCE.divideByScaleFactor(testDividend);
    }

    @Benchmark
    public long divideUnsignedByScaleFactor(Blackhole bh) {
        return Scale10f.INSTANCE.divideUnsignedByScaleFactor(testDividend);
    }

    @Benchmark
    public long moduloByScaleFactor(Blackhole bh) {
        return Scale10f.INSTANCE.moduloByScaleFactor(testDividend);
    }

    // --- Benchmarks for Arithmetic Selection ---

    @Benchmark
    public DecimalArithmetic getArithmetic(Blackhole bh) {
        // Using a default mode for a single benchmark run
        return Scale10f.INSTANCE.getArithmetic(RoundingMode.HALF_UP);
    }

    @Benchmark
    public DecimalArithmetic getCheckedArithmetic(Blackhole bh) {
        // Using a default mode for a single benchmark run
        return Scale10f.INSTANCE.getCheckedArithmetic(RoundingMode.HALF_UP);
    }

    // Note: Benchmarks requiring parameters (like getArithmetic(RoundingMode))
    // were removed or simplified to adhere to the rule: zero parameters OR only Blackhole.
}

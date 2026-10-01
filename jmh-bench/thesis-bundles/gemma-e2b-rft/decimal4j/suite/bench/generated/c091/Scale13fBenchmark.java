package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.scale.Scale13f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Scale13fBenchmark {

    // State fields for inputs
    private long validInput;
    private long overflowInput;
    private long underflowInput;
    private int factorInput;
    private long dividendInput;
    private long unsignedDividendInput;

    @Setup
    public void setup() {
        // Retrieve constants via public methods from the Scale13f instance
        long maxVal = Scale13f.INSTANCE.getMaxIntegerValue();
        long minVal = Scale13f.INSTANCE.getMinIntegerValue();

        // Setup valid input (e.g., near the middle of the valid range)
        this.validInput = maxVal / 2;

        // Setup overflow input (a factor that causes multiplication overflow)
        // We use a factor slightly larger than MAX_INTEGER_VALUE
        this.overflowInput = maxVal + 1;

        // Setup underflow input (a factor that causes multiplication underflow)
        this.underflowInput = minVal - 1;

        // Setup factor input (a factor within the valid range)
        this.factorInput = 1000;

        // Setup dividend input (a large number)
        this.dividendInput = 1_000_000_000_000L;

        // Setup unsigned dividend input
        this.unsignedDividendInput = Long.MAX_VALUE;
    }

    @Benchmark
    public void testIsValidIntegerValue(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.isValidIntegerValue(validInput));
        bh.consume(Scale13f.INSTANCE.isValidIntegerValue(Scale13f.INSTANCE.getMaxIntegerValue()));
        bh.consume(Scale13f.INSTANCE.isValidIntegerValue(Scale13f.INSTANCE.getMinIntegerValue()));
    }

    @Benchmark
    public void testMultiplyByScaleFactor(Blackhole bh) {
        // Test successful multiplication
        bh.consume(Scale13f.INSTANCE.multiplyByScaleFactor(factorInput));

        // Test overflow (expecting ArithmeticException)
        try {
            bh.consume(Scale13f.INSTANCE.multiplyByScaleFactor(overflowInput));
        } catch (ArithmeticException e) {
            // Expected exception
        }
    }

    @Benchmark
    public void testMulloByScaleFactor(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.mulloByScaleFactor(factorInput));
    }

    @Benchmark
    public void testMulhiByScaleFactor(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.mulhiByScaleFactor(factorInput));
    }

    @Benchmark
    public void testDivideByScaleFactor(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.divideByScaleFactor(dividendInput));
    }

    @Benchmark
    public void testDivideUnsignedByScaleFactor(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.divideUnsignedByScaleFactor(unsignedDividendInput));
    }

    @Benchmark
    public void testModuloByScaleFactor(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.moduloByScaleFactor(dividendInput));
    }

    @Benchmark
    public void testToString(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.toString(validInput));
    }

    @Benchmark
    public void testGetDefaultArithmetic(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.getDefaultArithmetic());
    }

    @Benchmark
    public void testGetCheckedArithmetic(Blackhole bh) {
        bh.consume(Scale13f.INSTANCE.getDefaultCheckedArithmetic());
    }
}

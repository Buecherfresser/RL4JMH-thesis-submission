package bench.generated.c060;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.factory.Factory2f;
import org.decimal4j.exact.Multipliable2f;
import org.decimal4j.mutable.MutableDecimal2f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal2fBenchmark {

    private String testString;
    private double testDouble;
    private BigDecimal testBigDecimal;
    private BigInteger testBigInteger;
    private Decimal2f testDecimal;
    private Decimal2f testDecimal2;
    private RoundingMode testRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        // Setup inputs for various conversion methods
        testString = "12345.67";
        testDouble = 987.654321;
        testBigDecimal = new BigDecimal("12345.6789");
        testBigInteger = new BigInteger("987654321012345");
        testDecimal = Decimal2f.valueOf(12345L);
        testDecimal2 = Decimal2f.valueOf(987.65f);
        testRoundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public void benchmarkStringConversionDefault(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testString));
    }

    @Benchmark
    public void benchmarkStringConversionRounding(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testString, testRoundingMode));
    }

    @Benchmark
    public void benchmarkDoubleConversionDefault(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testDouble));
    }

    @Benchmark
    public void benchmarkDoubleConversionRounding(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testDouble, testRoundingMode));
    }

    @Benchmark
    public void benchmarkBigDecimalConversionDefault(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testBigDecimal));
    }

    @Benchmark
    public void benchmarkBigDecimalConversionRounding(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testBigDecimal, testRoundingMode));
    }

    @Benchmark
    public void benchmarkBigIntegerConversion(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testBigInteger));
    }

    @Benchmark
    public void benchmarkDecimalConversionDefault(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testDecimal));
    }

    @Benchmark
    public void benchmarkDecimalConversionRounding(Blackhole bh) {
        bh.consume(Decimal2f.valueOf(testDecimal, testRoundingMode));
    }

    @Benchmark
    public void benchmarkToMutableDecimal(Blackhole bh) {
        bh.consume(testDecimal.toMutableDecimal());
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        bh.consume(testDecimal.multiplyExact());
    }

    @Benchmark
    public void benchmarkMultiplicationExact(Blackhole bh) {
        // Use the result of multiplyExact() and then multiply by another Decimal2f
        bh.consume(testDecimal.multiplyExact().by(testDecimal2));
    }

    @Benchmark
    public void benchmarkGetScale(Blackhole bh) {
        bh.consume(testDecimal.getScale());
    }

    @Benchmark
    public void benchmarkGetScaleMetrics(Blackhole bh) {
        bh.consume(testDecimal.getScaleMetrics());
    }

    @Benchmark
    public void benchmarkGetFactory(Blackhole bh) {
        bh.consume(testDecimal.getFactory());
    }
}

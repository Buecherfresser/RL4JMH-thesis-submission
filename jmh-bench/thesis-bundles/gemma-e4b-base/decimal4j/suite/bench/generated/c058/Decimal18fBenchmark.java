package bench.generated.c058;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal18f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable18f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.mutable.MutableDecimal18f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal18fBenchmark {

    private long testLong;
    private String testString;
    private double testDouble;
    private BigDecimal testBigDecimal;
    private Decimal18f testDecimal18f;
    private long testUnscaledValue;
    private int testScale;
    private RoundingMode testRoundingMode;

    @Setup
    public void setup() {
        // 1. Long input
        testLong = 1234567890123L;

        // 2. String input (high precision)
        testString = "123.4567890123456789";

        // 3. Double input
        testDouble = 123.45678901234567;

        // 4. BigDecimal input
        testBigDecimal = new BigDecimal("123.4567890123456789");

        // 5. Decimal<?> input (using Decimal18f itself)
        testDecimal18f = Decimal18f.valueOf(123.4567890123456789);

        // 6. Unscaled value input
        testUnscaledValue = 987654321L;
        testScale = 10;
        testRoundingMode = RoundingMode.HALF_UP;
    }

    // --- Conversion Benchmarks ---

    @Benchmark
    public Decimal18f benchmarkValueOfLong(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfStringDefault(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfStringRounding(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testString, testRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDoubleDefault(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDoubleRounding(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testDouble, testRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfBigDecimalDefault(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfBigDecimalRounding(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testBigDecimal, testRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDecimalDefault(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testDecimal18f);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfDecimalRounding(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOf(testDecimal18f, testRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkValueOfUnscaledRounding(Blackhole bh) {
        Decimal18f result = Decimal18f.valueOfUnscaled(testUnscaledValue, testScale, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public Multipliable18f benchmarkMultiplyExact(Blackhole bh) {
        Multipliable18f result = testDecimal18f.multiplyExact();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal18f benchmarkToMutableDecimal(Blackhole bh) {
        MutableDecimal18f result = testDecimal18f.toMutableDecimal();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal18f benchmarkToImmutableDecimal(Blackhole bh) {
        Decimal18f result = testDecimal18f.toImmutableDecimal();
        bh.consume(result);
        return result;
    }
}

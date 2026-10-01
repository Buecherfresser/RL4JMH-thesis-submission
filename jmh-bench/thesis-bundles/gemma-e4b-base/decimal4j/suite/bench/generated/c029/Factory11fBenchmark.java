package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory11f;
import org.decimal4j.immutable.Decimal11f;
import org.decimal4j.mutable.MutableDecimal11f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory11fBenchmark {

    private Factory11f factory;

    // Inputs for testing
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal<?> testDecimal;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory11f.INSTANCE;

        // Initialize representative inputs
        testLong = 1234567890123L;
        testFloat = 123.4567f;
        testDouble = 123.45678901234567;
        testBigInteger = new BigInteger("9876543210987654321");
        testBigDecimal = new BigDecimal("123.4567890123456789");
        testString = "123.45678901234567";
        
        // Create a representative Decimal<?> instance (using a simple Decimal11f)
        testDecimal = Decimal11f.valueOf(123.45678901234567);
    }

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        Decimal11f result = factory.valueOf(testLong);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        Decimal11f result = factory.valueOf(testFloat);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfFloatWithRoundingMode(Blackhole bh) {
        Decimal11f result = factory.valueOf(testFloat, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        Decimal11f result = factory.valueOf(testDouble);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDoubleWithRoundingMode(Blackhole bh) {
        Decimal11f result = factory.valueOf(testDouble, RoundingMode.HALF_DOWN);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        Decimal11f result = factory.valueOf(testBigInteger);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        Decimal11f result = factory.valueOf(testBigDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfBigDecimalWithRoundingMode(Blackhole bh) {
        Decimal11f result = factory.valueOf(testBigDecimal, RoundingMode.CEILING);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDecimal(Blackhole bh) {
        Decimal11f result = factory.valueOf(testDecimal);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfDecimalWithRoundingMode(Blackhole bh) {
        Decimal11f result = factory.valueOf(testDecimal, RoundingMode.DOWN);
        bh.consume(result);
    }

    @Benchmark
    public void testParseString(Blackhole bh) {
        Decimal11f result = factory.parse(testString);
        bh.consume(result);
    }

    @Benchmark
    public void testParseStringWithRoundingMode(Blackhole bh) {
        Decimal11f result = factory.parse(testString, RoundingMode.HALF_EVEN);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        Decimal11f result = factory.valueOfUnscaled(testLong);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLongWithScale(Blackhole bh) {
        // Using a small scale for the test
        Decimal11f result = factory.valueOfUnscaled(testLong, 5);
        bh.consume(result);
    }

    @Benchmark
    public void testValueOfUnscaledLongWithScaleAndRoundingMode(Blackhole bh) {
        // Using a small scale for the test
        Decimal11f result = factory.valueOfUnscaled(testLong, 5, RoundingMode.UP);
        bh.consume(result);
    }

    @Benchmark
    public void testNewArray(Blackhole bh) {
        int length = 100;
        Decimal11f[] result = factory.newArray(length);
        bh.consume(result);
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        MutableDecimal11f result = factory.newMutable();
        bh.consume(result);
    }

    @Benchmark
    public void testNewMutableArray(Blackhole bh) {
        int length = 100;
        MutableDecimal11f[] result = factory.newMutableArray(length);
        bh.consume(result);
    }
}

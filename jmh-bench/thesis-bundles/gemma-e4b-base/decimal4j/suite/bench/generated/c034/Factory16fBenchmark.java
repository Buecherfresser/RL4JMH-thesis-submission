package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory16f;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.mutable.MutableDecimal16f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory16fBenchmark {

    private Factory16f factory;

    // Inputs for valueOf methods
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal<?> testDecimal;

    // Inputs for unscaled methods
    private long testUnscaledLong;
    private int testScaleInt;
    private RoundingMode testRoundingMode;

    // Inputs for array methods
    private int arrayLength;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory16f.INSTANCE;

        // Initialize inputs
        testLong = 123456789L;
        testFloat = 123.456f;
        testDouble = 123.4567890123456;
        testBigInteger = new BigInteger("9876543210987654321");
        testBigDecimal = new BigDecimal("987654321.123456789");
        testString = "123.456";
        testDecimal = Decimal16f.valueOf(1.0);

        testUnscaledLong = 500000000L;
        testScaleInt = 16;
        testRoundingMode = RoundingMode.HALF_UP;

        arrayLength = 100;
    }

    // --- Basic Property Lookups ---

    @Benchmark
    public void benchmarkGetScaleMetrics(Blackhole bh) {
        bh.consume(factory.getScaleMetrics());
    }

    @Benchmark
    public void benchmarkGetScale(Blackhole bh) {
        bh.consume(factory.getScale());
    }

    @Benchmark
    public void benchmarkImmutableType(Blackhole bh) {
        bh.consume(factory.immutableType());
    }

    @Benchmark
    public void benchmarkMutableType(Blackhole bh) {
        bh.consume(factory.mutableType());
    }

    // --- Factory Derivation ---

    @Benchmark
    public void benchmarkDeriveFactoryInt(Blackhole bh) {
        bh.consume(factory.deriveFactory(10));
    }

    @Benchmark
    public void benchmarkDeriveFactoryScaleMetrics(Blackhole bh) {
        bh.consume(factory.deriveFactory(Factory16f.INSTANCE.getScaleMetrics()));
    }

    // --- ValueOf (Primitive/Wrapper) ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        bh.consume(factory.valueOf(testLong));
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        bh.consume(factory.valueOf(testFloat));
    }

    @Benchmark
    public void benchmarkValueOfFloatRounding(Blackhole bh) {
        bh.consume(factory.valueOf(testFloat, testRoundingMode));
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        bh.consume(factory.valueOf(testDouble));
    }

    @Benchmark
    public void benchmarkValueOfDoubleRounding(Blackhole bh) {
        bh.consume(factory.valueOf(testDouble, testRoundingMode));
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        bh.consume(factory.valueOf(testBigInteger));
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(testBigDecimal));
    }

    @Benchmark
    public void benchmarkValueOfBigDecimalRounding(Blackhole bh) {
        bh.consume(factory.valueOf(testBigDecimal, testRoundingMode));
    }

    @Benchmark
    public void benchmarkValueOfDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(testDecimal));
    }

    @Benchmark
    public void benchmarkValueOfDecimalRounding(Blackhole bh) {
        bh.consume(factory.valueOf(testDecimal, testRoundingMode));
    }

    // --- Parsing ---

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        bh.consume(factory.parse(testString));
    }

    @Benchmark
    public void benchmarkParseStringRounding(Blackhole bh) {
        bh.consume(factory.parse(testString, testRoundingMode));
    }

    // --- ValueOfUnscaled ---

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(testUnscaledLong));
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongScale(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(testUnscaledLong, testScaleInt));
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongScaleRounding(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(testUnscaledLong, testScaleInt, testRoundingMode));
    }

    // --- Array Creation ---

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        // We consume the array reference, but JMH might optimize array creation away if not careful.
        // Since we are measuring the factory call, this is acceptable.
        bh.consume(factory.newArray(arrayLength));
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        bh.consume(factory.newMutable());
    }

    @Benchmark
    public void benchmarkNewMutableArray(Blackhole bh) {
        bh.consume(factory.newMutableArray(arrayLength));
    }
}

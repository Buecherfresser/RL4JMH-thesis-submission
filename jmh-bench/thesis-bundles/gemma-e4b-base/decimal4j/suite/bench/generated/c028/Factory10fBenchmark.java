package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.scale.Scale10f;
import org.decimal4j.api.Decimal;
import org.decimal4j.mutable.MutableDecimal10f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory10fBenchmark {

    private Factory10f factory;

    // Inputs for testing
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal10f testDecimal;
    private RoundingMode testRoundingMode;
    private int arrayLength;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory10f.INSTANCE;

        // Initialize inputs
        testLong = 1234567890123L;
        testFloat = 3.14159f;
        testDouble = 3.1415926535;
        testBigInteger = new BigInteger("9876543210987654321");
        testBigDecimal = new BigDecimal("123.45678901234567");
        testString = "123.4567890123";
        testDecimal = Decimal10f.valueOf(1.0);
        testRoundingMode = RoundingMode.HALF_UP;
        arrayLength = 100;
    }

    // --- Basic Factory Lookups ---

    @Benchmark
    public Scale10f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal10f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal10f> mutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(10);
    }

    @Benchmark
    public DecimalFactory<Scale10f> deriveFactoryScaleMetrics() {
        return factory.deriveFactory(Scale10f.INSTANCE);
    }

    // --- ValueOf (Immutable Decimal10f creation) ---

    @Benchmark
    public Decimal10f valueOfLong() {
        return factory.valueOf(testLong);
    }

    @Benchmark
    public Decimal10f valueOfFloat() {
        return factory.valueOf(testFloat);
    }

    @Benchmark
    public Decimal10f valueOfFloatWithRoundingMode() {
        return factory.valueOf(testFloat, testRoundingMode);
    }

    @Benchmark
    public Decimal10f valueOfDouble() {
        return factory.valueOf(testDouble);
    }

    @Benchmark
    public Decimal10f valueOfDoubleWithRoundingMode() {
        return factory.valueOf(testDouble, testRoundingMode);
    }

    @Benchmark
    public Decimal10f valueOfBigInteger() {
        return factory.valueOf(testBigInteger);
    }

    @Benchmark
    public Decimal10f valueOfBigDecimal() {
        return factory.valueOf(testBigDecimal);
    }

    @Benchmark
    public Decimal10f valueOfBigDecimalWithRoundingMode() {
        return factory.valueOf(testBigDecimal, testRoundingMode);
    }

    @Benchmark
    public Decimal10f valueOfDecimal() {
        return factory.valueOf(testDecimal);
    }

    @Benchmark
    public Decimal10f valueOfDecimalWithRoundingMode() {
        return factory.valueOf(testDecimal, testRoundingMode);
    }

    // --- Parsing ---

    @Benchmark
    public Decimal10f parseString() {
        return factory.parse(testString);
    }

    @Benchmark
    public Decimal10f parseStringWithRoundingMode() {
        return factory.parse(testString, testRoundingMode);
    }

    // --- ValueOfUnscaled ---

    @Benchmark
    public Decimal10f valueOfUnscaledLong() {
        return factory.valueOfUnscaled(testLong);
    }

    @Benchmark
    public Decimal10f valueOfUnscaledLongWithScale() {
        return factory.valueOfUnscaled(testLong, 5);
    }

    @Benchmark
    public Decimal10f valueOfUnscaledLongWithScaleAndRoundingMode() {
        return factory.valueOfUnscaled(testLong, 5, testRoundingMode);
    }

    // --- Array Creation ---

    @Benchmark
    public Decimal10f[] newArray() {
        return factory.newArray(arrayLength);
    }

    @Benchmark
    public MutableDecimal10f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal10f[] newMutableArray() {
        return factory.newMutableArray(arrayLength);
    }
}

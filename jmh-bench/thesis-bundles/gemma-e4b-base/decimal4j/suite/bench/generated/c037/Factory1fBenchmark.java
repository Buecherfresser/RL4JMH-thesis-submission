package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory1f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.scale.Scale1f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory1fBenchmark {

    private Factory1f factory;

    // Inputs for various methods
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal1f testDecimal1f;
    private RoundingMode testRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory1f.INSTANCE;

        // Initialize inputs
        testLong = 1234567890123L;
        testFloat = 3.14159f;
        testDouble = 3.141592653589793;
        testBigInteger = new BigInteger("9876543210987654321");
        testBigDecimal = new BigDecimal("123.456789");
        testString = "123.456";
        testRoundingMode = RoundingMode.HALF_UP;

        // Initialize Decimal1f input
        testDecimal1f = Decimal1f.valueOf(testBigDecimal);
    }

    // --- Basic Factory Methods ---

    @Benchmark
    public Scale1f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal1f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal1f> mutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(1);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<Scale1f> deriveFactoryScaleMetrics() {
        return factory.deriveFactory(Scale1f.INSTANCE);
    }

    // --- ValueOf Methods (Immutable Decimal1f creation) ---

    @Benchmark
    public Decimal1f valueOfLong() {
        return factory.valueOf(testLong);
    }

    @Benchmark
    public Decimal1f valueOfFloat() {
        return factory.valueOf(testFloat);
    }

    @Benchmark
    public Decimal1f valueOfFloatWithRoundingMode() {
        return factory.valueOf(testFloat, testRoundingMode);
    }

    @Benchmark
    public Decimal1f valueOfDouble() {
        return factory.valueOf(testDouble);
    }

    @Benchmark
    public Decimal1f valueOfDoubleWithRoundingMode() {
        return factory.valueOf(testDouble, testRoundingMode);
    }

    @Benchmark
    public Decimal1f valueOfBigInteger() {
        return factory.valueOf(testBigInteger);
    }

    @Benchmark
    public Decimal1f valueOfBigDecimal() {
        return factory.valueOf(testBigDecimal);
    }

    @Benchmark
    public Decimal1f valueOfBigDecimalWithRoundingMode() {
        return factory.valueOf(testBigDecimal, testRoundingMode);
    }

    @Benchmark
    public Decimal1f valueOfDecimal() {
        return factory.valueOf(testDecimal1f);
    }

    @Benchmark
    public Decimal1f valueOfDecimalWithRoundingMode() {
        return factory.valueOf(testDecimal1f, testRoundingMode);
    }

    // --- Parse Methods ---

    @Benchmark
    public Decimal1f parseString() {
        return factory.parse(testString);
    }

    @Benchmark
    public Decimal1f parseStringWithRoundingMode() {
        return factory.parse(testString, testRoundingMode);
    }

    // --- Unscaled ValueOf Methods ---

    @Benchmark
    public Decimal1f valueOfUnscaledLong() {
        return factory.valueOfUnscaled(testLong);
    }

    @Benchmark
    public Decimal1f valueOfUnscaledLongWithScale() {
        // Using scale 1 as required by the method signature, though the input scale is irrelevant for Factory1f
        return factory.valueOfUnscaled(testLong, 1);
    }

    @Benchmark
    public Decimal1f valueOfUnscaledLongWithScaleAndRoundingMode() {
        return factory.valueOfUnscaled(testLong, 1, testRoundingMode);
    }

    // --- Array/Mutable Methods ---

    @Benchmark
    public Decimal1f[] newArray() {
        return factory.newArray(10);
    }

    @Benchmark
    public MutableDecimal1f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal1f[] newMutableArray() {
        return factory.newMutableArray(10);
    }
}

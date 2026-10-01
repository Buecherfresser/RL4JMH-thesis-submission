package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory3f;
import org.decimal4j.immutable.Decimal3f;
import org.decimal4j.scale.Scale3f;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory3fBenchmark {

    private Factory3f factory;

    // Inputs for testing
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal3f testDecimal;

    @Setup
    public void setup() {
        factory = Factory3f.INSTANCE;

        // Initialize inputs
        testLong = 1234567890L;
        testFloat = 123.456f;
        testDouble = 123.4567890123;
        testBigInteger = new BigInteger("9876543210987654321");
        testBigDecimal = new BigDecimal("123.456789");
        testString = "123.456";
        
        // Create a representative Decimal3f instance
        testDecimal = Decimal3f.valueOf(testDouble);
    }

    // --- Basic Factory Info Methods ---

    @Benchmark
    public Scale3f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal3f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<org.decimal4j.mutable.MutableDecimal3f> mutableType() {
        return factory.mutableType();
    }

    // --- Derive Factory Methods ---

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(3);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<Scale3f> deriveFactoryScaleMetrics() {
        return factory.deriveFactory(Scale3f.INSTANCE);
    }

    // --- ValueOf (Immutable) Methods ---

    @Benchmark
    public Decimal3f valueOfLong() {
        return factory.valueOf(testLong);
    }

    @Benchmark
    public Decimal3f valueOfFloat() {
        return factory.valueOf(testFloat);
    }

    @Benchmark
    public Decimal3f valueOfFloatRoundingMode() {
        return factory.valueOf(testFloat, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal3f valueOfDouble() {
        return factory.valueOf(testDouble);
    }

    @Benchmark
    public Decimal3f valueOfDoubleRoundingMode() {
        return factory.valueOf(testDouble, RoundingMode.HALF_DOWN);
    }

    @Benchmark
    public Decimal3f valueOfBigInteger() {
        return factory.valueOf(testBigInteger);
    }

    @Benchmark
    public Decimal3f valueOfBigDecimal() {
        return factory.valueOf(testBigDecimal);
    }

    @Benchmark
    public Decimal3f valueOfBigDecimalRoundingMode() {
        return factory.valueOf(testBigDecimal, RoundingMode.CEILING);
    }

    @Benchmark
    public Decimal3f valueOfDecimal() {
        return factory.valueOf(testDecimal);
    }

    @Benchmark
    public Decimal3f valueOfDecimalRoundingMode() {
        return factory.valueOf(testDecimal, RoundingMode.UNNECESSARY);
    }

    // --- Parse Methods ---

    @Benchmark
    public Decimal3f parseString() {
        return factory.parse(testString);
    }

    @Benchmark
    public Decimal3f parseStringRoundingMode() {
        return factory.parse(testString, RoundingMode.HALF_UP);
    }

    // --- ValueOfUnscaled Methods ---

    @Benchmark
    public Decimal3f valueOfUnscaledLong() {
        return factory.valueOfUnscaled(testLong);
    }

    @Benchmark
    public Decimal3f valueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(testLong, 5);
    }

    @Benchmark
    public Decimal3f valueOfUnscaledLongScaleRoundingMode() {
        return factory.valueOfUnscaled(testLong, 5, RoundingMode.DOWN);
    }

    // --- Array/Mutable Methods ---

    @Benchmark
    public Decimal3f[] newArray() {
        return factory.newArray(10);
    }

    @Benchmark
    public org.decimal4j.mutable.MutableDecimal3f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public org.decimal4j.mutable.MutableDecimal3f[] newMutableArray() {
        return factory.newMutableArray(10);
    }
}

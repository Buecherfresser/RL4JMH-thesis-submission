package bench.generated.c038;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory2f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.factory.Factories;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory2fBenchmark {

    private Factory2f factory = Factory2f.INSTANCE;

    // Inputs for various methods
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal2f testDecimal;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize inputs
        testLong = 123456789L;
        testFloat = 123.45f;
        testDouble = 123.4567890123456789;
        testBigInteger = new BigInteger("1234567890123456789");
        testBigDecimal = new BigDecimal("123.456789");
        testString = "123.45";
        
        // Initialize a representative Decimal<?>
        testDecimal = Decimal2f.valueOf(testDouble);
    }

    // --- Basic Factory Properties ---

    @Benchmark
    public ScaleMetrics testGetScaleMetrics(Blackhole bh) {
        bh.consume(factory.getScaleMetrics());
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int testGetScale(Blackhole bh) {
        bh.consume(factory.getScale());
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal2f> testImmutableType(Blackhole bh) {
        bh.consume(factory.immutableType());
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal2f> testMutableType(Blackhole bh) {
        bh.consume(factory.mutableType());
        return factory.mutableType();
    }

    // --- Derive Factory Methods ---

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> testDeriveFactoryInt(Blackhole bh) {
        bh.consume(factory.deriveFactory(2));
        return factory.deriveFactory(2);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<ScaleMetrics> testDeriveFactoryScaleMetrics(Blackhole bh) {
        bh.consume(factory.deriveFactory(Scale2f.INSTANCE));
        return factory.deriveFactory(Scale2f.INSTANCE);
    }

    // --- ValueOf (Immutable) Methods ---

    @Benchmark
    public Decimal2f testValueOfLong(Blackhole bh) {
        bh.consume(factory.valueOf(testLong));
        return factory.valueOf(testLong);
    }

    @Benchmark
    public Decimal2f testValueOfFloat(Blackhole bh) {
        bh.consume(factory.valueOf(testFloat));
        return factory.valueOf(testFloat);
    }

    @Benchmark
    public Decimal2f testValueOfFloatRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(testFloat, RoundingMode.HALF_UP));
        return factory.valueOf(testFloat, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal2f testValueOfDouble(Blackhole bh) {
        bh.consume(factory.valueOf(testDouble));
        return factory.valueOf(testDouble);
    }

    @Benchmark
    public Decimal2f testValueOfDoubleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(testDouble, RoundingMode.HALF_UP));
        return factory.valueOf(testDouble, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal2f testValueOfBigInteger(Blackhole bh) {
        bh.consume(factory.valueOf(testBigInteger));
        return factory.valueOf(testBigInteger);
    }

    @Benchmark
    public Decimal2f testValueOfBigDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(testBigDecimal));
        return factory.valueOf(testBigDecimal);
    }

    @Benchmark
    public Decimal2f testValueOfBigDecimalRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(testBigDecimal, RoundingMode.HALF_UP));
        return factory.valueOf(testBigDecimal, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal2f testValueOfDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(testDecimal));
        return factory.valueOf(testDecimal);
    }

    @Benchmark
    public Decimal2f testValueOfDecimalRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(testDecimal, RoundingMode.HALF_UP));
        return factory.valueOf(testDecimal, RoundingMode.HALF_UP);
    }

    // --- Parse Methods ---

    @Benchmark
    public Decimal2f testParseString(Blackhole bh) {
        bh.consume(factory.parse(testString));
        return factory.parse(testString);
    }

    @Benchmark
    public Decimal2f testParseStringRoundingMode(Blackhole bh) {
        bh.consume(factory.parse(testString, RoundingMode.HALF_UP));
        return factory.parse(testString, RoundingMode.HALF_UP);
    }

    // --- ValueOfUnscaled Methods ---

    @Benchmark
    public Decimal2f testValueOfUnscaledLong(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(testLong));
        return factory.valueOfUnscaled(testLong);
    }

    @Benchmark
    public Decimal2f testValueOfUnscaledLongScale(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(testLong, 2));
        return factory.valueOfUnscaled(testLong, 2);
    }

    @Benchmark
    public Decimal2f testValueOfUnscaledLongScaleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(testLong, 2, RoundingMode.HALF_UP));
        return factory.valueOfUnscaled(testLong, 2, RoundingMode.HALF_UP);
    }

    // --- Array and Mutable Methods ---

    @Benchmark
    public Decimal2f[] testNewArray(Blackhole bh) {
        int length = 10;
        bh.consume(factory.newArray(length));
        return factory.newArray(length);
    }

    @Benchmark
    public MutableDecimal2f testNewMutable(Blackhole bh) {
        bh.consume(factory.newMutable());
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal2f[] testNewMutableArray(Blackhole bh) {
        int length = 10;
        bh.consume(factory.newMutableArray(length));
        return factory.newMutableArray(length);
    }
}

package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory8f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.scale.Scale8f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory8fBenchmark {

    private Factory8f factory;

    // Inputs for testing various valueOf/parse methods
    private long testLong;
    private float testFloat;
    private double testDouble;
    private BigInteger testBigInteger;
    private BigDecimal testBigDecimal;
    private String testString;
    private Decimal8f testDecimal;
    private RoundingMode testRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory8f.INSTANCE;

        // Initialize inputs
        testLong = 1234567890123L;
        testFloat = 123.4567f;
        testDouble = 123.45678901234567;
        testBigInteger = new BigInteger("9876543210987654321");
        testBigDecimal = new BigDecimal("987654321.12345678");
        testString = "123.45678901";
        testRoundingMode = RoundingMode.HALF_UP;

        // Initialize a Decimal8f instance for Decimal<?> tests
        testDecimal = Decimal8f.valueOf(testDouble);
    }

    // --- Simple Getter Methods ---

    @Benchmark
    public Scale8f getScaleMetrics(Blackhole bh) {
        bh.consume(factory.getScaleMetrics());
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale(Blackhole bh) {
        bh.consume(factory.getScale());
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal8f> immutableType(Blackhole bh) {
        bh.consume(factory.immutableType());
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal8f> mutableType(Blackhole bh) {
        bh.consume(factory.mutableType());
        return factory.mutableType();
    }

    // --- Factory Derivation Methods ---

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryInt(Blackhole bh) {
        bh.consume(factory.deriveFactory(8));
        return factory.deriveFactory(8);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryScaleMetrics(Blackhole bh) {
        bh.consume(factory.deriveFactory(Scale8f.INSTANCE));
        return factory.deriveFactory(Scale8f.INSTANCE);
    }

    // --- ValueOf Methods (Immutable Decimal8f creation) ---

    @Benchmark
    public Decimal8f valueOfLong(Blackhole bh) {
        Decimal8f result = factory.valueOf(testLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfFloat(Blackhole bh) {
        Decimal8f result = factory.valueOf(testFloat);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfFloatWithRoundingMode(Blackhole bh) {
        Decimal8f result = factory.valueOf(testFloat, testRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfDouble(Blackhole bh) {
        Decimal8f result = factory.valueOf(testDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfDoubleWithRoundingMode(Blackhole bh) {
        Decimal8f result = factory.valueOf(testDouble, testRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfBigInteger(Blackhole bh) {
        Decimal8f result = factory.valueOf(testBigInteger);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfBigDecimal(Blackhole bh) {
        Decimal8f result = factory.valueOf(testBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfBigDecimalWithRoundingMode(Blackhole bh) {
        Decimal8f result = factory.valueOf(testBigDecimal, testRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfDecimal(Blackhole bh) {
        Decimal8f result = factory.valueOf(testDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfDecimalWithRoundingMode(Blackhole bh) {
        Decimal8f result = factory.valueOf(testDecimal, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- Parsing Methods ---

    @Benchmark
    public Decimal8f parseString(Blackhole bh) {
        Decimal8f result = factory.parse(testString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f parseStringWithRoundingMode(Blackhole bh) {
        Decimal8f result = factory.parse(testString, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- Unscaled ValueOf Methods ---

    @Benchmark
    public Decimal8f valueOfUnscaledLong(Blackhole bh) {
        Decimal8f result = factory.valueOfUnscaled(testLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfUnscaledLongWithScale(Blackhole bh) {
        // Using a dummy scale since the factory method requires it
        Decimal8f result = factory.valueOfUnscaled(testLong, 4);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal8f valueOfUnscaledLongWithScaleAndRoundingMode(Blackhole bh) {
        // Using a dummy scale and rounding mode
        Decimal8f result = factory.valueOfUnscaled(testLong, 4, testRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- Array and Mutable Methods ---

    @Benchmark
    public Decimal8f[] newArray(Blackhole bh) {
        Decimal8f[] result = factory.newArray(10);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal8f newMutable(Blackhole bh) {
        MutableDecimal8f result = factory.newMutable();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal8f[] newMutableArray(Blackhole bh) {
        MutableDecimal8f[] result = factory.newMutableArray(10);
        bh.consume(result);
        return result;
    }
}

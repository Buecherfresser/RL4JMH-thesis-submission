package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory9f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.scale.Scale9f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.factory.Factories;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory9fBenchmark {

    private Factory9f factory;

    // Inputs for testing various valueOf/parse methods
    private long sampleLong;
    private float sampleFloat;
    private double sampleDouble;
    private BigInteger sampleBigInteger;
    private BigDecimal sampleBigDecimal;
    private String sampleString;
    private Decimal<?> sampleDecimal;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory9f.INSTANCE;
        
        // Initialize inputs
        sampleLong = 123456789L;
        sampleFloat = 123.4567f;
        sampleDouble = 123.45678901234567;
        sampleBigInteger = new BigInteger("9876543210987654321");
        sampleBigDecimal = new BigDecimal("987654321.123456789");
        sampleString = "987654321.123456789";
        
        // Use a Decimal9f instance as the generic input
        sampleDecimal = Factory9f.INSTANCE.valueOf(sampleBigDecimal);
    }

    // --- Simple Getter/Type Methods ---

    @Benchmark
    public ScaleMetrics getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<org.decimal4j.immutable.Decimal9f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<org.decimal4j.mutable.MutableDecimal9f> mutableType() {
        return factory.mutableType();
    }

    // --- Factory Derivation Methods ---

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(9);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<ScaleMetrics> deriveFactoryScaleMetrics() {
        return factory.deriveFactory(Scale9f.INSTANCE);
    }

    // --- ValueOf Methods (Primitives) ---

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfLong() {
        return factory.valueOf(sampleLong);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfFloat() {
        return factory.valueOf(sampleFloat);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfFloatRoundingMode() {
        return factory.valueOf(sampleFloat, RoundingMode.HALF_UP);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfDouble() {
        return factory.valueOf(sampleDouble);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfDoubleRoundingMode() {
        return factory.valueOf(sampleDouble, RoundingMode.HALF_DOWN);
    }

    // --- ValueOf Methods (Complex Types) ---

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfBigInteger() {
        return factory.valueOf(sampleBigInteger);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfBigDecimal() {
        return factory.valueOf(sampleBigDecimal);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfBigDecimalRoundingMode() {
        return factory.valueOf(sampleBigDecimal, RoundingMode.CEILING);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfDecimalGeneric() {
        return factory.valueOf(sampleDecimal);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfDecimalGenericRoundingMode() {
        return factory.valueOf(sampleDecimal, RoundingMode.UNNECESSARY);
    }

    // --- Parse Methods ---

    @Benchmark
    public org.decimal4j.immutable.Decimal9f parseString() {
        return factory.parse(sampleString);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f parseStringRoundingMode() {
        return factory.parse(sampleString, RoundingMode.HALF_EVEN);
    }

    // --- ValueOfUnscaled Methods ---

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfUnscaledLong() {
        return factory.valueOfUnscaled(sampleLong);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(sampleLong, 5);
    }

    @Benchmark
    public org.decimal4j.immutable.Decimal9f valueOfUnscaledLongScaleRoundingMode() {
        return factory.valueOfUnscaled(sampleLong, 5, RoundingMode.DOWN);
    }

    // --- Array/Mutable Methods ---

    @Benchmark
    public org.decimal4j.immutable.Decimal9f[] newArray() {
        return factory.newArray(10);
    }

    @Benchmark
    public org.decimal4j.mutable.MutableDecimal9f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public org.decimal4j.mutable.MutableDecimal9f[] newMutableArray() {
        return factory.newMutableArray(10);
    }
}

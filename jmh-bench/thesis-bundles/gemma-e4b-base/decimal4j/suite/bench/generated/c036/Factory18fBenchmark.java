package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory18f;
import org.decimal4j.scale.Scale18f;
import org.decimal4j.scale.ScaleMetrics;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal18f;
import org.decimal4j.mutable.MutableDecimal18f;
import org.decimal4j.factory.Factories;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory18fBenchmark {

    private Factory18f factory;
    private BigDecimal bigDecimalInput;
    private BigInteger bigIntegerInput;
    private String stringInput;
    private Decimal18f decimalInput;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory18f.INSTANCE;
        
        // Setup inputs
        bigDecimalInput = new BigDecimal("12345.67890123456789");
        bigIntegerInput = new BigInteger("9876543210987654321");
        stringInput = "12345.6789";
        decimalInput = Decimal18f.valueOf(123.456789);
    }

    // --- Simple Getter Methods ---

    @Benchmark
    public Scale18f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal18f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal18f> mutableType() {
        return factory.mutableType();
    }

    // --- Derive Factory Methods ---

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(10);
    }

    @Benchmark
    public org.decimal4j.factory.DecimalFactory<ScaleMetrics> deriveFactoryScaleMetrics() {
        return factory.deriveFactory(Scale18f.INSTANCE);
    }

    // --- ValueOf (Immutable) Methods ---

    @Benchmark
    public Decimal18f valueOfLong() {
        return factory.valueOf(123456789L);
    }

    @Benchmark
    public Decimal18f valueOfFloat() {
        return factory.valueOf(123.45f);
    }

    @Benchmark
    public Decimal18f valueOfFloatRoundingMode() {
        return factory.valueOf(123.45f, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal18f valueOfDouble() {
        return factory.valueOf(123.4567890123456789);
    }

    @Benchmark
    public Decimal18f valueOfDoubleRoundingMode() {
        return factory.valueOf(123.4567890123456789, RoundingMode.DOWN);
    }

    @Benchmark
    public Decimal18f valueOfBigInteger() {
        return factory.valueOf(bigIntegerInput);
    }

    @Benchmark
    public Decimal18f valueOfBigDecimal() {
        return factory.valueOf(bigDecimalInput);
    }

    @Benchmark
    public Decimal18f valueOfBigDecimalRoundingMode() {
        return factory.valueOf(bigDecimalInput, RoundingMode.CEILING);
    }

    @Benchmark
    public Decimal18f valueOfDecimal() {
        return factory.valueOf(decimalInput);
    }

    @Benchmark
    public Decimal18f valueOfDecimalRoundingMode() {
        return factory.valueOf(decimalInput, RoundingMode.HALF_EVEN);
    }

    // --- Parse Methods ---

    @Benchmark
    public Decimal18f parseString() {
        return factory.parse(stringInput);
    }

    @Benchmark
    public Decimal18f parseStringRoundingMode() {
        return factory.parse(stringInput, RoundingMode.UP);
    }

    // --- ValueOfUnscaled Methods ---

    @Benchmark
    public Decimal18f valueOfUnscaledLong() {
        return factory.valueOfUnscaled(987654321L);
    }

    @Benchmark
    public Decimal18f valueOfUnscaledLongScale() {
        return factory.valueOfUnscaled(987654321L, 5);
    }

    @Benchmark
    public Decimal18f valueOfUnscaledLongScaleRoundingMode() {
        return factory.valueOfUnscaled(987654321L, 5, RoundingMode.HALF_DOWN);
    }

    // --- Array Creation Methods ---

    @Benchmark
    public Decimal18f[] newArray() {
        return factory.newArray(10);
    }

    @Benchmark
    public MutableDecimal18f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal18f[] newMutableArray() {
        return factory.newMutableArray(10);
    }
}

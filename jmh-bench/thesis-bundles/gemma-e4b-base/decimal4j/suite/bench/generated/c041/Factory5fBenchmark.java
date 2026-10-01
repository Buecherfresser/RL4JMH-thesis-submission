package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory5f;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.scale.Scale5f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.immutable.Decimal5f;
import org.decimal4j.mutable.MutableDecimal5f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory5fBenchmark {

    private Factory5f factory;

    // Inputs for testing various valueOf/parse methods
    private String sampleString;
    private BigDecimal sampleBigDecimal;
    private BigInteger sampleBigInteger;
    private Decimal5f sampleDecimal;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory5f.INSTANCE;
        
        // Prepare inputs
        sampleString = "123.45678";
        sampleBigDecimal = new BigDecimal("987654321.012345");
        sampleBigInteger = new BigInteger("1234567890123456789");
        
        // Create a sample Decimal5f instance
        sampleDecimal = Decimal5f.valueOf(123.456);
    }

    // --- Basic Factory Metadata Methods ---

    @Benchmark
    public Scale5f getScaleMetrics() {
        return factory.getScaleMetrics();
    }

    @Benchmark
    public int getScale() {
        return factory.getScale();
    }

    @Benchmark
    public Class<Decimal5f> immutableType() {
        return factory.immutableType();
    }

    @Benchmark
    public Class<MutableDecimal5f> mutableType() {
        return factory.mutableType();
    }

    @Benchmark
    public DecimalFactory<?> deriveFactoryInt() {
        return factory.deriveFactory(5);
    }

    @Benchmark
    public DecimalFactory<ScaleMetrics> deriveFactoryScaleMetrics() {
        return factory.deriveFactory(Scale5f.INSTANCE);
    }

    // --- ValueOf Methods (Immutable Decimal5f) ---

    @Benchmark
    public Decimal5f valueOfLong() {
        return factory.valueOf(12345L);
    }

    @Benchmark
    public Decimal5f valueOfFloat() {
        return factory.valueOf(123.45f);
    }

    @Benchmark
    public Decimal5f valueOfFloatWithRoundingMode() {
        return factory.valueOf(123.45f, RoundingMode.HALF_UP);
    }

    @Benchmark
    public Decimal5f valueOfDouble() {
        return factory.valueOf(123.456789);
    }

    @Benchmark
    public Decimal5f valueOfDoubleWithRoundingMode() {
        return factory.valueOf(123.456789, RoundingMode.DOWN);
    }

    @Benchmark
    public Decimal5f valueOfBigInteger() {
        return factory.valueOf(sampleBigInteger);
    }

    @Benchmark
    public Decimal5f valueOfBigDecimal() {
        return factory.valueOf(sampleBigDecimal);
    }

    @Benchmark
    public Decimal5f valueOfBigDecimalWithRoundingMode() {
        return factory.valueOf(sampleBigDecimal, RoundingMode.CEILING);
    }

    @Benchmark
    public Decimal5f valueOfDecimal() {
        return factory.valueOf(sampleDecimal);
    }

    @Benchmark
    public Decimal5f valueOfDecimalWithRoundingMode() {
        return factory.valueOf(sampleDecimal, RoundingMode.HALF_EVEN);
    }

    // --- Parse Methods ---

    @Benchmark
    public Decimal5f parseString() {
        return factory.parse(sampleString);
    }

    @Benchmark
    public Decimal5f parseStringWithRoundingMode() {
        return factory.parse(sampleString, RoundingMode.UP);
    }

    // --- Unscaled ValueOf Methods ---

    @Benchmark
    public Decimal5f valueOfUnscaledLong() {
        return factory.valueOfUnscaled(123456789L);
    }

    @Benchmark
    public Decimal5f valueOfUnscaledLongWithScale() {
        return factory.valueOfUnscaled(123456789L, 2);
    }

    @Benchmark
    public Decimal5f valueOfUnscaledLongWithScaleAndRoundingMode() {
        return factory.valueOfUnscaled(123456789L, 2, RoundingMode.HALF_UP);
    }

    // --- Array and Mutable Methods ---

    @Benchmark
    public Decimal5f[] newArray() {
        return factory.newArray(10);
    }

    @Benchmark
    public MutableDecimal5f newMutable() {
        return factory.newMutable();
    }

    @Benchmark
    public MutableDecimal5f[] newMutableArray() {
        return factory.newMutableArray(10);
    }
}

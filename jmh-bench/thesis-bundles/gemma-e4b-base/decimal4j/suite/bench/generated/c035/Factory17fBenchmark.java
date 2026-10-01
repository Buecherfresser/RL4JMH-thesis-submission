package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.factory.Factory17f;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory17fBenchmark {

    private Factory17f factory;

    // Inputs for various methods
    private long longInput;
    private float floatInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private Decimal17f decimalInput;
    private ScaleMetrics scaleMetricsInput;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory17f.INSTANCE;

        // Initialize inputs
        longInput = 1234567890123L;
        floatInput = 123.456f;
        doubleInput = 123.4567890123456;
        bigIntegerInput = new BigInteger("9876543210987654321");
        bigDecimalInput = new BigDecimal("9876543210987654321.12345");
        stringInput = "12345.6789";
        
        // Initialize Decimal<?> input
        decimalInput = Decimal17f.valueOf(123.456);

        // Initialize ScaleMetrics input
        scaleMetricsInput = Scale17f.INSTANCE;
    }

    // --- Trivial/Metadata Methods ---

    @Benchmark
    public void testGetScaleMetrics(Blackhole bh) {
        bh.consume(factory.getScaleMetrics());
    }

    @Benchmark
    public void testGetScale(Blackhole bh) {
        bh.consume(factory.getScale());
    }

    @Benchmark
    public void testImmutableType(Blackhole bh) {
        bh.consume(factory.immutableType());
    }

    @Benchmark
    public void testMutableType(Blackhole bh) {
        bh.consume(factory.mutableType());
    }

    // --- Factory Derivation Methods ---

    @Benchmark
    public void testDeriveFactoryInt(Blackhole bh) {
        // Using a different scale for testing derivation
        bh.consume(factory.deriveFactory(10));
    }

    @Benchmark
    public void testDeriveFactoryScaleMetrics(Blackhole bh) {
        // Using the scale metrics input
        bh.consume(factory.deriveFactory(scaleMetricsInput));
    }

    // --- ValueOf (Immutable) Methods ---

    @Benchmark
    public void testValueOfLong(Blackhole bh) {
        bh.consume(factory.valueOf(longInput));
    }

    @Benchmark
    public void testValueOfFloat(Blackhole bh) {
        bh.consume(factory.valueOf(floatInput));
    }

    @Benchmark
    public void testValueOfFloatRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(floatInput, RoundingMode.HALF_UP));
    }

    @Benchmark
    public void testValueOfDouble(Blackhole bh) {
        bh.consume(factory.valueOf(doubleInput));
    }

    @Benchmark
    public void testValueOfDoubleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(doubleInput, RoundingMode.HALF_DOWN));
    }

    @Benchmark
    public void testValueOfBigInteger(Blackhole bh) {
        bh.consume(factory.valueOf(bigIntegerInput));
    }

    @Benchmark
    public void testValueOfBigDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(bigDecimalInput));
    }

    @Benchmark
    public void testValueOfBigDecimalRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(bigDecimalInput, RoundingMode.CEILING));
    }

    @Benchmark
    public void testValueOfDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(decimalInput));
    }

    @Benchmark
    public void testValueOfDecimalRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(decimalInput, RoundingMode.UP));
    }

    // --- Parse Methods ---

    @Benchmark
    public void testParseString(Blackhole bh) {
        bh.consume(factory.parse(stringInput));
    }

    @Benchmark
    public void testParseStringRoundingMode(Blackhole bh) {
        bh.consume(factory.parse(stringInput, RoundingMode.HALF_EVEN));
    }

    // --- ValueOfUnscaled Methods ---

    @Benchmark
    public void testValueOfUnscaledLong(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(longInput));
    }

    @Benchmark
    public void testValueOfUnscaledLongScale(Blackhole bh) {
        // Using a scale different from 17 for the second argument
        bh.consume(factory.valueOfUnscaled(longInput, 10));
    }

    @Benchmark
    public void testValueOfUnscaledLongScaleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(longInput, 10, RoundingMode.DOWN));
    }

    // --- Array/Mutable Methods ---

    @Benchmark
    public void testNewArray(Blackhole bh) {
        // Test creation of an array of size 10
        bh.consume(factory.newArray(10));
    }

    @Benchmark
    public void testNewMutable(Blackhole bh) {
        bh.consume(factory.newMutable());
    }

    @Benchmark
    public void testNewMutableArray(Blackhole bh) {
        // Test creation of an array of size 10
        bh.consume(factory.newMutableArray(10));
    }
}

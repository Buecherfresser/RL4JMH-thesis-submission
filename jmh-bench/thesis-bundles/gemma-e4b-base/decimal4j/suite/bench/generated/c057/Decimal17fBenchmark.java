package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal17f;
import org.decimal4j.scale.Scale17f;
import org.decimal4j.factory.Factory17f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable17f;
import org.decimal4j.mutable.MutableDecimal17f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal17fBenchmark {

    private Decimal17f decA;
    private Decimal17f decB;
    private String testString;
    private BigDecimal testBigDecimal;

    @Setup(Level.Trial)
    public void setup() {
        // Setup two representative Decimal17f instances
        // A: A simple value
        decA = Decimal17f.valueOf(123456789012345678.0); 
        // B: Another simple value
        decB = Decimal17f.valueOf(987654321098765432.0);

        // Setup inputs for parsing/conversion
        testString = "123456789012345678.12345";
        testBigDecimal = new BigDecimal("123456789012345678.12345");
    }

    // --- Construction and Parsing Benchmarks ---

    @Benchmark
    public Decimal17f parseStringDefault(Blackhole bh) {
        // FIX: Use static factory method valueOf(String)
        Decimal17f result = Decimal17f.valueOf(testString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f parseStringRounding(Blackhole bh) {
        // FIX: Use static factory method valueOf(String, RoundingMode)
        Decimal17f result = Decimal17f.valueOf(testString, RoundingMode.HALF_UP);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f fromDoubleDefault(Blackhole bh) {
        // Use a double value that requires rounding to test the conversion logic
        double value = 1.234567890123456789; 
        Decimal17f result = Decimal17f.valueOf(value);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f fromBigDecimalDefault(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(testBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f fromLong(Blackhole bh) {
        Decimal17f result = Decimal17f.valueOf(1234567890L);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f fromUnscaledLong(Blackhole bh) {
        // Test creation from raw unscaled long
        long unscaled = 123456789012345678L;
        Decimal17f result = Decimal17f.valueOfUnscaled(unscaled);
        bh.consume(result);
        return result;
    }

    // --- Arithmetic Benchmarks ---

    @Benchmark
    public Decimal17f addOperation(Blackhole bh) {
        // Assuming standard arithmetic methods exist on the base class
        Decimal17f result = decA.add(decB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal17f multiplyOperation(Blackhole bh) {
        // Assuming standard arithmetic methods exist on the base class
        Decimal17f result = decA.multiply(decB);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Multipliable17f multiplyExactFactor(Blackhole bh) {
        // Test the exact multiplication factor creation
        Multipliable17f multiplier = decA.multiplyExact();
        bh.consume(multiplier);
        return multiplier;
    }

    // --- Conversion and Accessor Benchmarks ---

    @Benchmark
    public MutableDecimal17f toMutableConversion(Blackhole bh) {
        // Test conversion to mutable type
        MutableDecimal17f mutable = decA.toMutableDecimal();
        bh.consume(mutable);
        return mutable;
    }

    @Benchmark
    public Decimal17f toImmutableConversion(Blackhole bh) {
        // Test conversion to immutable type (should be cheap/identity)
        Decimal17f immutable = decA.toImmutableDecimal();
        bh.consume(immutable);
        return immutable;
    }

    @Benchmark
    public int getScaleAccessor(Blackhole bh) {
        int scale = decA.getScale();
        bh.consume(scale);
        return scale;
    }

    @Benchmark
    public Scale17f getScaleMetricsAccessor(Blackhole bh) {
        Scale17f metrics = decA.getScaleMetrics();
        bh.consume(metrics);
        return metrics;
    }
}

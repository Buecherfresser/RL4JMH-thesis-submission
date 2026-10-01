package bench.generated.c042;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory6f;
import org.decimal4j.scale.Scale6f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal6f;
import org.decimal4j.mutable.MutableDecimal6f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory6fBenchmark {

    private Factory6f factory;

    // Inputs for testing various valueOf/parse methods
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private Decimal6f decimalInput;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory6f.INSTANCE;
        
        // Setup complex inputs
        bigDecimalInput = new BigDecimal("12345.678901");
        stringInput = "987654321.012345";
        bigIntegerInput = new BigInteger("1234567890123456789");
        
        // Setup a Decimal<?> input (using Decimal6f as a representative)
        decimalInput = Decimal6f.valueOf(123.456);
    }

    // --- Basic Factory Properties ---

    @Benchmark
    public void benchmarkGetScaleMetrics(Blackhole bh) {
        bh.consume(factory.getScaleMetrics());
    }

    @Benchmark
    public void benchmarkGetScale(Blackhole bh) {
        bh.consume(factory.getScale());
    }

    @Benchmark
    public void benchmarkImmutableType(Blackhole bh) {
        bh.consume(factory.immutableType());
    }

    @Benchmark
    public void benchmarkMutableType(Blackhole bh) {
        bh.consume(factory.mutableType());
    }

    // --- Derive Factory Methods ---

    @Benchmark
    public void benchmarkDeriveFactoryInt(Blackhole bh) {
        // Deriving factory for a different scale (e.g., scale 10)
        bh.consume(factory.deriveFactory(10));
    }

    @Benchmark
    public void benchmarkDeriveFactoryScaleMetrics(Blackhole bh) {
        // Deriving factory using ScaleMetrics (Scale6f.INSTANCE is used here)
        bh.consume(factory.deriveFactory(Scale6f.INSTANCE));
    }

    // --- ValueOf Methods (Primitives) ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        bh.consume(factory.valueOf(123456789L));
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        bh.consume(factory.valueOf(123.45f));
    }

    @Benchmark
    public void benchmarkValueOfFloatRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(123.45f, RoundingMode.HALF_UP));
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        bh.consume(factory.valueOf(123.456789));
    }

    @Benchmark
    public void benchmarkValueOfDoubleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(123.456789, RoundingMode.DOWN));
    }

    // --- ValueOf Methods (Complex Types) ---

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        bh.consume(factory.valueOf(bigIntegerInput));
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(bigDecimalInput));
    }

    @Benchmark
    public void benchmarkValueOfBigDecimalRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(bigDecimalInput, RoundingMode.CEILING));
    }

    @Benchmark
    public void benchmarkValueOfDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(decimalInput));
    }

    @Benchmark
    public void benchmarkValueOfDecimalRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(decimalInput, RoundingMode.HALF_EVEN));
    }

    // --- Parse Methods ---

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        bh.consume(factory.parse(stringInput));
    }

    @Benchmark
    public void benchmarkParseStringRoundingMode(Blackhole bh) {
        bh.consume(factory.parse(stringInput, RoundingMode.UP));
    }

    // --- ValueOfUnscaled Methods ---

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(987654321L));
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongScale(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(987654321L, 6));
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongScaleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(987654321L, 6, RoundingMode.HALF_DOWN));
    }

    // --- Array/Mutable Methods ---

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        // Test creation of an array of size 10
        bh.consume(factory.newArray(10));
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        bh.consume(factory.newMutable());
    }

    @Benchmark
    public void benchmarkNewMutableArray(Blackhole bh) {
        // Test creation of an array of size 10
        bh.consume(factory.newMutableArray(10));
    }
}

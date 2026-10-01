package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory15f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal15f;
import org.decimal4j.mutable.MutableDecimal15f;
import org.decimal4j.scale.Scale15f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.factory.Factories;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory15fBenchmark {

    private Factory15f factory;

    // Inputs for various methods
    private long longInput;
    private float floatInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private Decimal<?> decimalInput;
    private ScaleMetrics scaleMetricsInput;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory15f.INSTANCE;

        // Initialize inputs
        longInput = 1234567890123L;
        floatInput = 123.456f;
        doubleInput = 123.4567890123456;
        bigIntegerInput = new BigInteger("9876543210987654321");
        bigDecimalInput = new BigDecimal("9876543210987654321.12345");
        stringInput = "123.4567890123456";
        
        // Initialize Decimal<?> input (using a generic Decimal instance)
        decimalInput = Decimal15f.valueOf(bigDecimalInput);

        // Initialize ScaleMetrics input
        scaleMetricsInput = Scale15f.INSTANCE;
    }

    // --- Simple Accessor Methods ---

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

    // --- Factory Derivation Methods ---

    @Benchmark
    public void benchmarkDeriveFactoryInt(Blackhole bh) {
        // Derive factory for a different scale (e.g., scale 5)
        bh.consume(factory.deriveFactory(5));
    }

    @Benchmark
    public void benchmarkDeriveFactoryScaleMetrics(Blackhole bh) {
        // Derive factory using a specific scale metrics instance
        bh.consume(factory.deriveFactory(scaleMetricsInput));
    }

    // --- Immutable ValueOf Methods (Primitives/Objects) ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        bh.consume(factory.valueOf(longInput));
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        bh.consume(factory.valueOf(floatInput));
    }

    @Benchmark
    public void benchmarkValueOfFloatRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(floatInput, RoundingMode.HALF_UP));
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        bh.consume(factory.valueOf(doubleInput));
    }

    @Benchmark
    public void benchmarkValueOfDoubleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOf(doubleInput, RoundingMode.HALF_DOWN));
    }

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
        bh.consume(factory.valueOf(decimalInput, RoundingMode.UP));
    }

    // --- Parsing Methods ---

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        bh.consume(factory.parse(stringInput));
    }

    @Benchmark
    public void benchmarkParseStringRoundingMode(Blackhole bh) {
        bh.consume(factory.parse(stringInput, RoundingMode.DOWN));
    }

    // --- Unscaled ValueOf Methods ---

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(longInput));
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongScale(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(longInput, 10));
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongScaleRoundingMode(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(longInput, 10, RoundingMode.HALF_EVEN));
    }

    // --- Array Creation Methods ---

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        // Create an array of size 10
        bh.consume(factory.newArray(10));
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        bh.consume(factory.newMutable());
    }

    @Benchmark
    public void benchmarkNewMutableArray(Blackhole bh) {
        // Create an array of size 10
        bh.consume(factory.newMutableArray(10));
    }
}

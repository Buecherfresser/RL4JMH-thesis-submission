package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.factory.Factory7f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;
import org.decimal4j.immutable.Decimal7f;
import org.decimal4j.mutable.MutableDecimal7f;
import org.decimal4j.scale.Scale7f;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.factory.Factories;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory7fBenchmark {

    private Factory7f factory;

    // Inputs for various valueOf/parse methods
    private long longInput;
    private float floatInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private Decimal7f decimalInput;

    @Setup(Level.Trial)
    public void setup() {
        factory = Factory7f.INSTANCE;

        // Initialize representative inputs
        longInput = 123456789L;
        floatInput = 123.4567f;
        doubleInput = 123.4567890123456;
        bigIntegerInput = new BigInteger("9876543210987654321");
        bigDecimalInput = new BigDecimal("987654321.1234567");
        stringInput = "123.4567";
        decimalInput = Decimal7f.valueOf(123.4567);
    }

    // --- Metadata/Type methods ---

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

    @Benchmark
    public void benchmarkDeriveFactoryIntScale(Blackhole bh) {
        // Using a different scale to ensure the call is meaningful
        bh.consume(factory.deriveFactory(10));
    }

    @Benchmark
    public void benchmarkDeriveFactoryScaleMetrics(Blackhole bh) {
        // Using Scale7f.INSTANCE as the scale metrics input
        bh.consume(factory.deriveFactory(Scale7f.INSTANCE));
    }

    // --- ValueOf methods (Immutable Decimal7f creation) ---

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        bh.consume(factory.valueOf(longInput));
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        bh.consume(factory.valueOf(floatInput));
    }

    @Benchmark
    public void benchmarkValueOfFloatRounding(Blackhole bh) {
        bh.consume(factory.valueOf(floatInput, RoundingMode.HALF_UP));
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        bh.consume(factory.valueOf(doubleInput));
    }

    @Benchmark
    public void benchmarkValueOfDoubleRounding(Blackhole bh) {
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
    public void benchmarkValueOfBigDecimalRounding(Blackhole bh) {
        bh.consume(factory.valueOf(bigDecimalInput, RoundingMode.CEILING));
    }

    @Benchmark
    public void benchmarkValueOfDecimal(Blackhole bh) {
        bh.consume(factory.valueOf(decimalInput));
    }

    @Benchmark
    public void benchmarkValueOfDecimalRounding(Blackhole bh) {
        bh.consume(factory.valueOf(decimalInput, RoundingMode.UNNECESSARY));
    }

    // --- Parse methods ---

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        bh.consume(factory.parse(stringInput));
    }

    @Benchmark
    public void benchmarkParseStringRounding(Blackhole bh) {
        bh.consume(factory.parse(stringInput, RoundingMode.HALF_UP));
    }

    // --- Unscaled ValueOf methods ---

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(longInput));
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongScale(Blackhole bh) {
        // Using a scale different from 7
        bh.consume(factory.valueOfUnscaled(longInput, 10));
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongScaleRounding(Blackhole bh) {
        bh.consume(factory.valueOfUnscaled(longInput, 10, RoundingMode.DOWN));
    }

    // --- Array/Mutable methods ---

    @Benchmark
    public void benchmarkNewArray(Blackhole bh) {
        // Array creation is fast, but we measure it
        bh.consume(factory.newArray(100));
    }

    @Benchmark
    public void benchmarkNewMutable(Blackhole bh) {
        bh.consume(factory.newMutable());
    }

    @Benchmark
    public void benchmarkNewMutableArray(Blackhole bh) {
        bh.consume(factory.newMutableArray(100));
    }
}

package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.generic.GenericDecimalFactory;
import org.decimal4j.generic.GenericImmutableDecimal;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.scale.Scales;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.api.Decimal;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GenericDecimalFactoryBenchmark {

    private GenericDecimalFactory<ScaleMetrics> factory;
    private ScaleMetrics scaleMetrics;

    // Inputs for various valueOf methods
    private long longInput;
    private float floatInput;
    private double doubleInput;
    private BigInteger bigIntegerInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private Decimal<?> decimalInput;

    // Inputs for unscaled valueOf methods
    private long unscaledLongInput;
    private int scaleInput = 5;

    @Setup(Level.Trial)
    public void setup() {
        // Use Scale 5 for concrete testing
        scaleMetrics = Scales.getScaleMetrics(5);
        factory = new GenericDecimalFactory<>(scaleMetrics);

        // Initialize inputs
        longInput = 1234567890123L;
        floatInput = 123.4567f;
        doubleInput = 123.45678901234567;
        bigIntegerInput = new BigInteger("9876543210987654321");
        bigDecimalInput = new BigDecimal("12345.6789012345");
        stringInput = "12345.6789012345";
        
        // Create a dummy Decimal object for testing valueOf(Decimal<?>)
        // We need a Decimal object that uses the same scale metrics for consistency
        Decimal<?> tempDecimal = new GenericImmutableDecimal<>(
                scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().fromUnscaled(123456789L, 5));
        decimalInput = tempDecimal;

        // Unscaled inputs
        unscaledLongInput = 123456789L;
    }

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(longInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfFloat(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(floatInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfFloatWithRounding(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(floatInput, RoundingMode.HALF_UP);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(doubleInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDoubleWithRounding(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(doubleInput, RoundingMode.HALF_DOWN);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(bigIntegerInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(bigDecimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimalWithRounding(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(bigDecimalInput, RoundingMode.CEILING);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDecimal(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(decimalInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDecimalWithRounding(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOf(decimalInput, RoundingMode.DOWN);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseString(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.parse(stringInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkParseStringWithRounding(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.parse(stringInput, RoundingMode.HALF_EVEN);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLong(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOfUnscaled(unscaledLongInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScale(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOfUnscaled(unscaledLongInput, scaleInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledLongWithScaleAndRounding(Blackhole bh) {
        GenericImmutableDecimal<ScaleMetrics> result = factory.valueOfUnscaled(unscaledLongInput, scaleInput, RoundingMode.UP);
        bh.consume(result);
    }
}

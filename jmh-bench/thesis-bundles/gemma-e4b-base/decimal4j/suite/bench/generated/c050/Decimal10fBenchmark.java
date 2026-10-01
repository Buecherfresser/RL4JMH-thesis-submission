package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal10f;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable10f;
import org.decimal4j.scale.Scale10f;
import java.math.BigDecimal;
import java.math.RoundingMode;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal10fBenchmark {

    private String complexString;
    private double representativeDouble;
    private BigDecimal representativeBigDecimal;
    private long representativeLong;
    private long representativeUnscaledLong;
    private RoundingMode customRoundingMode;

    @Setup(Level.Trial)
    public void setup() {
        // Complex string input for parsing
        complexString = "1234567890.1234567890123"; // Requires rounding
        
        // Representative double input
        representativeDouble = 123.4567890123456;
        
        // Representative BigDecimal input
        representativeBigDecimal = new BigDecimal("987654321.0000000001");
        
        // Representative long input
        representativeLong = 123456789L;
        
        // Representative unscaled long input
        representativeUnscaledLong = 9876543210L;
        
        // Custom rounding mode
        customRoundingMode = RoundingMode.CEILING;
    }

    // --- Construction/Parsing Benchmarks ---

    @Benchmark
    public Decimal10f benchmarkValueOfStringDefault(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOf(complexString);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkValueOfStringCustomRounding(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOf(complexString, customRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkValueOfDoubleDefault(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOf(representativeDouble);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkValueOfDoubleCustomRounding(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOf(representativeDouble, customRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkValueOfBigDecimalDefault(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOf(representativeBigDecimal);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkValueOfBigDecimalCustomRounding(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOf(representativeBigDecimal, customRoundingMode);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkValueOfLong(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOf(representativeLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkValueOfUnscaledDefault(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOfUnscaled(representativeUnscaledLong);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Decimal10f benchmarkValueOfUnscaledCustomRounding(Blackhole bh) {
        Decimal10f result = Decimal10f.valueOfUnscaled(representativeUnscaledLong, 5, customRoundingMode);
        bh.consume(result);
        return result;
    }

    // --- Arithmetic/Structure Benchmarks ---

    @Benchmark
    public Multipliable10f benchmarkMultiplyExactSetup(Blackhole bh) {
        // Benchmarking the setup of the exact multiplication chain
        Multipliable10f multiplier = Decimal10f.ONE.multiplyExact();
        bh.consume(multiplier);
        return multiplier;
    }

    @Benchmark
    public MutableDecimal10f benchmarkToMutableDecimal(Blackhole bh) {
        // Benchmarking conversion to mutable type
        Decimal10f original = Decimal10f.TEN;
        MutableDecimal10f mutable = original.toMutableDecimal();
        bh.consume(mutable);
        return mutable;
    }

    @Benchmark
    public Decimal10f benchmarkToImmutableDecimal(Blackhole bh) {
        // Benchmarking conversion to immutable type (should be cheap/identity)
        Decimal10f original = Decimal10f.TEN;
        Decimal10f immutable = original.toImmutableDecimal();
        bh.consume(immutable);
        return immutable;
    }

    // --- Accessor Benchmarks ---

    @Benchmark
    public Scale10f benchmarkGetScaleMetrics(Blackhole bh) {
        Scale10f metrics = Decimal10f.ONE.getScaleMetrics();
        bh.consume(metrics);
        return metrics;
    }

    @Benchmark
    public int benchmarkGetScale(Blackhole bh) {
        int scale = Decimal10f.ONE.getScale();
        bh.consume(scale);
        return scale;
    }
}

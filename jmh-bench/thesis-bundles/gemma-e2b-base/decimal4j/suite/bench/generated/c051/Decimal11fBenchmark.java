package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory11f;
import org.decimal4j.immutable.Decimal11f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class Decimal11fBenchmark {

    private Decimal11f longInput;
    private Decimal11f doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private Decimal<?> decimalInput;

    // Constants for testing
    private static final long TEST_LONG = 1234567890123L;
    private static final double TEST_DOUBLE = 1234567890123.456789;
    private static final BigDecimal TEST_BIG_DECIMAL = new BigDecimal("1234567890123.456789");
    private static final String TEST_STRING = "1234567890123.456789";
    private static final BigInteger TEST_BIG_INTEGER = new BigInteger("1234567890123456789");
    private static final Decimal<?> TEST_DECIMAL = new Decimal11f(TEST_STRING);

    @Setup
    public void setup() {
        // Build inputs once in @Setup
        this.longInput = Decimal11f.valueOf(TEST_LONG);
        this.doubleInput = Decimal11f.valueOf(TEST_DOUBLE);
        this.bigDecimalInput = TEST_BIG_DECIMAL;
        this.stringInput = TEST_STRING;
        this.bigIntegerInput = TEST_BIG_INTEGER;
        this.decimalInput = TEST_DECIMAL;
    }

    @Benchmark
    public void benchmarkValueOfLong(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(TEST_LONG);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDouble(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(TEST_DOUBLE);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigDecimal(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(TEST_BIG_DECIMAL);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfString(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(TEST_STRING);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfBigInteger(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(TEST_BIG_INTEGER);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfDecimal(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOf(TEST_DECIMAL);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaled(Blackhole bh) {
        Decimal11f result = Decimal11f.valueOfUnscaled(TEST_LONG);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithScale(Blackhole bh) {
        int scale = 5;
        Decimal11f result = Decimal11f.valueOfUnscaled(TEST_LONG, scale);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkValueOfUnscaledWithRounding(Blackhole bh) {
        int scale = 10;
        RoundingMode mode = RoundingMode.HALF_UP;
        Decimal11f result = Decimal11f.valueOfUnscaled(TEST_LONG, scale, mode);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMultiplyExact(Blackhole bh) {
        // Test the multiplication factory method
        org.decimal4j.exact.Multipliable11f multiplier = longInput.multiplyExact();
        bh.consume(multiplier);
    }

    @Benchmark
    public void benchmarkToMutableDecimal(Blackhole bh) {
        // Test conversion to mutable
        org.decimal4j.mutable.MutableDecimal11f mutableDecimal = longInput.toMutableDecimal();
        bh.consume(mutableDecimal);
    }

    @Benchmark
    public void benchmarkToImmutableDecimal(Blackhole bh) {
        // Test conversion to immutable (identity check)
        Decimal11f immutableDecimal = longInput.toImmutableDecimal();
        bh.consume(immutableDecimal);
    }
}

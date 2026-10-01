package bench.generated.c044;

import org.decimal4j.api.Decimal;
import org.decimal4j.factory.Factory8f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Factory8fBenchmark {

    // --- Setup State ---
    private long longInput;
    private double doubleInput;
    private BigDecimal bigDecimalInput;
    private String stringInput;
    private BigInteger bigIntegerInput;
    private String stringParseInput;
    private int arrayLength;

    @Setup
    public void setup() {
        // Setup primitive/standard inputs
        longInput = 1234567890123L;
        doubleInput = 3.1415926535;
        bigDecimalInput = new BigDecimal("1234567890123.456789");
        stringInput = "1234567890123";
        bigIntegerInput = new BigInteger("9876543210987654321");
        stringParseInput = "9876543210987654321";
        arrayLength = 1024;
    }

    // --- ValueOf Benchmarks ---

    @Benchmark
    public void valueOf_long(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOf(longInput));
    }

    @Benchmark
    public void valueOf_double(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOf(doubleInput));
    }

    @Benchmark
    public void valueOf_double_with_rounding(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOf(doubleInput, RoundingMode.HALF_UP));
    }

    @Benchmark
    public void valueOf_bigdecimal(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOf(bigDecimalInput));
    }

    @Benchmark
    public void valueOf_bigdecimal_with_rounding(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOf(bigDecimalInput, RoundingMode.HALF_EVEN));
    }

    @Benchmark
    public void valueOf_biginteger(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOf(bigIntegerInput));
    }

    @Benchmark
    public void valueOf_decimal_generic(Blackhole bh) {
        // Use Factory8f to create a Decimal<?> instance
        Decimal<?> dummyDecimal = Factory8f.INSTANCE.valueOf(100L);
        bh.consume(Factory8f.INSTANCE.valueOf(dummyDecimal));
    }

    @Benchmark
    public void valueOf_decimal_generic_with_rounding(Blackhole bh) {
        // Use Factory8f to create a Decimal<?> instance
        Decimal<?> dummyDecimal = Factory8f.INSTANCE.valueOf(100L);
        bh.consume(Factory8f.INSTANCE.valueOf(dummyDecimal, RoundingMode.CEILING));
    }

    // --- Parse Benchmarks ---

    @Benchmark
    public void parse_string(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.parse(stringParseInput));
    }

    @Benchmark
    public void parse_string_with_rounding(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.parse(stringParseInput, RoundingMode.HALF_DOWN));
    }

    // --- Unscaled Value Benchmarks ---

    @Benchmark
    public void valueOfUnscaled_long(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOfUnscaled(longInput));
    }

    @Benchmark
    public void valueOfUnscaled_long_with_scale(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOfUnscaled(longInput, 8));
    }

    @Benchmark
    public void valueOfUnscaled_long_with_scale_and_rounding(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.valueOfUnscaled(longInput, 8, RoundingMode.UP));
    }

    // --- Array/Mutable Benchmarks ---

    @Benchmark
    public void newArray(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.newArray(arrayLength));
    }

    @Benchmark
    public void newMutableArray(Blackhole bh) {
        bh.consume(Factory8f.INSTANCE.newMutableArray(arrayLength));
    }
}

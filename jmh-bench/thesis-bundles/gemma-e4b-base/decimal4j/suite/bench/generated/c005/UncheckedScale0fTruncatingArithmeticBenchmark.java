package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class UncheckedScale0fTruncatingArithmeticBenchmark {

    private UncheckedScale0fTruncatingArithmetic arithmetic;

    // Inputs for arithmetic operations
    private long longA;
    private long longB;
    private long longC;
    private int scaleInt;
    private int positionsInt;
    private int exponentInt;
    private int precisionInt;

    // Inputs for conversion/parsing
    private float floatValue;
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;
    private CharSequence charSequenceValue;

    @Setup(Level.Trial)
    public void setup() {
        arithmetic = UncheckedScale0fTruncatingArithmetic.INSTANCE;

        // Initialize representative inputs
        longA = 1234567890123L;
        longB = 9876543210987L;
        longC = 500000000L;
        
        scaleInt = 5;
        positionsInt = 3;
        exponentInt = 2;
        precisionInt = 10;

        floatValue = 3.14159f;
        doubleValue = 2.718281828;
        
        bigDecimalValue = new BigDecimal("12345.6789");
        
        stringValue = "987654321";
        charSequenceValue = "1234567890";
    }

    // --- Arithmetic Operations ---

    @Benchmark
    public long bench_addUnscaled() {
        return arithmetic.addUnscaled(longA, longB, scaleInt);
    }

    @Benchmark
    public long bench_subtractUnscaled() {
        return arithmetic.subtractUnscaled(longA, longB, scaleInt);
    }

    @Benchmark
    public long bench_multiplyByUnscaled() {
        return arithmetic.multiplyByUnscaled(longA, longB, scaleInt);
    }

    @Benchmark
    public long bench_divide() {
        return arithmetic.divide(longA, longB);
    }

    @Benchmark
    public long bench_divideByLong() {
        return arithmetic.divideByLong(longA, longC);
    }

    @Benchmark
    public long bench_divideByUnscaled() {
        return arithmetic.divideByUnscaled(longA, longB, scaleInt);
    }

    @Benchmark
    public long bench_multiplyByPowerOf10() {
        return arithmetic.multiplyByPowerOf10(longA, positionsInt);
    }

    @Benchmark
    public long bench_divideByPowerOf10() {
        return arithmetic.divideByPowerOf10(longA, positionsInt);
    }

    @Benchmark
    public long bench_invert() {
        return arithmetic.invert(longA);
    }

    @Benchmark
    public long bench_sqrt() {
        return arithmetic.sqrt(longA);
    }

    @Benchmark
    public long bench_pow() {
        return arithmetic.pow(longA, exponentInt);
    }

    @Benchmark
    public long bench_shiftLeft() {
        return arithmetic.shiftLeft(longA, positionsInt);
    }

    @Benchmark
    public long bench_shiftRight() {
        return arithmetic.shiftRight(longA, positionsInt);
    }

    @Benchmark
    public long bench_avg() {
        return arithmetic.avg(longA, longB);
    }

    @Benchmark
    public long bench_round() {
        return arithmetic.round(longA, precisionInt);
    }

    // --- Conversion and Parsing ---

    @Benchmark
    public long bench_toUnscaled() {
        return arithmetic.toUnscaled(longA, scaleInt);
    }

    @Benchmark
    public double bench_toDouble() {
        return arithmetic.toDouble(longA);
    }

    @Benchmark
    public float bench_toFloat() {
        return arithmetic.toFloat(longA);
    }

    @Benchmark
    public long bench_fromUnscaled() {
        return arithmetic.fromUnscaled(longB, scaleInt);
    }

    @Benchmark
    public long bench_fromFloat() {
        return arithmetic.fromFloat(floatValue);
    }

    @Benchmark
    public long bench_fromDouble() {
        return arithmetic.fromDouble(doubleValue);
    }

    @Benchmark
    public long bench_fromBigDecimal() {
        return arithmetic.fromBigDecimal(bigDecimalValue);
    }

    @Benchmark
    public long bench_parseString() {
        return arithmetic.parse(stringValue);
    }

    @Benchmark
    public long bench_parseCharSequence() {
        return arithmetic.parse(charSequenceValue, 0, charSequenceValue.length());
    }
}

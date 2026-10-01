package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic;
import org.decimal4j.scale.Scale0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CheckedScale0fTruncatingArithmeticBenchmark {

    private CheckedScale0fTruncatingArithmetic arithmetic;

    // Inputs for arithmetic operations
    private long uDecimal1;
    private long unscaled1;
    private int scale1;
    private long uDecimal2;
    private long unscaled2;
    private int scale2;

    // Inputs for conversions
    private float floatValue;
    private double doubleValue;
    private BigDecimal bigDecimalValue;
    private String stringValue;

    @Setup
    public void setup() {
        arithmetic = CheckedScale0fTruncatingArithmetic.INSTANCE;

        // Setup inputs for arithmetic operations (using large, non-trivial longs)
        uDecimal1 = 123456789012345L;
        unscaled1 = 987654321098765L;
        scale1 = 0;

        uDecimal2 = 543210987654321L;
        unscaled2 = 123456789012345L;
        scale2 = 0;

        // Setup inputs for conversions
        floatValue = 3.14159f;
        doubleValue = 123456789012345.6789;
        bigDecimalValue = new BigDecimal("123456789012345.6789");
        stringValue = "987654321098765";
    }

    // --- Unscaled Arithmetic Benchmarks ---

    @Benchmark
    public void addUnscaled(Blackhole bh) {
        long result = arithmetic.addUnscaled(uDecimal1, unscaled1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void subtractUnscaled(Blackhole bh) {
        long result = arithmetic.subtractUnscaled(uDecimal1, unscaled1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByUnscaled(Blackhole bh) {
        long result = arithmetic.multiplyByUnscaled(uDecimal1, unscaled1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void divideByUnscaled(Blackhole bh) {
        long result = arithmetic.divideByUnscaled(uDecimal1, unscaled1, scale1);
        bh.consume(result);
    }

    // --- Long Arithmetic Benchmarks ---

    @Benchmark
    public void divide(Blackhole bh) {
        long result = arithmetic.divide(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void divideByLong(Blackhole bh) {
        long result = arithmetic.divideByLong(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void avg(Blackhole bh) {
        long result = arithmetic.avg(uDecimal1, uDecimal2);
        bh.consume(result);
    }

    @Benchmark
    public void invert(Blackhole bh) {
        long result = arithmetic.invert(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void pow(Blackhole bh) {
        long result = arithmetic.pow(uDecimal1, 10);
        bh.consume(result);
    }

    @Benchmark
    public void sqrt(Blackhole bh) {
        long result = arithmetic.sqrt(uDecimal1);
        bh.consume(result);
    }

    // --- Power of 10 Benchmarks ---

    @Benchmark
    public void divideByPowerOf10(Blackhole bh) {
        int n = 10;
        long result = arithmetic.divideByPowerOf10(uDecimal1, n);
        bh.consume(result);
    }

    @Benchmark
    public void multiplyByPowerOf10(Blackhole bh) {
        int n = 10;
        long result = arithmetic.multiplyByPowerOf10(uDecimal1, n);
        bh.consume(result);
    }

    // --- Shift Benchmarks ---

    @Benchmark
    public void shiftLeft(Blackhole bh) {
        int positions = 5;
        long result = arithmetic.shiftLeft(uDecimal1, positions);
        bh.consume(result);
    }

    @Benchmark
    public void shiftRight(Blackhole bh) {
        int positions = 5;
        long result = arithmetic.shiftRight(uDecimal1, positions);
        bh.consume(result);
    }

    // --- Rounding and Conversion Benchmarks ---

    @Benchmark
    public void round(Blackhole bh) {
        int precision = 5;
        long result = arithmetic.round(uDecimal1, precision);
        bh.consume(result);
    }

    @Benchmark
    public void toFloat(Blackhole bh) {
        float result = arithmetic.toFloat(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void toDouble(Blackhole bh) {
        double result = arithmetic.toDouble(uDecimal1);
        bh.consume(result);
    }

    @Benchmark
    public void toUnscaled(Blackhole bh) {
        long result = arithmetic.toUnscaled(uDecimal1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void fromFloat(Blackhole bh) {
        long result = arithmetic.fromFloat(floatValue);
        bh.consume(result);
    }

    @Benchmark
    public void fromDouble(Blackhole bh) {
        long result = arithmetic.fromDouble(doubleValue);
        bh.consume(result);
    }

    @Benchmark
    public void fromUnscaled(Blackhole bh) {
        long result = arithmetic.fromUnscaled(unscaled1, scale1);
        bh.consume(result);
    }

    @Benchmark
    public void fromBigDecimal(Blackhole bh) {
        long result = arithmetic.fromBigDecimal(bigDecimalValue);
        bh.consume(result);
    }

    @Benchmark
    public void parseString(Blackhole bh) {
        long result = arithmetic.parse(stringValue);
        bh.consume(result);
    }

    @Benchmark
    public void parseCharSequence(Blackhole bh) {
        // Test parsing a substring
        long result = arithmetic.parse(stringValue, 0, stringValue.length());
        bh.consume(result);
    }
}

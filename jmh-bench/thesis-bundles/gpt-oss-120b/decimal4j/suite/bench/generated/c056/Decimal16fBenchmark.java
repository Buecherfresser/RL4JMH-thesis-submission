package bench.generated.c056;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal16f;
import org.decimal4j.mutable.MutableDecimal16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal16fBenchmark {

    private Decimal16f a;
    private Decimal16f b;
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;
    private String decimalString;
    private RoundingMode roundingMode;

    @Setup
    public void setup() {
        a = Decimal16f.valueOf(12345L);
        b = Decimal16f.valueOf(6789L);
        bigDecimal = new BigDecimal("12345.67890123456789");
        bigInteger = new BigInteger("12345678901234567890");
        decimalString = "98765.43210987654321";
        roundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public Decimal16f benchmarkValueOfLong() {
        return Decimal16f.valueOf(42L);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfFloat() {
        return Decimal16f.valueOf(3.14f);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfDouble() {
        return Decimal16f.valueOf(2.718281828459045);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfBigInteger() {
        return Decimal16f.valueOf(bigInteger);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfBigDecimal() {
        return Decimal16f.valueOf(bigDecimal);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfString() {
        return Decimal16f.valueOf(decimalString);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfUnscaled() {
        return Decimal16f.valueOfUnscaled(123456789L);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfUnscaledWithScale() {
        return Decimal16f.valueOfUnscaled(123456789L, 10);
    }

    @Benchmark
    public Decimal16f benchmarkValueOfUnscaledWithRounding() {
        return Decimal16f.valueOfUnscaled(123456789L, 10, roundingMode);
    }

    @Benchmark
    public Decimal16f benchmarkAdd() {
        return a.add(b);
    }

    @Benchmark
    public Decimal16f benchmarkSubtract() {
        return a.subtract(b);
    }

    @Benchmark
    public Decimal16f benchmarkMultiply() {
        return a.multiply(b);
    }

    @Benchmark
    public Decimal16f benchmarkDivide() {
        return a.divide(b);
    }

    @Benchmark
    public Decimal16f benchmarkNegate() {
        return a.negate();
    }

    @Benchmark
    public Decimal16f benchmarkAbs() {
        return a.abs();
    }

    @Benchmark
    public Decimal16f benchmarkSquare() {
        return a.square();
    }

    @Benchmark
    public Decimal16f benchmarkPow() {
        return a.pow(3);
    }

    @Benchmark
    public MutableDecimal16f benchmarkToMutable() {
        return a.toMutableDecimal();
    }

    @Benchmark
    public void benchmarkConsumeBlackhole(Blackhole bh) {
        bh.consume(a.add(b));
    }
}

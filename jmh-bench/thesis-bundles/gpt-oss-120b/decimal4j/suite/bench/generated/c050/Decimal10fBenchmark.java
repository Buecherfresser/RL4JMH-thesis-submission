package bench.generated.c050;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.immutable.Decimal10f;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.decimal4j.mutable.MutableDecimal10f;
import org.decimal4j.factory.Factory10f;
import org.decimal4j.scale.Scale10f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal10fBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        Decimal10f a;
        Decimal10f b;
        long longValue;
        double doubleValue;
        String stringValue;
        BigDecimal bigDecimalValue;
        BigInteger bigIntegerValue;
        long unscaledValue;
        RoundingMode roundingMode;

        @Setup
        public void setup() {
            a = Decimal10f.valueOf(12345L);
            b = Decimal10f.valueOf(6789L);
            longValue = 12345L;
            doubleValue = 12345.6789;
            stringValue = "12345.6789";
            bigDecimalValue = new BigDecimal("12345.6789");
            bigIntegerValue = new BigInteger("1234567890123456789");
            unscaledValue = 1234567890L; // corresponds to 0.1234567890
            roundingMode = RoundingMode.HALF_UP;
        }
    }

    @Benchmark
    public Decimal10f benchmarkValueOfLong(BenchmarkState s) {
        return Decimal10f.valueOf(s.longValue);
    }

    @Benchmark
    public Decimal10f benchmarkValueOfDouble(BenchmarkState s) {
        return Decimal10f.valueOf(s.doubleValue);
    }

    @Benchmark
    public Decimal10f benchmarkValueOfString(BenchmarkState s) {
        return Decimal10f.valueOf(s.stringValue);
    }

    @Benchmark
    public Decimal10f benchmarkValueOfBigDecimal(BenchmarkState s) {
        return Decimal10f.valueOf(s.bigDecimalValue);
    }

    @Benchmark
    public Decimal10f benchmarkValueOfBigInteger(BenchmarkState s) {
        return Decimal10f.valueOf(s.bigIntegerValue);
    }

    @Benchmark
    public Decimal10f benchmarkValueOfUnscaled(BenchmarkState s) {
        return Decimal10f.valueOfUnscaled(s.unscaledValue);
    }

    @Benchmark
    public Decimal10f benchmarkAdd(BenchmarkState s) {
        return s.a.add(s.b);
    }

    @Benchmark
    public Decimal10f benchmarkSubtract(BenchmarkState s) {
        return s.a.subtract(s.b);
    }

    @Benchmark
    public Decimal10f benchmarkMultiply(BenchmarkState s) {
        return s.a.multiply(s.b);
    }

    @Benchmark
    public Decimal10f benchmarkDivide(BenchmarkState s) {
        return s.a.divide(s.b);
    }

    @Benchmark
    public Decimal10f benchmarkNegate(BenchmarkState s) {
        return s.a.negate();
    }

    @Benchmark
    public Decimal10f benchmarkAbs(BenchmarkState s) {
        return s.a.abs();
    }

    @Benchmark
    public Decimal10f benchmarkSquare(BenchmarkState s) {
        return s.a.square();
    }

    @Benchmark
    public Decimal10f benchmarkSqrt(BenchmarkState s) {
        return s.a.sqrt();
    }

    @Benchmark
    public MutableDecimal10f benchmarkToMutableDecimal(BenchmarkState s) {
        return s.a.toMutableDecimal();
    }

    @Benchmark
    public int benchmarkGetScale(BenchmarkState s) {
        return s.a.getScale();
    }

    @Benchmark
    public Factory10f benchmarkGetFactory(BenchmarkState s) {
        return s.a.getFactory();
    }

    @Benchmark
    public Scale10f benchmarkGetScaleMetrics(BenchmarkState s) {
        return s.a.getScaleMetrics();
    }

    @Benchmark
    public String benchmarkToString(BenchmarkState s) {
        return s.a.toString();
    }

    @Benchmark
    public void benchmarkConsumeViaBlackhole(BenchmarkState s, Blackhole bh) {
        bh.consume(s.a.add(s.b));
    }
}

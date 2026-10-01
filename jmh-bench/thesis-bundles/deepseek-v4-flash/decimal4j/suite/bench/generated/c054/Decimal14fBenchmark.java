package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.exact.Multipliable14f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Decimal14fBenchmark {

    private long longValue;
    private double doubleValue;
    private String stringValue;
    private String stringValueLonger;
    private BigDecimal bigDecimalValue;
    private Decimal14f a, b, c;
    private long unscaledValue;
    private int scale;
    private RoundingMode roundingMode;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 123456789L;
        doubleValue = 123456.7890123456789;
        stringValue = "123456789.12345678901234";
        stringValueLonger = "123456789.123456789012345";
        bigDecimalValue = new BigDecimal("123456789.1234567890123456789");
        a = Decimal14f.valueOf("12345.12345678901234");
        b = Decimal14f.valueOf("67890.98765432109876");
        c = Decimal14f.valueOf("2.5");
        unscaledValue = 123456789012345678L;
        scale = 2;
        roundingMode = RoundingMode.HALF_UP;
    }

    @Benchmark
    public Decimal14f valueOfLong() {
        return Decimal14f.valueOf(longValue);
    }

    @Benchmark
    public Decimal14f valueOfDouble() {
        return Decimal14f.valueOf(doubleValue);
    }

    @Benchmark
    public Decimal14f valueOfString() {
        return Decimal14f.valueOf(stringValue);
    }

    @Benchmark
    public Decimal14f valueOfStringRounding() {
        return Decimal14f.valueOf(stringValueLonger, roundingMode);
    }

    @Benchmark
    public Decimal14f valueOfBigDecimal() {
        return Decimal14f.valueOf(bigDecimalValue);
    }

    @Benchmark
    public Decimal14f valueOfUnscaledBench() {
        return Decimal14f.valueOfUnscaled(unscaledValue, scale, roundingMode);
    }

    @Benchmark
    public Decimal14f add() {
        return a.add(b);
    }

    @Benchmark
    public Decimal14f subtract() {
        return a.subtract(b);
    }

    @Benchmark
    public Decimal14f multiply() {
        return a.multiply(b);
    }

    @Benchmark
    public Decimal14f divide() {
        return a.divide(c);
    }

    @Benchmark
    public Decimal14f remainder() {
        return a.remainder(c);
    }

    @Benchmark
    public Decimal14f negate() {
        return a.negate();
    }

    @Benchmark
    public Decimal14f abs() {
        return a.abs();
    }

    @Benchmark
    public Decimal14f square() {
        return a.square();
    }

    @Benchmark
    public Multipliable14f multiplyExact() {
        return a.multiplyExact();
    }

    @Benchmark
    public MutableDecimal14f toMutableDecimal() {
        return a.toMutableDecimal();
    }

    @Benchmark
    public double doubleValueBench() {
        return a.doubleValue();
    }

    @Benchmark
    public long longValueBench() {
        return a.longValue();
    }

    @Benchmark
    public long unscaledValueBench() {
        return a.unscaledValue();
    }

    @Benchmark
    public BigDecimal toBigDecimal() {
        return a.toBigDecimal();
    }

    @Benchmark
    public String toStringBench() {
        return a.toString();
    }

    @Benchmark
    public int compareTo() {
        return a.compareTo(b);
    }

    @Benchmark
    public int getScale() {
        return a.getScale();
    }

    @Benchmark
    public Object getScaleMetrics() {
        return a.getScaleMetrics();
    }

    @Benchmark
    public Object getFactory() {
        return a.getFactory();
    }
}

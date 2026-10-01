package bench.generated.c073;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal14f;
import org.decimal4j.immutable.Decimal14f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable14f;
import org.decimal4j.factory.Factory14f;
import org.decimal4j.scale.Scale14f;
import java.math.BigDecimal;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal14fBenchmark {

    private long unscaledValue;
    private double doubleValue;
    private String stringValue;
    private BigDecimal bigDecimalValue;
    private BigInteger bigIntegerValue;
    private Decimal14f decimal14fValue;
    private MutableDecimal14f mutableInstance;

    @Setup(Level.Trial)
    public void setup() {
        unscaledValue = 1234567890123456L;
        doubleValue = 12345.67890123456;
        stringValue = "12345.67890123456";
        bigDecimalValue = new BigDecimal(stringValue);
        bigIntegerValue = new BigInteger("1234567890123456789");
        decimal14fValue = Decimal14f.valueOf(doubleValue);
        mutableInstance = new MutableDecimal14f(decimal14fValue);
    }

    @Benchmark
    public MutableDecimal14f benchmarkConstructorNoArgs() {
        return new MutableDecimal14f();
    }

    @Benchmark
    public MutableDecimal14f benchmarkConstructorLong() {
        return new MutableDecimal14f(unscaledValue);
    }

    @Benchmark
    public MutableDecimal14f benchmarkConstructorDouble() {
        return new MutableDecimal14f(doubleValue);
    }

    @Benchmark
    public MutableDecimal14f benchmarkConstructorString() {
        return new MutableDecimal14f(stringValue);
    }

    @Benchmark
    public MutableDecimal14f benchmarkConstructorBigDecimal() {
        return new MutableDecimal14f(bigDecimalValue);
    }

    @Benchmark
    public MutableDecimal14f benchmarkConstructorBigInteger() {
        return new MutableDecimal14f(bigIntegerValue);
    }

    @Benchmark
    public MutableDecimal14f benchmarkConstructorDecimal14f() {
        return new MutableDecimal14f(decimal14fValue);
    }

    @Benchmark
    public MutableDecimal14f benchmarkConstructorDecimalInterface() {
        Decimal<?> dec = decimal14fValue;
        return new MutableDecimal14f(dec);
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticUnscaled() {
        return MutableDecimal14f.unscaled(unscaledValue);
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticZero() {
        return MutableDecimal14f.zero();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticUlp() {
        return MutableDecimal14f.ulp();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticOne() {
        return MutableDecimal14f.one();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticTwo() {
        return MutableDecimal14f.two();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticThree() {
        return MutableDecimal14f.three();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticFour() {
        return MutableDecimal14f.four();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticFive() {
        return MutableDecimal14f.five();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticSix() {
        return MutableDecimal14f.six();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticSeven() {
        return MutableDecimal14f.seven();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticEight() {
        return MutableDecimal14f.eight();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticNine() {
        return MutableDecimal14f.nine();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticTen() {
        return MutableDecimal14f.ten();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticHundred() {
        return MutableDecimal14f.hundred();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticThousand() {
        return MutableDecimal14f.thousand();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticMinusOne() {
        return MutableDecimal14f.minusOne();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticHalf() {
        return MutableDecimal14f.half();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticTenth() {
        return MutableDecimal14f.tenth();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticHundredth() {
        return MutableDecimal14f.hundredth();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticThousandth() {
        return MutableDecimal14f.thousandth();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticMillionth() {
        return MutableDecimal14f.millionth();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticBillionth() {
        return MutableDecimal14f.billionth();
    }

    @Benchmark
    public MutableDecimal14f benchmarkStaticTrillionth() {
        return MutableDecimal14f.trillionth();
    }

    @Benchmark
    public Multipliable14f benchmarkMultiplyExact() {
        return mutableInstance.multiplyExact();
    }

    @Benchmark
    public Decimal14f benchmarkToImmutableDecimal() {
        return mutableInstance.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal14f benchmarkToMutableDecimal() {
        return mutableInstance.toMutableDecimal();
    }

    @Benchmark
    public MutableDecimal14f benchmarkClone() {
        return mutableInstance.clone();
    }

    @Benchmark
    public Scale14f benchmarkGetScaleMetrics() {
        return mutableInstance.getScaleMetrics();
    }

    @Benchmark
    public int benchmarkGetScale() {
        return mutableInstance.getScale();
    }

    @Benchmark
    public Factory14f benchmarkGetFactory() {
        return mutableInstance.getFactory();
    }
}

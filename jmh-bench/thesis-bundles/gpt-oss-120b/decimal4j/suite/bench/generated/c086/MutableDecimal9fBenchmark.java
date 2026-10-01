package bench.generated.c086;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal9f;
import org.decimal4j.immutable.Decimal9f;
import org.decimal4j.factory.Factory9f;
import org.decimal4j.exact.Multipliable9f;
import org.decimal4j.scale.Scale9f;
import java.math.BigDecimal;
import java.math.BigInteger;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal9fBenchmark {

    // mutable instance used for read‑only operations
    private MutableDecimal9f mutable;

    // values for constructors / factories
    private long longValue;
    private double doubleValue;
    private String stringValue;
    private BigDecimal bigDecimalValue;
    private BigInteger bigIntegerValue;
    private Decimal9f decimal9fConstant;

    @Setup(Level.Trial)
    public void setUp() {
        this.longValue = 123456789L;
        this.doubleValue = 12345.678901234;
        this.stringValue = "987654321.123456789";
        this.bigDecimalValue = new BigDecimal("123456789.987654321");
        this.bigIntegerValue = new BigInteger("12345678901234567890");
        this.decimal9fConstant = Decimal9f.ONE;
        this.mutable = new MutableDecimal9f(this.longValue);
    }

    // -------------------------------------------------------------------------
    // Construction / static factories
    // -------------------------------------------------------------------------

    @Benchmark
    public MutableDecimal9f benchZeroStatic() {
        return MutableDecimal9f.zero();
    }

    @Benchmark
    public MutableDecimal9f benchOneStatic() {
        return MutableDecimal9f.one();
    }

    @Benchmark
    public MutableDecimal9f benchTwoStatic() {
        return MutableDecimal9f.two();
    }

    @Benchmark
    public MutableDecimal9f benchFromLong() {
        return new MutableDecimal9f(this.longValue);
    }

    @Benchmark
    public MutableDecimal9f benchFromDouble() {
        return new MutableDecimal9f(this.doubleValue);
    }

    @Benchmark
    public MutableDecimal9f benchFromString() {
        return new MutableDecimal9f(this.stringValue);
    }

    @Benchmark
    public MutableDecimal9f benchFromBigDecimal() {
        return new MutableDecimal9f(this.bigDecimalValue);
    }

    @Benchmark
    public MutableDecimal9f benchFromBigInteger() {
        return new MutableDecimal9f(this.bigIntegerValue);
    }

    @Benchmark
    public MutableDecimal9f benchFromDecimal9f() {
        return new MutableDecimal9f(this.decimal9fConstant);
    }

    @Benchmark
    public MutableDecimal9f benchUnscaledStatic() {
        return MutableDecimal9f.unscaled(this.longValue);
    }

    // -------------------------------------------------------------------------
    // Instance methods that do not mutate the object
    // -------------------------------------------------------------------------

    @Benchmark
    public int benchGetScale() {
        return this.mutable.getScale();
    }

    @Benchmark
    public Scale9f benchGetScaleMetrics() {
        return this.mutable.getScaleMetrics();
    }

    @Benchmark
    public Factory9f benchGetFactory() {
        return this.mutable.getFactory();
    }

    @Benchmark
    public Decimal9f benchToImmutable() {
        return this.mutable.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal9f benchToMutable() {
        return this.mutable.toMutableDecimal();
    }

    @Benchmark
    public Multipliable9f benchMultiplyExact() {
        return this.mutable.multiplyExact();
    }

    @Benchmark
    public MutableDecimal9f benchClone() {
        return this.mutable.clone();
    }

    @Benchmark
    public long benchUnscaledValue() {
        return this.mutable.unscaledValue();
    }
}

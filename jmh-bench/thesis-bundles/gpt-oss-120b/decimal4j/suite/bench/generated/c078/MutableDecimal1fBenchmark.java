package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal1f;
import org.decimal4j.immutable.Decimal1f;
import org.decimal4j.api.Decimal;
import org.decimal4j.exact.Multipliable1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal1fBenchmark {

    private MutableDecimal1f a;
    private MutableDecimal1f b;
    private long longValue;
    private double doubleValue;
    private String stringValue;
    private Decimal1f immutableFive;

    @Setup(Level.Trial)
    public void setup() {
        longValue = 12345L;
        doubleValue = 1234.5;
        stringValue = "1234.5";
        a = new MutableDecimal1f(longValue);
        b = new MutableDecimal1f(doubleValue);
        immutableFive = Decimal1f.FIVE;
    }

    @Benchmark
    public MutableDecimal1f benchZero() {
        return MutableDecimal1f.zero();
    }

    @Benchmark
    public MutableDecimal1f benchOne() {
        return MutableDecimal1f.one();
    }

    @Benchmark
    public MutableDecimal1f benchFromLong() {
        return new MutableDecimal1f(longValue);
    }

    @Benchmark
    public MutableDecimal1f benchFromDouble() {
        return new MutableDecimal1f(doubleValue);
    }

    @Benchmark
    public MutableDecimal1f benchFromString() {
        return new MutableDecimal1f(stringValue);
    }

    @Benchmark
    public MutableDecimal1f benchClone() {
        return a.clone();
    }

    @Benchmark
    public MutableDecimal1f benchAdd(Blackhole bh) {
        MutableDecimal1f copy = a.clone();
        MutableDecimal1f result = copy.add(b);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal1f benchSubtract(Blackhole bh) {
        MutableDecimal1f copy = a.clone();
        MutableDecimal1f result = copy.subtract(b);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal1f benchMultiply(Blackhole bh) {
        MutableDecimal1f copy = a.clone();
        MutableDecimal1f result = copy.multiply(b);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal1f benchDivide(Blackhole bh) {
        MutableDecimal1f copy = a.clone();
        MutableDecimal1f result = copy.divide(b);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal1f benchNegate(Blackhole bh) {
        MutableDecimal1f copy = a.clone();
        MutableDecimal1f result = copy.negate();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal1f benchAbs(Blackhole bh) {
        MutableDecimal1f copy = a.clone();
        MutableDecimal1f result = copy.abs();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal1f benchSquare(Blackhole bh) {
        MutableDecimal1f copy = a.clone();
        MutableDecimal1f result = copy.square();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public MutableDecimal1f benchSqrt(Blackhole bh) {
        MutableDecimal1f copy = a.clone();
        MutableDecimal1f result = copy.sqrt();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Multipliable1f benchMultiplyExact() {
        return a.multiplyExact();
    }

    @Benchmark
    public Decimal1f benchToImmutable() {
        return a.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal1f benchToMutable() {
        return a.toMutableDecimal();
    }
}

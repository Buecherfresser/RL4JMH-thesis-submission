package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal8f;
import org.decimal4j.immutable.Decimal8f;
import org.decimal4j.immutable.Decimal16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal8fBenchmark {

    private MutableDecimal8f base;
    private MutableDecimal8f other;

    @Setup(Level.Trial)
    public void setUp() {
        base = new MutableDecimal8f(123456789L);   // 1.23456789
        other = new MutableDecimal8f(987654321L); // 9.87654321
    }

    @Benchmark
    public MutableDecimal8f constructorLong() {
        return new MutableDecimal8f(55555555L);
    }

    @Benchmark
    public MutableDecimal8f constructorString() {
        return new MutableDecimal8f("12345.67890123");
    }

    @Benchmark
    public MutableDecimal8f staticZero() {
        return MutableDecimal8f.zero();
    }

    @Benchmark
    public MutableDecimal8f staticOne() {
        return MutableDecimal8f.one();
    }

    @Benchmark
    public MutableDecimal8f staticTen() {
        return MutableDecimal8f.ten();
    }

    @Benchmark
    public MutableDecimal8f add() {
        MutableDecimal8f a = base.clone();
        a.add(other);
        return a;
    }

    @Benchmark
    public MutableDecimal8f subtract() {
        MutableDecimal8f a = base.clone();
        a.subtract(other);
        return a;
    }

    @Benchmark
    public MutableDecimal8f multiply() {
        MutableDecimal8f a = base.clone();
        a.multiply(other);
        return a;
    }

    @Benchmark
    public MutableDecimal8f divide() {
        MutableDecimal8f a = base.clone();
        a.divide(other);
        return a;
    }

    @Benchmark
    public MutableDecimal8f remainder() {
        MutableDecimal8f a = base.clone();
        a.remainder(other);
        return a;
    }

    @Benchmark
    public MutableDecimal8f negate() {
        MutableDecimal8f a = base.clone();
        a.negate();
        return a;
    }

    @Benchmark
    public MutableDecimal8f abs() {
        MutableDecimal8f a = base.clone();
        a.abs();
        return a;
    }

    @Benchmark
    public MutableDecimal8f invert() {
        MutableDecimal8f a = base.clone();
        a.invert();
        return a;
    }

    @Benchmark
    public MutableDecimal8f square() {
        MutableDecimal8f a = base.clone();
        a.square();
        return a;
    }

    @Benchmark
    public MutableDecimal8f sqrt() {
        MutableDecimal8f a = base.clone();
        a.sqrt();
        return a;
    }

    @Benchmark
    public MutableDecimal8f pow() {
        MutableDecimal8f a = base.clone();
        a.pow(3);
        return a;
    }

    @Benchmark
    public MutableDecimal8f shiftLeft() {
        MutableDecimal8f a = base.clone();
        a.shiftLeft(2);
        return a;
    }

    @Benchmark
    public MutableDecimal8f shiftRight() {
        MutableDecimal8f a = base.clone();
        a.shiftRight(2);
        return a;
    }

    @Benchmark
    public MutableDecimal8f round() {
        MutableDecimal8f a = base.clone();
        a.round(4);
        return a;
    }

    @Benchmark
    public Decimal8f toImmutable() {
        return base.clone().toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal8f cloneSelf() {
        return base.clone();
    }

    @Benchmark
    public MutableDecimal8f setLong() {
        MutableDecimal8f a = new MutableDecimal8f();
        a.set(77777777L);
        return a;
    }

    @Benchmark
    public MutableDecimal8f setString() {
        MutableDecimal8f a = new MutableDecimal8f();
        a.set("22222.33334444");
        return a;
    }

    @Benchmark
    public Decimal16f multiplyExact() {
        return base.clone().multiplyExact().by(Decimal8f.ONE);
    }

    @Benchmark
    public void consumeViaBlackhole(Blackhole bh) {
        MutableDecimal8f a = base.clone();
        a.add(other);
        bh.consume(a);
    }
}

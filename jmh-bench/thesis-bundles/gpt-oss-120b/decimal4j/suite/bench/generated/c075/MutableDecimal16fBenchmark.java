package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.decimal4j.mutable.MutableDecimal16f;
import org.decimal4j.immutable.Decimal16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal16fBenchmark {

    private static final int POOL_SIZE = 64;

    private MutableDecimal16f[] pool;
    private int idx;

    private long longValue = 1234567890123456L;
    private double doubleValue = 12345.678901234567;
    private String stringValue = "12345.678901234567";

    @Setup
    public void setup() {
        pool = new MutableDecimal16f[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            pool[i] = new MutableDecimal16f(longValue);
        }
        idx = 0;
    }

    private MutableDecimal16f nextMutable() {
        int i = idx;
        idx = (i + 1) & (POOL_SIZE - 1);
        return pool[i].clone();
    }

    @Benchmark
    public MutableDecimal16f benchmarkConstructorLong() {
        return new MutableDecimal16f(longValue);
    }

    @Benchmark
    public MutableDecimal16f benchmarkConstructorDouble() {
        return new MutableDecimal16f(doubleValue);
    }

    @Benchmark
    public MutableDecimal16f benchmarkConstructorString() {
        return new MutableDecimal16f(stringValue);
    }

    @Benchmark
    public MutableDecimal16f benchmarkStaticOne() {
        return MutableDecimal16f.one();
    }

    @Benchmark
    public MutableDecimal16f benchmarkStaticZero() {
        return MutableDecimal16f.zero();
    }

    @Benchmark
    public MutableDecimal16f benchmarkAdd() {
        MutableDecimal16f m = nextMutable();
        m.add(Decimal16f.ONE);
        return m;
    }

    @Benchmark
    public MutableDecimal16f benchmarkSubtract() {
        MutableDecimal16f m = nextMutable();
        m.subtract(Decimal16f.ONE);
        return m;
    }

    @Benchmark
    public MutableDecimal16f benchmarkMultiply() {
        MutableDecimal16f m = nextMutable();
        m.multiply(Decimal16f.TWO);
        return m;
    }

    @Benchmark
    public MutableDecimal16f benchmarkDivide() {
        MutableDecimal16f m = nextMutable();
        m.divide(Decimal16f.TWO);
        return m;
    }

    @Benchmark
    public MutableDecimal16f benchmarkNegate() {
        MutableDecimal16f m = nextMutable();
        m.negate();
        return m;
    }

    @Benchmark
    public MutableDecimal16f benchmarkAbs() {
        MutableDecimal16f m = nextMutable();
        m.abs();
        return m;
    }

    @Benchmark
    public MutableDecimal16f benchmarkSquare() {
        MutableDecimal16f m = nextMutable();
        m.square();
        return m;
    }
}

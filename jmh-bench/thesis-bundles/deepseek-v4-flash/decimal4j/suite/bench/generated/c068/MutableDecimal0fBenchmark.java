package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.immutable.Decimal0f;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal0fBenchmark {

    private static final int POOL_SIZE = 1024;

    private MutableDecimal0f[] a;
    private MutableDecimal0f[] b;
    private MutableDecimal0f[] result;
    private long[] longVals;
    private double[] doubleVals;
    private String[] stringVals;
    private BigDecimal[] bigDecimalVals;
    private int index;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(12345L);
        a = new MutableDecimal0f[POOL_SIZE];
        b = new MutableDecimal0f[POOL_SIZE];
        result = new MutableDecimal0f[POOL_SIZE];
        longVals = new long[POOL_SIZE];
        doubleVals = new double[POOL_SIZE];
        stringVals = new String[POOL_SIZE];
        bigDecimalVals = new BigDecimal[POOL_SIZE];

        for (int i = 0; i < POOL_SIZE; i++) {
            long l1 = random.nextLong() % 1_000_000L;
            long l2 = random.nextLong() % 1_000_000L;
            a[i] = new MutableDecimal0f(l1);
            b[i] = new MutableDecimal0f(l2);
            result[i] = new MutableDecimal0f();
            longVals[i] = l2;
            doubleVals[i] = (double) l2;
            stringVals[i] = Long.toString(l2);
            bigDecimalVals[i] = BigDecimal.valueOf(l2);
        }
    }

    private int nextIndex() {
        int i = index;
        index = (index + 1) & (POOL_SIZE - 1);
        return i;
    }

    @Benchmark
    public MutableDecimal0f add() {
        int i = nextIndex();
        return result[i].set(a[i]).add(b[i]);
    }

    @Benchmark
    public MutableDecimal0f subtract() {
        int i = nextIndex();
        return result[i].set(a[i]).subtract(b[i]);
    }

    @Benchmark
    public MutableDecimal0f multiply() {
        int i = nextIndex();
        return result[i].set(a[i]).multiply(b[i]);
    }

    @Benchmark
    public MutableDecimal0f divide() {
        int i = nextIndex();
        return result[i].set(a[i]).divide(b[i]);
    }

    @Benchmark
    public MutableDecimal0f negate() {
        int i = nextIndex();
        return result[i].set(a[i]).negate();
    }

    @Benchmark
    public MutableDecimal0f abs() {
        int i = nextIndex();
        return result[i].set(a[i]).abs();
    }

    @Benchmark
    public MutableDecimal0f pow() {
        int i = nextIndex();
        return result[i].set(a[i]).pow(3);
    }

    @Benchmark
    public MutableDecimal0f setLong() {
        int i = nextIndex();
        return result[i].set(longVals[i]);
    }

    @Benchmark
    public MutableDecimal0f setDouble() {
        int i = nextIndex();
        return result[i].set(doubleVals[i]);
    }

    @Benchmark
    public MutableDecimal0f setString() {
        int i = nextIndex();
        return result[i].set(stringVals[i]);
    }

    @Benchmark
    public MutableDecimal0f setBigDecimal() {
        int i = nextIndex();
        return result[i].set(bigDecimalVals[i]);
    }

    @Benchmark
    public MutableDecimal0f setDecimal() {
        int i = nextIndex();
        return result[i].set(a[i]);
    }

    @Benchmark
    public MutableDecimal0f setZero() {
        int i = nextIndex();
        return result[i].setZero();
    }

    @Benchmark
    public MutableDecimal0f setOne() {
        int i = nextIndex();
        return result[i].setOne();
    }

    @Benchmark
    public MutableDecimal0f setMinusOne() {
        int i = nextIndex();
        return result[i].setMinusOne();
    }

    @Benchmark
    public MutableDecimal0f addLong() {
        int i = nextIndex();
        return result[i].set(a[i]).add(longVals[i]);
    }

    @Benchmark
    public MutableDecimal0f subtractLong() {
        int i = nextIndex();
        return result[i].set(a[i]).subtract(longVals[i]);
    }

    @Benchmark
    public MutableDecimal0f multiplyLong() {
        int i = nextIndex();
        return result[i].set(a[i]).multiply(longVals[i]);
    }

    @Benchmark
    public MutableDecimal0f divideLong() {
        int i = nextIndex();
        return result[i].set(a[i]).divide(longVals[i]);
    }

    @Benchmark
    public MutableDecimal0f addDouble() {
        int i = nextIndex();
        return result[i].set(a[i]).add(doubleVals[i]);
    }

    @Benchmark
    public MutableDecimal0f subtractDouble() {
        int i = nextIndex();
        return result[i].set(a[i]).subtract(doubleVals[i]);
    }

    @Benchmark
    public MutableDecimal0f multiplyDouble() {
        int i = nextIndex();
        return result[i].set(a[i]).multiply(doubleVals[i]);
    }

    @Benchmark
    public MutableDecimal0f divideDouble() {
        int i = nextIndex();
        return result[i].set(a[i]).divide(doubleVals[i]);
    }

    @Benchmark
    public MutableDecimal0f addDecimal() {
        int i = nextIndex();
        return result[i].set(a[i]).add(a[i]);
    }

    @Benchmark
    public MutableDecimal0f subtractDecimal() {
        int i = nextIndex();
        return result[i].set(a[i]).subtract(a[i]);
    }

    @Benchmark
    public MutableDecimal0f multiplyDecimal() {
        int i = nextIndex();
        return result[i].set(a[i]).multiply(a[i]);
    }

    @Benchmark
    public MutableDecimal0f divideDecimal() {
        int i = nextIndex();
        return result[i].set(a[i]).divide(a[i]);
    }

    @Benchmark
    public MutableDecimal0f remainder() {
        int i = nextIndex();
        return result[i].set(a[i]).remainder(b[i]);
    }

    @Benchmark
    public MutableDecimal0f invert() {
        int i = nextIndex();
        return result[i].set(a[i]).invert();
    }

    @Benchmark
    public MutableDecimal0f square() {
        int i = nextIndex();
        return result[i].set(a[i]).square();
    }

    @Benchmark
    public MutableDecimal0f avg() {
        int i = nextIndex();
        return result[i].set(a[i]).avg(b[i]);
    }

    @Benchmark
    public MutableDecimal0f shiftLeft() {
        int i = nextIndex();
        return result[i].set(a[i]).shiftLeft(2);
    }

    @Benchmark
    public MutableDecimal0f shiftRight() {
        int i = nextIndex();
        return result[i].set(a[i]).shiftRight(2);
    }

    @Benchmark
    public MutableDecimal0f min() {
        int i = nextIndex();
        return result[i].set(a[i]).min(b[i]);
    }

    @Benchmark
    public MutableDecimal0f max() {
        int i = nextIndex();
        return result[i].set(a[i]).max(b[i]);
    }

    @Benchmark
    public MutableDecimal0f cloneBenchmark() {
        int i = nextIndex();
        return a[i].clone();
    }

    @Benchmark
    public Decimal0f toImmutable() {
        int i = nextIndex();
        return a[i].toImmutableDecimal();
    }

    @Benchmark
    public long longValue() {
        int i = nextIndex();
        return a[i].longValue();
    }

    @Benchmark
    public double doubleValue() {
        int i = nextIndex();
        return a[i].doubleValue();
    }

    @Benchmark
    public String toStringBenchmark() {
        int i = nextIndex();
        return a[i].toString();
    }

    @Benchmark
    public int hashCodeBenchmark() {
        int i = nextIndex();
        return a[i].hashCode();
    }

    @Benchmark
    public boolean equalsBenchmark() {
        int i = nextIndex();
        return a[i].equals(b[i]);
    }

    @Benchmark
    public int compareToBenchmark() {
        int i = nextIndex();
        return a[i].compareTo(b[i]);
    }

    @Benchmark
    public MutableDecimal0f unscaled() {
        int i = nextIndex();
        return MutableDecimal0f.unscaled(longVals[i]);
    }

    @Benchmark
    public MutableDecimal0f zero() {
        return MutableDecimal0f.zero();
    }

    @Benchmark
    public MutableDecimal0f one() {
        return MutableDecimal0f.one();
    }

    @Benchmark
    public MutableDecimal0f ten() {
        return MutableDecimal0f.ten();
    }

    @Benchmark
    public MutableDecimal0f hundred() {
        return MutableDecimal0f.hundred();
    }

    @Benchmark
    public MutableDecimal0f thousand() {
        return MutableDecimal0f.thousand();
    }

    @Benchmark
    public MutableDecimal0f million() {
        return MutableDecimal0f.million();
    }

    @Benchmark
    public MutableDecimal0f billion() {
        return MutableDecimal0f.billion();
    }

    @Benchmark
    public MutableDecimal0f trillion() {
        return MutableDecimal0f.trillion();
    }

    @Benchmark
    public MutableDecimal0f quadrillion() {
        return MutableDecimal0f.quadrillion();
    }

    @Benchmark
    public MutableDecimal0f quintillion() {
        return MutableDecimal0f.quintillion();
    }

    @Benchmark
    public MutableDecimal0f minusOne() {
        return MutableDecimal0f.minusOne();
    }

    @Benchmark
    public MutableDecimal0f ulp() {
        return MutableDecimal0f.ulp();
    }

    @Benchmark
    public MutableDecimal0f two() {
        return MutableDecimal0f.two();
    }

    @Benchmark
    public MutableDecimal0f three() {
        return MutableDecimal0f.three();
    }

    @Benchmark
    public MutableDecimal0f four() {
        return MutableDecimal0f.four();
    }

    @Benchmark
    public MutableDecimal0f five() {
        return MutableDecimal0f.five();
    }

    @Benchmark
    public MutableDecimal0f six() {
        return MutableDecimal0f.six();
    }

    @Benchmark
    public MutableDecimal0f seven() {
        return MutableDecimal0f.seven();
    }

    @Benchmark
    public MutableDecimal0f eight() {
        return MutableDecimal0f.eight();
    }

    @Benchmark
    public MutableDecimal0f nine() {
        return MutableDecimal0f.nine();
    }
}

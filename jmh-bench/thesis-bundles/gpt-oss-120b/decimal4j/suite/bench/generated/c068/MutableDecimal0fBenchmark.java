package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.decimal4j.mutable.MutableDecimal0f;
import org.decimal4j.immutable.Decimal0f;
import org.decimal4j.exact.Multipliable0f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal0fBenchmark {

    private MutableDecimal0f mutableDecimal;
    private long longValue;
    private double doubleValue;
    private String stringValue;
    private BigDecimal bigDecimalValue;
    private BigInteger bigIntegerValue;

    @Setup
    public void setup() {
        mutableDecimal = MutableDecimal0f.one();

        longValue = 123456789L;
        doubleValue = 123456789.987;
        stringValue = "123456789";
        bigDecimalValue = new BigDecimal("123456789.987654321");
        bigIntegerValue = new BigInteger("1234567890123456789");
    }

    @Benchmark
    public MutableDecimal0f benchmarkZeroFactory() {
        return MutableDecimal0f.zero();
    }

    @Benchmark
    public MutableDecimal0f benchmarkOneFactory() {
        return MutableDecimal0f.one();
    }

    @Benchmark
    public MutableDecimal0f benchmarkTwoFactory() {
        return MutableDecimal0f.two();
    }

    @Benchmark
    public MutableDecimal0f benchmarkUnscaledFactory() {
        return MutableDecimal0f.unscaled(longValue);
    }

    @Benchmark
    public MutableDecimal0f benchmarkSetLong() {
        mutableDecimal.set(longValue);
        return mutableDecimal;
    }

    @Benchmark
    public MutableDecimal0f benchmarkSetDouble() {
        mutableDecimal.set(doubleValue);
        return mutableDecimal;
    }

    @Benchmark
    public MutableDecimal0f benchmarkSetString() {
        mutableDecimal.set(stringValue);
        return mutableDecimal;
    }

    @Benchmark
    public MutableDecimal0f benchmarkSetBigDecimal() {
        mutableDecimal.set(bigDecimalValue);
        return mutableDecimal;
    }

    @Benchmark
    public MutableDecimal0f benchmarkSetBigInteger() {
        mutableDecimal.set(bigIntegerValue);
        return mutableDecimal;
    }

    @Benchmark
    public Multipliable0f benchmarkMultiplyExact() {
        return mutableDecimal.multiplyExact();
    }

    @Benchmark
    public Decimal0f benchmarkToImmutable() {
        return mutableDecimal.toImmutableDecimal();
    }

    @Benchmark
    public MutableDecimal0f benchmarkToMutable() {
        return mutableDecimal.toMutableDecimal();
    }
}

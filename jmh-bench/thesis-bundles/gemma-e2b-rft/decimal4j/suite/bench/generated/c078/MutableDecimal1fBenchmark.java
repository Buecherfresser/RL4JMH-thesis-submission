package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.decimal4j.mutable.MutableDecimal1f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class MutableDecimal1fBenchmark {

    private MutableDecimal1f decimal;
    private String stringInput;
    private BigDecimal bigDecimalInput;
    private double doubleInput;

    @Setup
    public void setup() {
        // Setup common inputs
        this.stringInput = "123.45";
        this.doubleInput = 123.45;
        this.bigDecimalInput = new BigDecimal("123.45");
    }

    @Benchmark
    public void constructorFromLong(Blackhole bh) {
        long value = 123456789L;
        MutableDecimal1f d = new MutableDecimal1f(value);
        bh.consume(d);
    }

    @Benchmark
    public void constructorFromDouble(Blackhole bh) {
        double value = 123.45;
        MutableDecimal1f d = new MutableDecimal1f(value);
        bh.consume(d);
    }

    @Benchmark
    public void setFromString(Blackhole bh) {
        MutableDecimal1f d = new MutableDecimal1f();
        d.set(stringInput, java.math.RoundingMode.HALF_UP);
        bh.consume(d);
    }

    @Benchmark
    public void setFromBigDecimal(Blackhole bh) {
        MutableDecimal1f d = new MutableDecimal1f();
        d.set(bigDecimalInput, java.math.RoundingMode.HALF_UP);
        bh.consume(d);
    }

    @Benchmark
    public void clone(Blackhole bh) {
        MutableDecimal1f original = new MutableDecimal1f(100L);
        MutableDecimal1f cloned = original.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void staticOne(Blackhole bh) {
        MutableDecimal1f d = MutableDecimal1f.one();
        bh.consume(d);
    }

    @Benchmark
    public void staticHalf(Blackhole bh) {
        MutableDecimal1f d = MutableDecimal1f.half();
        bh.consume(d);
    }

    @Benchmark
    public void staticTenth(Blackhole bh) {
        MutableDecimal1f d = MutableDecimal1f.tenth();
        bh.consume(d);
    }
}

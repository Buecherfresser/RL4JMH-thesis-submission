package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal16f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal16fBenchmark {

    // State fields for read-only/reusable objects
    private MutableDecimal16f zero;
    private MutableDecimal16f one;
    private MutableDecimal16f two;
    private MutableDecimal16f ten;
    private MutableDecimal16f hundred;
    private MutableDecimal16f half;

    @Setup
    public void setup() {
        // Initialize reusable objects once per trial
        this.zero = MutableDecimal16f.zero();
        this.one = MutableDecimal16f.one();
        this.two = MutableDecimal16f.two();
        this.ten = MutableDecimal16f.ten();
        this.hundred = MutableDecimal16f.hundred();
        this.half = MutableDecimal16f.half();
    }

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        // Test a simple read/consumption operation
        bh.consume(zero);
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        // Test a simple read/consumption operation
        bh.consume(one);
    }

    @Benchmark
    public void benchmarkTwo(Blackhole bh) {
        // Test a simple read/consumption operation
        bh.consume(two);
    }

    @Benchmark
    public void benchmarkTen(Blackhole bh) {
        // Test a simple read/consumption operation
        bh.consume(ten);
    }

    @Benchmark
    public void benchmarkHundred(Blackhole bh) {
        // Test a simple read/consumption operation
        bh.consume(hundred);
    }

    @Benchmark
    public void benchmarkHalf(Blackhole bh) {
        // Test a simple read/consumption operation
        bh.consume(half);
    }

    @Benchmark
    public void benchmarkUnscaled(Blackhole bh) {
        // Test static factory method
        bh.consume(MutableDecimal16f.unscaled(1234567890123456L));
    }

    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        // Test clone operation
        MutableDecimal16f original = MutableDecimal16f.one();
        bh.consume(original.clone());
    }

    @Benchmark
    public void benchmarkToImmutable(Blackhole bh) {
        // Test conversion to immutable
        MutableDecimal16f mutableVal = MutableDecimal16f.two();
        bh.consume(mutableVal.toImmutableDecimal());
    }
}

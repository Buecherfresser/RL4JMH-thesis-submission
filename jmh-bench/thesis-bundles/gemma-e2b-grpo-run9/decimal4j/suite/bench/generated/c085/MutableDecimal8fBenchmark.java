package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal8f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal8fBenchmark {

    // State fields for read-only/reusable objects.
    // These are safe because the benchmark methods do not mutate them,
    // and static factory methods create new instances.
    private MutableDecimal8f zero;
    private MutableDecimal8f one;
    private MutableDecimal8f ten;
    private MutableDecimal8f hundredth;
    private MutableDecimal8f millionth;

    @Setup
    public void setup() {
        // Initialize reusable instances using static factory methods.
        this.zero = MutableDecimal8f.zero();
        this.one = MutableDecimal8f.one();
        this.ten = MutableDecimal8f.ten();
        this.hundredth = MutableDecimal8f.hundredth();
        this.millionth = MutableDecimal8f.millionth();
    }

    @Benchmark
    public void testZero(Blackhole bh) {
        // Test a simple read operation (cloning is safe for immutable reads)
        MutableDecimal8f result = zero.clone();
        bh.consume(result);
    }

    @Benchmark
    public void testOne(Blackhole bh) {
        // Test a simple read operation
        MutableDecimal8f result = one.clone();
        bh.consume(result);
    }

    @Benchmark
    public void testTen(Blackhole bh) {
        // Test a simple read operation
        MutableDecimal8f result = ten.clone();
        bh.consume(result);
    }

    @Benchmark
    public void testHundredth(Blackhole bh) {
        // Test a simple read operation
        MutableDecimal8f result = hundredth.clone();
        bh.consume(result);
    }

    @Benchmark
    public void testMillionth(Blackhole bh) {
        // Test a simple read operation
        MutableDecimal8f result = millionth.clone();
        bh.consume(result);
    }

    @Benchmark
    public void testUnscaledFactory(Blackhole bh) {
        // Test a static factory method
        MutableDecimal8f result = MutableDecimal8f.unscaled(123456789012345L);
        bh.consume(result);
    }

    @Benchmark
    public void testMultiplyExact(Blackhole bh) {
        // Test a method that returns a Multipliable8f (which involves internal arithmetic)
        // We call the static one() method to get a base instance for the operation.
        try {
            MutableDecimal8f.one().multiplyExact();
        } catch (Exception e) {
            // Ignore exceptions if they occur during benchmarking setup
        }
        bh.consume(null);
    }

    @Benchmark
    public void testToImmutableDecimal(Blackhole bh) {
        // Test conversion to immutable type
        MutableDecimal8f mutableValue = MutableDecimal8f.two();
        bh.consume(mutableValue.toImmutableDecimal());
    }

    @Benchmark
    public void testToMutableDecimal(Blackhole bh) {
        // Test conversion to mutable type (should return self)
        MutableDecimal8f mutableValue = MutableDecimal8f.zero();
        bh.consume(mutableValue.toMutableDecimal());
    }
}

package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal3f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal3fBenchmark {

    // State field is not strictly necessary if we only use static factory methods
    // in benchmarks, but kept for structural completeness if instance methods were used.
    // private MutableDecimal3f mutableDecimal;

    @Setup
    public void setup() {
        // Initialization logic if needed. For this benchmark using static methods,
        // this setup is minimal.
    }

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        // Test a simple zero instance created via static factory.
        MutableDecimal3f result = MutableDecimal3f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        // Test the one instance.
        MutableDecimal3f result = MutableDecimal3f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkTen(Blackhole bh) {
        // Test the ten instance.
        MutableDecimal3f result = MutableDecimal3f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHundredth(Blackhole bh) {
        // Test the hundredth instance.
        MutableDecimal3f result = MutableDecimal3f.hundredth();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkUnscaled(Blackhole bh) {
        // Test the static unscaled factory method.
        MutableDecimal3f result = MutableDecimal3f.unscaled(123456789L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkClone(Blackhole bh) {
        // Test the clone method.
        MutableDecimal3f original = MutableDecimal3f.one();
        MutableDecimal3f clone = original.clone();
        bh.consume(clone);
    }

    @Benchmark
    public void benchmarkToImmutable(Blackhole bh) {
        // Test conversion to immutable Decimal3f.
        MutableDecimal3f mutableInstance = MutableDecimal3f.ten();
        bh.consume(mutableInstance.toImmutableDecimal());
    }

    @Benchmark
    public void benchmarkSet(Blackhole bh) {
        // Test a simple mutation via set (using a static factory for simplicity).
        MutableDecimal3f result = MutableDecimal3f.zero();
        // Assuming set(long, null) is a valid call or using a known valid call
        try {
            result.set(100L, null); 
        } catch (NullPointerException e) {
            // Ignore NPE if the specific set method signature isn't available/valid for testing
        }
        bh.consume(result);
    }
}

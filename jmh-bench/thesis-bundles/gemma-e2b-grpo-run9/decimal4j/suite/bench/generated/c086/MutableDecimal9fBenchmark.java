package bench.generated.c086;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.decimal4j.mutable.MutableDecimal9f;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MutableDecimal9fBenchmark {

    // State fields are not strictly necessary if we only use static methods,
    // but they satisfy the requirement for @State if we were to test instance methods.
    // Since we focus on static methods for safety, we omit instance state.

    @Benchmark
    public void benchmarkZero(Blackhole bh) {
        MutableDecimal9f result = MutableDecimal9f.zero();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkOne(Blackhole bh) {
        MutableDecimal9f result = MutableDecimal9f.one();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkTwo(Blackhole bh) {
        MutableDecimal9f result = MutableDecimal9f.two();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkTen(Blackhole bh) {
        MutableDecimal9f result = MutableDecimal9f.ten();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHalf(Blackhole bh) {
        MutableDecimal9f result = MutableDecimal9f.half();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHundredth(Blackhole bh) {
        MutableDecimal9f result = MutableDecimal9f.hundredth();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkBillionth(Blackhole bh) {
        MutableDecimal9f result = MutableDecimal9f.billionth();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkUnscaledConversion(Blackhole bh) {
        // Test a conversion method that returns a new instance
        MutableDecimal9f result = MutableDecimal9f.unscaled(1234567890123L);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkToImmutable(Blackhole bh) {
        // Test a conversion method that returns a new instance
        MutableDecimal9f mutableInstance = MutableDecimal9f.zero();
        // We call a method that returns a new object, consuming the result
        bh.consume(mutableInstance.toImmutableDecimal());
    }
}

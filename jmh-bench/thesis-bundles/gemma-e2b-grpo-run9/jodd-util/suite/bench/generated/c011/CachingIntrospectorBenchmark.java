package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.introspector.CachingIntrospector;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CachingIntrospectorBenchmark {

    // The subject under test. Initialized once per benchmark run.
    private CachingIntrospector introspector;

    @Setup
    public void setup() {
        // Initialize the introspector. Using the default constructor.
        this.introspector = new CachingIntrospector();
    }

    @Benchmark
    public void benchmarkLookup(Blackhole bh) {
        try {
            // Call the public method. The result is consumed by Blackhole.
            introspector.lookup(String.class);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes
        }
    }

    @Benchmark
    public void benchmarkReset(Blackhole bh) {
        try {
            // Call the reset method.
            introspector.reset();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

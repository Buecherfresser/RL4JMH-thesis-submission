package bench.generated.c011;

import jodd.introspector.CachingIntrospector;
import jodd.introspector.ClassDescriptor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CachingIntrospectorBenchmark {

    private CachingIntrospector introspector;
    private Class<?> testClass;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize the SUT using the default constructor
        introspector = new CachingIntrospector();
        
        // Use a standard class as input for lookup
        testClass = ArrayList.class;
    }

    /**
     * Benchmarks the lookup operation. The first few calls will involve
     * reflection/scanning (cache miss), subsequent calls will be fast (cache hit).
     * This measures the average time of the lookup process.
     */
    @Benchmark
    public ClassDescriptor lookupBenchmark(Blackhole bh) {
        // The result must be consumed or returned
        ClassDescriptor descriptor = introspector.lookup(testClass);
        bh.consume(descriptor);
        return descriptor;
    }

    /**
     * Benchmarks the reset operation, which clears the internal cache.
     */
    @Benchmark
    public void resetBenchmark(Blackhole bh) {
        introspector.reset();
        bh.consume(true); // Consume result to prevent dead code elimination
    }
}

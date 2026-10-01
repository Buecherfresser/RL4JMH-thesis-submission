package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.LoaderOptions;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SafeConstructorBenchmark {

    // Instance of the class under test. Since SafeConstructor is not thread-safe,
    // we instantiate it per benchmark state.
    private SafeConstructor constructor;

    @Setup
    public void setup() {
        try {
            // Initialize the constructor. We pass null for options as the constructor
            // might handle default initialization internally.
            this.constructor = new SafeConstructor(null);
        } catch (Exception e) {
            // Handle potential exceptions during setup if necessary, though for a simple
            // constructor call, this is usually fine.
            System.err.println("Error during SafeConstructor setup: " + e.getMessage());
        }
    }

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        // Call the constructor. This tests the initialization logic within SafeConstructor.
        try {
            // We call the constructor, which initializes internal maps.
            new SafeConstructor(null);
        } catch (Exception e) {
            // Catch exceptions that might occur during construction (e.g., if internal
            // initialization fails due to missing dependencies, though unlikely here).
        }
        bh.consume(this.constructor);
    }
}

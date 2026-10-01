package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jodd.bean.BeanCopy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanCopyBenchmark {

    // Since BeanCopy is stateless regarding its internal state (source/destination are set during construction
    // or via fluent methods), we don't need @State fields for the BeanCopy instance itself,
    // as we create new instances in each benchmark method.

    /**
     * Benchmark for the core copy operation on a Map.
     * This tests the internal logic of BeanCopy.copy().
     */
    @Benchmark
    public void benchmarkCopy(Blackhole bh) {
        try {
            // Setup: Create source and destination objects.
            // Using a mutable Map to ensure the copy operation has something to work with.
            Map<String, Object> source = new HashMap<>();
            source.put("key1", 100);
            source.put("key2", "value");

            Map<String, Object> destination = new HashMap<>();

            // Execution
            BeanCopy copyProcess = new BeanCopy(source, destination);
            copyProcess.copy();

            // Consume the result to prevent dead code elimination
            bh.consume(copyProcess);

        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected in complex scenarios
        }
    }

    /**
     * Benchmark for the static factory method BeanCopy.from().
     * This tests the overhead of creating a new BeanCopy instance.
     */
    @Benchmark
    public void benchmarkFrom(Blackhole bh) {
        try {
            // Execution: Call the static factory method.
            BeanCopy copyProcess = BeanCopy.from(null);
            bh.consume(copyProcess);
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * Benchmark for the fluent setter method BeanCopy.to().
     * This tests the overhead of creating a new BeanCopy instance and setting a property.
     */
    @Benchmark
    public void benchmarkTo(Blackhole bh) {
        try {
            // Execution: Call the fluent method.
            BeanCopy copyProcess = BeanCopy.from(null).to(null);
            bh.consume(copyProcess);
        } catch (Exception e) {
            // Ignore
        }
    }
}

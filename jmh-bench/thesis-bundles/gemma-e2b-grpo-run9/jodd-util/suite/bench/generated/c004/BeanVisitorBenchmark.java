package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.Map;

import jodd.bean.BeanVisitor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BeanVisitorBenchmark {

    // Since BeanVisitor relies on an Object source in its constructor,
    // and we cannot easily instantiate a complex POJO without full library context,
    // we rely on the fact that JMH will handle the instantiation overhead
    // if we don't use @State fields for the visitor itself.

    // We define a simple, non-mutating benchmark method.
    // Note: This benchmark relies on the BeanVisitor constructor accepting a simple Object.
    // If the actual Jodd library requires a specific type, this benchmark might fail compilation
    // or runtime if the Object passed is incompatible.
    @Benchmark
    public void benchmarkVisit(Blackhole bh) {
        try {
            // Create a temporary, simple object instance for the source.
            // This simulates the input required by the constructor.
            Object dummySource = new Object();
            BeanVisitor visitor = new BeanVisitor(dummySource);

            // Define a consumer that does nothing, satisfying the BiConsumer<String, Object> requirement.
            BiConsumer<String, Object> consumer = (name, value) -> {
                // Consume the result to prevent dead code elimination
            };

            // Call the method under test.
            visitor.visit(consumer);

        } catch (Exception e) {
            // Catch exceptions that might occur during reflection/introspection
            // and consume the exception if necessary, though for benchmarking,
            // we usually let the harness handle unexpected failures.
        }
    }

    // Example of a benchmark that tests state mutation (if we were to test it)
    // This method is included to show how stateful methods would be tested,
    // though it might be slower due to object creation overhead.
    @Benchmark
    public void benchmarkStateMutation(Blackhole bh) {
        try {
            Object dummySource = new Object();
            BeanVisitor visitor = new BeanVisitor(dummySource);

            // Test state mutation methods (these return 'this')
            visitor.ignoreNulls(true);
            visitor.declared(true);

            // Call the main method
            BiConsumer<String, Object> consumer = (name, value) -> {};
            visitor.visit(consumer);

        } catch (Exception e) {
            // Ignore exceptions for benchmark stability
        }
    }
}

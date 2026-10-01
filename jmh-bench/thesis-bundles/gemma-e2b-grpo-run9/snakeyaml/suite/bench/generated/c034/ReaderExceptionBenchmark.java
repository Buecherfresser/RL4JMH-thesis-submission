package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.reader.ReaderException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ReaderExceptionBenchmark {

    // Since ReaderException is immutable and we are measuring instantiation cost,
    // we don't strictly need a @State field, but we keep the class structure clean.

    @Benchmark
    public void createAndConsumeException(Blackhole bh) {
        try {
            // Create an instance. This measures the cost of construction.
            ReaderException exception = new ReaderException(
                "TestName",
                100,
                65535,
                "Test message"
            );
            // Consume the result to prevent dead code elimination
            bh.consume(exception);
        } catch (Exception e) {
            // Should not happen in this simple benchmark
        }
    }

    @Benchmark
    public void accessGetName(Blackhole bh) {
        try {
            // Create an instance and call a getter.
            ReaderException exception = new ReaderException(
                "TestName",
                100,
                65535,
                "Test message"
            );
            // Call a method and consume the result
            bh.consume(exception.getName());
        } catch (Exception e) {
            // Should not happen
        }
    }
}

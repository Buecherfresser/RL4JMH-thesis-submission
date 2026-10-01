package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.serializer.SerializerException;
import org.yaml.snakeyaml.error.YAMLException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SerializerExceptionBenchmark {

    // Since the constructor takes a String and we are benchmarking the construction cost,
    // we don't need complex state setup or mutable fields.

    /**
     * Benchmarks the construction of SerializerException with a simple message.
     * The result is consumed via Blackhole to prevent dead code elimination.
     *
     * @param bh The Blackhole to consume the result.
     */
    @Benchmark
    public void testConstructor(Blackhole bh) {
        try {
            // Call the constructor of the target class
            new SerializerException("Test message");
        } catch (Exception e) {
            // Catching potential exceptions during construction if any, though unlikely here
        }
        // Consume the result (void method, so we just ensure the call happens)
        bh.consume(null);
    }
}

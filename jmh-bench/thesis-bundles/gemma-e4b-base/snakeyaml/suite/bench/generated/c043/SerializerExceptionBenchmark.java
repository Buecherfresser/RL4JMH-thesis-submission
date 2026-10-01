package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.serializer.SerializerException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SerializerExceptionBenchmark {

    private String errorMessage;

    @Setup
    public void setup() {
        // Input is the message string
        errorMessage = "A serialization error occurred during processing.";
    }

    @Benchmark
    public SerializerException benchmarkExceptionCreation(Blackhole bh) {
        // Call the subject method: SerializerException(String message)
        SerializerException exception = new SerializerException(errorMessage);
        
        // Consume the result
        bh.consume(exception);
        return exception;
    }
}

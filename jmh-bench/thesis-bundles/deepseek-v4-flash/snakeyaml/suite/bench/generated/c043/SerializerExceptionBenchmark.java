package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.serializer.SerializerException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SerializerExceptionBenchmark {

    private String message;
    private SerializerException exception;

    @Setup(Level.Trial)
    public void setup() {
        message = "A serialization error occurred";
        exception = new SerializerException(message);
    }

    @Benchmark
    public SerializerException construct() {
        return new SerializerException(message);
    }

    @Benchmark
    public String getMessage() {
        return exception.getMessage();
    }
}

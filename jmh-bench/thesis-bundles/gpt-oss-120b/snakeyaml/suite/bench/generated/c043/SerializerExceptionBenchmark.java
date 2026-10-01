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

    private String message;
    private SerializerException prebuiltException;

    @Setup(Level.Trial)
    public void setUp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Error during serialization: ");
        for (int i = 0; i < 10; i++) {
            sb.append("detail").append(i).append(' ');
        }
        message = sb.toString();
        prebuiltException = new SerializerException(message);
    }

    @Benchmark
    public SerializerException constructException() {
        return new SerializerException(message);
    }

    @Benchmark
    public String getMessage() {
        return prebuiltException.getMessage();
    }

    @Benchmark
    public void consumeMessage(Blackhole bh) {
        bh.consume(prebuiltException.getMessage());
    }
}

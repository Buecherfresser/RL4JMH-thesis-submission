package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.emitter.EmitterException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class EmitterExceptionBenchmark {

    private String message;
    private EmitterException preCreated;

    @Setup(Level.Trial)
    public void setUp() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("msg");
            sb.append(i);
            sb.append(' ');
        }
        message = sb.toString();
        preCreated = new EmitterException(message);
    }

    @Benchmark
    public EmitterException constructException() {
        return new EmitterException(message);
    }

    @Benchmark
    public String getMessage() {
        return preCreated.getMessage();
    }
}

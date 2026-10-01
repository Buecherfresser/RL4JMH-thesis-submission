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

    private String exceptionMessage;

    @Setup
    public void setup() {
        // Prepare a fixed payload for the exception message.
        this.exceptionMessage = "Test message for EmitterException.";
    }

    @Benchmark
    public void createException(Blackhole bh) {
        // Call the constructor exactly once per invocation.
        EmitterException ex = new EmitterException(exceptionMessage);
        // Consume the result to prevent dead code elimination.
        bh.consume(ex);
    }
}

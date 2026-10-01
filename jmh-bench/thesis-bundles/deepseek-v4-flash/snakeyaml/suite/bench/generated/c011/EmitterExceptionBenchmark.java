package bench.generated.c011;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.yaml.snakeyaml.emitter.EmitterException;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class EmitterExceptionBenchmark {

    private String message;
    private EmitterException exception;

    @Setup(Level.Trial)
    public void setUp() {
        StringBuilder sb = new StringBuilder();
        sb.append("Failed to emit YAML directive at line ");
        sb.append(123);
        sb.append(", column ");
        sb.append(45);
        message = sb.toString();
        exception = new EmitterException(message);
    }

    @Benchmark
    public EmitterException constructException() {
        return new EmitterException(message);
    }

    @Benchmark
    public String getMessage() {
        return exception.getMessage();
    }
}

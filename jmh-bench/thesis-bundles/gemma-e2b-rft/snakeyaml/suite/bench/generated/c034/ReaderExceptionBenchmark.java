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

    private ReaderException exception;

    @Setup
    public void setup() {
        // Build fixed inputs for the ReaderException constructor
        String name = "TestError";
        int position = 12345;
        int codePoint = 65533; // A high code point
        String message = "Simulated YAML parsing error.";

        this.exception = new ReaderException(name, position, codePoint, message);
    }

    @Benchmark
    public void benchmarkExceptionCreation(Blackhole bh) {
        // Call the constructor once per benchmark invocation
        ReaderException e = new ReaderException("TestError", 12345, 65533, "Simulated YAML parsing error.");
        bh.consume(e);
    }

    @Benchmark
    public void benchmarkExceptionGetters(Blackhole bh) {
        // Test accessing the public getters
        String name = exception.getName();
        int codePoint = exception.getCodePoint();
        int position = exception.getPosition();

        bh.consume(name);
        bh.consume(codePoint);
        bh.consume(position);
    }
}

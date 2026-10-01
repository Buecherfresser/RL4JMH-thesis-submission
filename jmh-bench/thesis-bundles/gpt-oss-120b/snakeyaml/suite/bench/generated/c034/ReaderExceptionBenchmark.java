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
    public void setUp() {
        int codePoint = 0x1F600; // 😀
        exception = new ReaderException("test.yaml", 42, codePoint, "Invalid character");
    }

    @Benchmark
    public ReaderException benchmarkCreateException() {
        int cp = 0x1F603; // 😃
        return new ReaderException("generated.yaml", 10, cp, "Another invalid character");
    }

    @Benchmark
    public String benchmarkGetName() {
        return exception.getName();
    }

    @Benchmark
    public int benchmarkGetCodePoint() {
        return exception.getCodePoint();
    }

    @Benchmark
    public int benchmarkGetPosition() {
        return exception.getPosition();
    }

    @Benchmark
    public String benchmarkToString() {
        return exception.toString();
    }

    @Benchmark
    public void benchmarkConsumeToString(Blackhole bh) {
        bh.consume(exception.toString());
    }
}

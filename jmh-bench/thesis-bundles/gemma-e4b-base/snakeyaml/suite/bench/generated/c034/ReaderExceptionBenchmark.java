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

    private ReaderException exceptionInstance;
    private final String testName = "test_name";
    private final int testPosition = 100;
    private final int testCodePoint = 0xFFFD;
    private final String testMessage = "Unacceptable character found";

    @Setup(Level.Trial)
    public void setup() {
        // Build the exception instance once for all benchmarks
        exceptionInstance = new ReaderException(testName, testPosition, testCodePoint, testMessage);
    }

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        // Recreate the exception instance to measure construction time
        ReaderException ex = new ReaderException(testName, testPosition, testCodePoint, testMessage);
        bh.consume(ex);
    }

    @Benchmark
    public void benchmarkGetName(Blackhole bh) {
        String name = exceptionInstance.getName();
        bh.consume(name);
    }

    @Benchmark
    public void benchmarkGetCodePoint(Blackhole bh) {
        int codePoint = exceptionInstance.getCodePoint();
        bh.consume(codePoint);
    }

    @Benchmark
    public void benchmarkGetPosition(Blackhole bh) {
        int position = exceptionInstance.getPosition();
        bh.consume(position);
    }

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        String result = exceptionInstance.toString();
        bh.consume(result);
    }
}

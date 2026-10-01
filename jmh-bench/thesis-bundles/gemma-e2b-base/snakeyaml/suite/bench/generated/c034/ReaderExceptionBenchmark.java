package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.reader.ReaderException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ReaderExceptionBenchmark {

    // State fields to hold inputs built in @Setup
    private String exceptionName;
    private int exceptionPosition;
    private int exceptionCodePoint;
    private String exceptionMessage;

    // The subject instance is not strictly necessary if we only benchmark construction,
    // but we can use a holder if we were benchmarking a method on an existing object.
    // Since we are benchmarking the class behavior (construction), we rely on the setup state.

    @Setup
    public void setup() {
        // Build fixed, non-literal inputs for the exception constructor.
        this.exceptionName = "TestError";
        this.exceptionPosition = 12345;
        this.exceptionCodePoint = 65535; // Max valid Unicode code point
        this.exceptionMessage = "Simulated parsing error.";
    }

    @Benchmark
    public void benchmarkExceptionConstruction(Blackhole bh) {
        // Call the primary public method (constructor) and consume the result.
        ReaderException exception = new ReaderException(
                exceptionName,
                exceptionPosition,
                exceptionCodePoint,
                exceptionMessage
        );
        bh.consume(exception);
    }

    @Benchmark
    public void benchmarkExceptionConstructionHighCodePoint(Blackhole bh) {
        // Test construction with a different, high code point value.
        String name = "HighCPError";
        int position = 999999;
        int codePoint = 0x10FFFF; // Max Unicode code point
        String message = "Max code point failure.";

        ReaderException exception = new ReaderException(
                name,
                position,
                codePoint,
                message
        );
        bh.consume(exception);
    }
}

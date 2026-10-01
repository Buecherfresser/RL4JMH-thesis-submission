package bench.generated.c039;

import org.apache.commons.compress.compressors.CompressorException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CompressorExceptionBenchmark {

    // State fields for inputs
    private String simpleMessage;
    private String complexMessage;
    private Throwable simpleCause;
    private Throwable complexCause;

    // State field to hold the constructed exception for benchmarking
    private CompressorException simpleException;
    private CompressorException complexException;

    @Setup
    public void setup() {
        // Setup simple inputs
        this.simpleMessage = "Simple error message";
        this.simpleCause = new RuntimeException("Simple cause");

        // Setup complex inputs
        this.complexMessage = "A much longer and more complex error message designed to test string handling performance";
        this.complexCause = new IllegalStateException("Complex internal failure");

        // Pre-construct exceptions to avoid setup overhead in the benchmark loop
        this.simpleException = new CompressorException(simpleMessage);
        this.complexException = new CompressorException(complexMessage, complexCause);
    }

    @Benchmark
    public void createSimpleException(Blackhole bh) {
        CompressorException ex = new CompressorException(simpleMessage);
        bh.consume(ex);
    }

    @Benchmark
    public void createComplexException(Blackhole bh) {
        CompressorException ex = new CompressorException(complexMessage, complexCause);
        bh.consume(ex);
    }
}

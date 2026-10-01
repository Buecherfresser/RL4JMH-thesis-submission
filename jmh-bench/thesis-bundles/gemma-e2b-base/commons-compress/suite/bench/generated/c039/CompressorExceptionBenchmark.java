package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.CompressorException;
import java.io.IOException;
import java.lang.RuntimeException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class CompressorExceptionBenchmark {

    private String simpleMessage;
    private RuntimeException simpleCause;
    private String complexMessage;
    private Throwable complexCause;

    @Setup
    public void setup() {
        // Setup simple inputs
        this.simpleMessage = "Test message for simple exception";
        this.simpleCause = new RuntimeException("Simple runtime error");

        // Setup complex inputs
        this.complexMessage = "A much longer message designed to test string handling performance";
        this.complexCause = new IOException("Complex IO failure");
    }

    @Benchmark
    public void createSimpleException(Blackhole bh) {
        CompressorException ex = new CompressorException(simpleMessage);
        bh.consume(ex);
    }

    @Benchmark
    public void createSimpleExceptionWithCause(Blackhole bh) {
        CompressorException ex = new CompressorException(simpleMessage, simpleCause);
        bh.consume(ex);
    }

    @Benchmark
    public void createComplexException(Blackhole bh) {
        CompressorException ex = new CompressorException(complexMessage);
        bh.consume(ex);
    }

    @Benchmark
    public void createComplexExceptionWithCause(Blackhole bh) {
        CompressorException ex = new CompressorException(complexMessage, complexCause);
        bh.consume(ex);
    }
}

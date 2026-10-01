package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.FlushShieldFilterOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlushShieldFilterOutputStreamBenchmark {

    private FlushShieldFilterOutputStream flushShieldStream;
    private ByteArrayOutputStream underlyingStream;

    @Setup(Level.Trial)
    public void setup() {
        // Use ByteArrayOutputStream as the underlying stream
        underlyingStream = new ByteArrayOutputStream();
        // Initialize the SUT
        flushShieldStream = new FlushShieldFilterOutputStream(underlyingStream);
    }

    /**
     * Benchmarks the flush() operation of FlushShieldFilterOutputStream.
     * Since flush() is a void method, we call it and rely on JMH timing.
     * We pass Blackhole to satisfy the rule, although consumption is impossible for a void return.
     */
    @Benchmark
    public void testFlushOperation(Blackhole bh) throws IOException {
        flushShieldStream.flush();
        // Consume null to satisfy the requirement for a void benchmark method taking Blackhole
        bh.consume(null);
    }
}

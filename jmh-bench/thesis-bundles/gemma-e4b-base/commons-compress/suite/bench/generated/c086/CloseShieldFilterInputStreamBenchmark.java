package bench.generated.c086;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CloseShieldFilterInputStreamBenchmark {

    private CloseShieldFilterInputStream subject;
    private InputStream underlyingStream;

    @Setup
    public void setup() {
        // Initialize the underlying stream (e.g., a small byte array stream)
        underlyingStream = new ByteArrayInputStream(new byte[1024]);
        // Initialize the subject under test
        subject = new CloseShieldFilterInputStream(underlyingStream);
    }

    /**
     * Benchmarks the close() method of CloseShieldFilterInputStream.
     * Since close() is void, we must consume the result via Blackhole.
     */
    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        subject.close();
        bh.consume(null);
    }
}

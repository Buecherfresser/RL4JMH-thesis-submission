package bench.generated.c086;

import org.apache.commons.compress.utils.CloseShieldFilterInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CloseShieldFilterInputStreamBenchmark {

    private InputStream input;
    private CloseShieldFilterInputStream filterInputStream;

    @Setup
    public void setup() throws IOException {
        // Prepare a fixed payload in memory
        byte[] payload = "This is a test payload for benchmarking CloseShieldFilterInputStream.".getBytes();
        this.input = new ByteArrayInputStream(payload);
    }

    @Benchmark
    public void benchmarkClose() throws IOException {
        // 1. Construction (to ensure the object is initialized)
        this.filterInputStream = new CloseShieldFilterInputStream(this.input);

        // 2. The operation being measured: calling close()
        try {
            this.filterInputStream.close();
        } catch (IOException e) {
            // Should not happen in this test setup
        }
    }
}

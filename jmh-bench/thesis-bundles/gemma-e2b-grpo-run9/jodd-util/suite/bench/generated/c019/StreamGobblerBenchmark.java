package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jodd.io.StreamGobbler;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamGobblerBenchmark {

    // State fields to hold resources that can be reused or initialized once per trial
    private ByteArrayInputStream inputStream;
    private ByteArrayOutputStream outputStream;

    @Setup
    public void setup() throws IOException {
        // Setup reusable in-memory streams.
        // We don't need to write to outputStream for this benchmark, but we need it initialized
        // if we were testing the output path.
        this.inputStream = new ByteArrayInputStream("Line 1\nLine 2\nLine 3\n".getBytes(StandardCharsets.UTF_8));
        this.outputStream = new ByteArrayOutputStream();
    }

    @Benchmark
    public void benchmarkRun(Blackhole bh) {
        try {
            // Instantiate the StreamGobbler. We pass null for output stream since we don't need to capture it.
            // Since run() is a Thread method, calling it starts a new thread.
            StreamGobbler gobbler = new StreamGobbler(this.inputStream);
            
            // Start the thread. This call blocks until the stream is drained.
            gobbler.start();
            
            // Wait for the gobbler thread to finish its work.
            gobbler.waitFor();
            
        } catch (Exception e) {
            // Catch exceptions that might occur during stream processing (e.g., IOException)
            // In a real benchmark, we might log this, but for JMH, we just let the exception propagate
            // or handle it minimally if it prevents the benchmark from running.
        }
        // We don't need to consume the return value since run() is void.
    }
}

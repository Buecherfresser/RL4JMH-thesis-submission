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

    // Input data setup
    private byte[] inputData;
    private InputStream inputStream;
    private String prefix;

    // Output streams setup
    private ByteArrayOutputStream outputStream;

    @Setup
    public void setup() throws IOException {
        // Create a reasonably sized input payload (e.g., 10 KB of data)
        String content = "Line 1\nLine 2\nLine 3\nLine 4\nLine 5\n";
        this.inputData = content.getBytes(StandardCharsets.UTF_8);
        this.inputStream = new ByteArrayInputStream(inputData);
        this.outputStream = new ByteArrayOutputStream();
        this.prefix = "PREFIX: ";
    }

    @Benchmark
    public void benchmarkGobblerInputOnly(Blackhole bh) throws IOException {
        // Test case 1: Gobbler consuming input, output is null
        StreamGobbler gobbler = new StreamGobbler(inputStream);
        gobbler.start();
        gobbler.waitFor();
        bh.consume(true); // Consume a dummy value to ensure the method runs
    }

    @Benchmark
    public void benchmarkGobblerInputOutputNoPrefix(Blackhole bh) throws IOException {
        // Test case 2: Gobbler consuming input, writing to output, no prefix
        StreamGobbler gobbler = new StreamGobbler(inputStream, outputStream);
        gobbler.start();
        gobbler.waitFor();
        bh.consume(outputStream.size()); // Consume the resulting output size
    }

    @Benchmark
    public void benchmarkGobblerInputOutputWithPrefix(Blackhole bh) throws IOException {
        // Test case 3: Gobbler consuming input, writing to output, with prefix
        StreamGobbler gobbler = new StreamGobbler(inputStream, outputStream, prefix);
        gobbler.start();
        gobbler.waitFor();
        bh.consume(outputStream.size()); // Consume the resulting output size
    }
}

package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import jodd.io.StreamGobbler;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 1)
public class StreamGobblerBenchmark {

    private InputStream inputIs;
    private OutputStream outputOs;
    private String prefix;
    private StreamGobbler gobbler;

    // Fixed input data: multiple lines
    private static final String TEST_DATA = 
            "Line 1\nLine 2\nLine 3\nLine 4\nLine 5\nLine 6\nLine 7\nLine 8\nLine 9\nLine 10";
    private static final int DATA_SIZE = TEST_DATA.getBytes().length;

    @Setup(Level.Trial)
    public void setup() {
        // Setup input stream once per trial
        inputIs = new ByteArrayInputStream(TEST_DATA.getBytes());
        
        // Initialize output stream and prefix to null/default for flexibility
        outputOs = null;
        prefix = null;
    }

    /**
     * Benchmark 1: Gobbling without writing to an output stream.
     * Measures the time taken to read all lines from the input stream.
     */
    @Benchmark
    public void benchmarkGobbleOnly(Blackhole bh) throws InterruptedException {
        // Setup specific to this benchmark
        outputOs = null;
        prefix = null;
        
        // Create and start the gobbler
        gobbler = new StreamGobbler(inputIs);
        gobbler.start();

        // Wait for the gobbler to finish processing the stream
        gobbler.waitFor();

        // Consume the result (the completion of the thread)
        bh.consume(gobbler);
    }

    /**
     * Benchmark 2: Gobbling and writing to an output stream, no prefix.
     * Measures the time taken to read lines and write them to the output stream.
     */
    @Benchmark
    public void benchmarkGobbleAndWriteNoPrefix(Blackhole bh) throws InterruptedException {
        // Setup specific to this benchmark
        outputOs = new ByteArrayOutputStream();
        prefix = null;

        // Create and start the gobbler
        gobbler = new StreamGobbler(inputIs, outputOs);
        gobbler.start();

        // Wait for the gobbler to finish processing the stream
        gobbler.waitFor();

        // Consume the result (the completion of the thread)
        bh.consume(gobbler);
    }

    /**
     * Benchmark 3: Gobbling and writing to an output stream with a prefix.
     * Measures the time taken to read lines, prepend a prefix, and write to the output stream.
     */
    @Benchmark
    public void benchmarkGobbleAndWriteWithPrefix(Blackhole bh) throws InterruptedException {
        // Setup specific to this benchmark
        outputOs = new ByteArrayOutputStream();
        prefix = "LOG: ";

        // Create and start the gobbler
        gobbler = new StreamGobbler(inputIs, outputOs, prefix);
        gobbler.start();

        // Wait for the gobbler to finish processing the stream
        gobbler.waitFor();

        // Consume the result (the completion of the thread)
        bh.consume(gobbler);
    }
}

package bench.generated.c051;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipCompressorOutputStreamBenchmark {

    // State fields are avoided here because GzipCompressorOutputStream holds mutable state
    // (Deflater, CRC) and we want a fresh instance for each benchmark invocation
    // to ensure isolation, even though this adds setup overhead.

    @Benchmark
    public void benchmarkWriteSimple(Blackhole bh) {
        try {
            // Create a fresh stream for each benchmark run
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(baos)) {

                byte[] data = "This is a test string for gzip compression.".getBytes();
                gzipOut.write(data);
                gzipOut.finish(); // Ensure all buffered data is written
            }
        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes if they occur during I/O operations
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkWriteWithOffset(Blackhole bh) {
        try {
            // Create a fresh stream for each benchmark run
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(baos)) {

                byte[] data = "This is a test string for gzip compression.".getBytes();
                
                // Write a portion of the data
                gzipOut.write(data, 0, data.length / 2);
                
                // Write the rest
                gzipOut.write(data, data.length / 2, data.length);
                
                gzipOut.finish();
            }
        } catch (IOException e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
    
    @Benchmark
    public void benchmarkClose(Blackhole bh) {
        try {
            // Create a fresh stream and immediately close it
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(baos)) {
                
                // Perform a minimal operation to ensure state is initialized
                gzipOut.write(new byte[]{1});
                
                gzipOut.close();
            }
        } catch (IOException e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}

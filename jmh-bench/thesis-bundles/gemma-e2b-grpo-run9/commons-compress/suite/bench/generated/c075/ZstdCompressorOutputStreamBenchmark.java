package bench.generated.c075;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.zstandard.ZstdCompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ZstdCompressorOutputStreamBenchmark {

    // No state needed as we create fresh streams inside the benchmark methods
    // to avoid state mutation issues, adhering to anti-pattern avoidance.

    /**
     * Benchmark for writing data to the ZstdCompressorOutputStream.
     * This tests the write path using a fresh stream instance for each invocation.
     */
    @Benchmark
    public void writeData(Blackhole bh) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Create a new compressor stream for this specific write test
            // We use the default constructor which wraps a ByteArrayOutputStream
            try (ZstdCompressorOutputStream stream = new ZstdCompressorOutputStream(baos)) {
                
                // Write a moderate amount of data (10 KB)
                byte[] data = new byte[1024 * 10]; 
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) (i % 256);
                }
                
                // Call the public write method
                stream.write(data, 0, data.length);
            }
            
            // Consume the result (the output buffer content) to prevent dead code elimination
            bh.consume(baos.toByteArray());
        }
    }

    /**
     * Benchmark for creating a ZstdCompressorOutputStream using the builder.
     * This tests the construction path.
     */
    @Benchmark
    public void buildCompressorStream(Blackhole bh) {
        try {
            // Call the static builder method
            ZstdCompressorOutputStream.builder()
                .setLevel(3)
                .setWorkers(1)
                .get();
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes
        }
        bh.consume(null);
    }
}

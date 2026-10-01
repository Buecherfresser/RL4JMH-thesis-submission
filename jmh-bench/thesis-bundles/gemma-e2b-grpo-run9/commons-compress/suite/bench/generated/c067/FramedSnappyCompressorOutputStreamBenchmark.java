package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FramedSnappyCompressorOutputStreamBenchmark {

    // We don't need @Setup if we instantiate the stream inside the benchmark
    // and rely on the fact that the internal state is reset per invocation,
    // which is safer for non-thread-safe classes.

    /**
     * Benchmark for writing a large chunk of data to the FramedSnappyCompressorOutputStream.
     * This tests the internal buffering and copying logic of the write method.
     */
    @Benchmark
    public void benchmarkWriteLargeData(Blackhole bh) {
        try (OutputStream baos = new ByteArrayOutputStream()) {
            // Instantiate the compressor stream. This triggers the signature write.
            FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos);

            // Data to write (must not be static final)
            byte[] data = new byte[1024 * 10]; // 10 KB of data
            for (int i = 0; i < data.length; i++) {
                data[i] = (byte) (i % 256);
            }

            // Call the method under test. We don't need to consume the return value
            // as the method is void.
            compressor.write(data, 0, data.length);

            // Force flush/compression to ensure the internal logic runs and state is cleared.
            compressor.close();

        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes if they occur during setup/teardown
        }
        // Blackhole consumes the result (void method)
        bh.consume(null);
    }

    /**
     * Benchmark for writing a single byte.
     * This tests the overhead of the single-byte write path.
     */
    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) {
        try (OutputStream baos = new ByteArrayOutputStream()) {
            FramedSnappyCompressorOutputStream compressor = new FramedSnappyCompressorOutputStream(baos);

            // Write a single byte
            compressor.write(0xAA);

            compressor.close();
        } catch (IOException e) {
            // Ignore
        }
        bh.consume(null);
    }
}

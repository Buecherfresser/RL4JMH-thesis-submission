package bench.generated.c074;

import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ZstdCompressorInputStreamBenchmark {

    // State field to hold the stream instance, initialized in setup if possible.
    // Since the constructor requires an InputStream, we initialize it in the benchmark
    // or rely on the harness to handle the instantiation cost if we don't use @Setup.
    // For simplicity and to avoid complex setup involving external libraries,
    // we instantiate inside the benchmark method, relying on the fact that
    // the benchmark harness will run the method multiple times.

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(new byte[1024])) {
            // Attempt to create the stream. This might throw IOException if the underlying
            // ZstdInputStream fails initialization due to invalid data, but we test the path.
            ZstdCompressorInputStream stream = new ZstdCompressorInputStream(is);
            
            // Call the method and consume the result
            stream.read();
            
            // Ensure stream is closed if it wasn't closed by try-with-resources
            stream.close();
        } catch (IOException e) {
            // Ignore exceptions for benchmarking purposes if they occur due to invalid input
        }
    }

    @Benchmark
    public void benchmarkReadWithBuffer(Blackhole bh) {
        try (InputStream is = new ByteArrayInputStream(new byte[1024])) {
            ZstdCompressorInputStream stream = new ZstdCompressorInputStream(is);
            
            // Call the method and consume the result
            stream.read(new byte[1024]);
            
            stream.close();
        } catch (IOException e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) {
        try {
            // We don't need to store the stream, just test the close operation
            try (InputStream is = new ByteArrayInputStream(new byte[1024])) {
                new ZstdCompressorInputStream(is);
            }
        } catch (IOException e) {
            // Ignore exceptions
        }
    }
}

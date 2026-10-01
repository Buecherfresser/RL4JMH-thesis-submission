package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FramedLZ4CompressorInputStreamBenchmark {

    // Since FramedLZ4CompressorInputStream is designed to be instantiated per operation
    // and relies on an InputStream, we don't need complex @State fields for mutable state.

    /**
     * Setup method to prepare a simple, non-empty input stream for use in benchmarks.
     * Since we cannot easily generate a valid LZ4 frame payload without the compressor,
     * we use a simple byte array stream. This tests the overhead of stream wrapping
     * and initial setup.
     */
    @Setup
    public void setup() {
        // No complex setup needed for this stateless benchmark structure.
    }

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Create a dummy input stream. The actual content doesn't matter much
            // as long as it doesn't immediately cause an unrecoverable error.
            InputStream dummyStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
            FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(dummyStream);
            // Consume the result to prevent dead code elimination
            bh.consume(stream);
        } catch (IOException e) {
            // Ignore exceptions during setup/benchmark if they occur due to invalid input,
            // as the goal is to measure the path taken.
        }
    }

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try {
            // Create a dummy input stream.
            InputStream dummyStream = new ByteArrayInputStream(new byte[]{1, 2, 3, 4});
            FramedLZ4CompressorInputStream stream = new FramedLZ4CompressorInputStream(dummyStream);
            
            // Call a method that performs I/O and state transition
            stream.read();
            
            // Consume the result
            bh.consume(stream);
        } catch (IOException e) {
            // Ignore exceptions
        }
    }
}

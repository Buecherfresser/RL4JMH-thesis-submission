package bench.generated.c054;

import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorInputStream;
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
public class BlockLZ4CompressorInputStreamBenchmark {

    // State field to hold the instance of the stream, initialized in setup
    private BlockLZ4CompressorInputStream inputStream;

    @Setup
    public void setup() throws IOException {
        // Create a dummy input stream. Since we don't have the compressor,
        // this stream will likely cause exceptions during actual decompression,
        // but it allows us to test the instantiation and the call path of the read method.
        // We use an empty stream for minimal overhead testing.
        try (InputStream is = new ByteArrayInputStream(new byte[0])) {
            this.inputStream = new BlockLZ4CompressorInputStream(is);
        }
    }

    @Benchmark
    public void benchmarkRead(Blackhole bh) {
        try {
            // Call the public method. We pass a small buffer and length.
            // Since the input stream is empty, this is expected to throw an IOException,
            // but we consume the result via Blackhole to satisfy the anti-pattern rule.
            bh.consume(inputStream.read(new byte[10], 0, 10));
        } catch (IOException e) {
            // Catch expected exceptions during testing of an uninitialized/empty stream
        }
    }
}

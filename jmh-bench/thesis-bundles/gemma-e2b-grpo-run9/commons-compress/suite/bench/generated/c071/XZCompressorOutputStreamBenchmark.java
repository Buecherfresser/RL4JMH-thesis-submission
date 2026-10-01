package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.tukaani.xz.LZMA2Options;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZCompressorOutputStreamBenchmark {

    private XZCompressorOutputStream compressorStream;

    @Setup
    public void setup() throws IOException {
        // Initialize the compressor stream. We use a ByteArrayOutputStream to capture the compressed data.
        // We use the default builder which uses default LZMA2Options.
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            this.compressorStream = new XZCompressorOutputStream(baos);
        }
    }

    @Benchmark
    public void writeData(Blackhole bh) {
        try {
            // Write a small chunk of data. This tests the write path.
            byte[] data = "This is some test data for XZ compression.".getBytes();
            compressorStream.write(data, 0, data.length);
        } catch (IOException e) {
            // Ignore for benchmarking purposes, but handle if necessary
        }
    }

    @Benchmark
    public void finish(Blackhole bh) {
        try {
            // Call finish to flush the encoder.
            compressorStream.finish();
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void writeLargeData(Blackhole bh) {
        try {
            // Write a larger chunk of data to test throughput under load.
            byte[] data = new byte[1024 * 10]; // 10 KB
            compressorStream.write(data, 0, data.length);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void writeAnotherChunk(Blackhole bh) {
        try {
            // Write another chunk to ensure the stream state is handled correctly.
            byte[] data = "Another chunk of data.".getBytes();
            compressorStream.write(data, 0, data.length);
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void writeAndFinish(Blackhole bh) {
        try {
            // Test a full write cycle including finishing.
            byte[] data = "Test cycle data.".getBytes();
            compressorStream.write(data, 0, data.length);
            compressorStream.finish();
        } catch (IOException e) {
            // Ignore
        }
    }
}

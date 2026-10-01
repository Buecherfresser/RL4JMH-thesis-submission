package bench.generated.c049;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.deflate64.Deflate64CompressorInputStream;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Deflate64CompressorInputStreamBenchmark {

    // Fixed payload representing compressed data. 1MB payload.
    private final byte[] compressedData = new byte[1024 * 1024];
    private InputStream inputStream;
    private Deflate64CompressorInputStream compressorInputStream;

    @Setup
    public void setup() throws IOException {
        this.inputStream = new ByteArrayInputStream(compressedData);
        
        // Initialize the subject under test.
        this.compressorInputStream = new Deflate64CompressorInputStream(this.inputStream);
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        // Test the basic read() method.
        int result = compressorInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadChunk(Blackhole bh) throws IOException {
        // Test the read(byte[], off, len) method for a medium chunk size (4KB).
        byte[] buffer = new byte[4096];
        int bytesRead = compressorInputStream.read(buffer, 0, buffer.length);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void benchmarkReadLargeChunk(Blackhole bh) throws IOException {
        // Test the read(byte[], off, len) method for a large chunk size (64KB).
        byte[] buffer = new byte[65536];
        int bytesRead = compressorInputStream.read(buffer, 0, buffer.length);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void benchmarkGetCompressedCount(Blackhole bh) {
        // Test the getCompressedCount() method.
        long count = compressorInputStream.getCompressedCount();
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        // Test the close() method.
        compressorInputStream.close();
        bh.consume(null); // close returns void
    }
}

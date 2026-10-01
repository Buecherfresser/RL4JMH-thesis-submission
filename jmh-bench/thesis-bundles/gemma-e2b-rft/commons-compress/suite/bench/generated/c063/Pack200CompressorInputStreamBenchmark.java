package bench.generated.c063;

import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Strategy;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Pack200CompressorInputStreamBenchmark {

    private InputStream compressedDataStream;
    private byte[] decompressedData;
    private Pack200CompressorInputStream pack200InputStream;
    private final int payloadSize = 1024 * 1024; // 1MB payload

    @Setup
    public void setup() throws IOException {
        // 1. Create a representative compressed payload (dummy data for testing stream mechanics)
        byte[] compressedPayload = new byte[payloadSize];
        // Fill with some data to simulate a compressed file
        for (int i = 0; i < payloadSize; i++) {
            compressedPayload[i] = (byte) (i % 256);
        }

        // 2. Prepare the input stream for benchmarking
        this.compressedDataStream = new ByteArrayInputStream(compressedPayload);

        // 3. Initialize the Pack200CompressorInputStream instance using the InputStream constructor
        // Fixed: Using the constructor that takes InputStream and Pack200Strategy to resolve ambiguity.
        this.pack200InputStream = new Pack200CompressorInputStream(this.compressedDataStream, Pack200Strategy.IN_MEMORY);

        // 4. Pre-calculate the expected decompressed size (assuming 1:1 for simplicity in this test)
        this.decompressedData = IOUtils.toByteArray(this.pack200InputStream);
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        // Test basic read() operation
        int result = pack200InputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadFullBuffer(Blackhole bh) throws IOException {
        // Test reading a large chunk
        byte[] buffer = new byte[1024];
        int bytesRead = pack200InputStream.read(buffer);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void benchmarkSkipOperation(Blackhole bh) throws IOException {
        // Test skip operation
        long skipCount = 1024 * 10; // Skip 10KB
        long skipped = pack200InputStream.skip(skipCount);
        bh.consume(skipped);
    }

    @Benchmark
    public void benchmarkReadSpecificRange(Blackhole bh) throws IOException {
        // Test read(byte[] b, int off, int count)
        byte[] buffer = new byte[100];
        int offset = 500;
        int count = 100;
        int bytesRead = pack200InputStream.read(buffer, offset, count);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        // Test close operation
        pack200InputStream.close();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkMarkSupported(Blackhole bh) throws IOException {
        // Test markSupported check
        boolean supported = pack200InputStream.markSupported();
        bh.consume(supported);
    }

    @Benchmark
    public void benchmarkDecompressionThroughput(Blackhole bh) throws IOException {
        // Test the overall decompression throughput by reading the entire stream
        byte[] result = IOUtils.toByteArray(pack200InputStream);
        bh.consume(result);
    }
}

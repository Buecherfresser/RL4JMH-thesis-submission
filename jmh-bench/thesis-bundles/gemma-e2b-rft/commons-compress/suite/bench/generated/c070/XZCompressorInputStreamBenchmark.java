package bench.generated.c070;

import org.apache.commons.io.IOUtils;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class XZCompressorInputStreamBenchmark {

    // Input data: A representative, pre-compressed byte array.
    private byte[] compressedData;
    private ByteArrayInputStream inputStream;
    private XZCompressorInputStream xzInputStream;

    @Setup
    public void setup() throws IOException {
        // --- Setup Input Data ---
        // Creating a dummy payload. For a real benchmark, this should be a large, valid XZ file.
        // We use a 1MB array to ensure the stream operations are exercised.
        this.compressedData = new byte[1024 * 1024];
        for (int i = 0; i < compressedData.length; i++) {
            compressedData[i] = (byte) (i % 256);
        }

        this.inputStream = new ByteArrayInputStream(compressedData);

        // --- Setup Subject ---
        // Initialize the XZCompressorInputStream using the setup input stream.
        this.xzInputStream = new XZCompressorInputStream(this.inputStream);
    }

    @Benchmark
    public void readSingleByte(Blackhole bh) throws IOException {
        int result = xzInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void readBlock(Blackhole bh) throws IOException {
        byte[] buffer = new byte[4096];
        int result = xzInputStream.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void skipData(Blackhole bh) throws IOException {
        long skipAmount = 1024 * 1024; // Skip 1MB
        long skipped = xzInputStream.skip(skipAmount);
        bh.consume(skipped);
    }

    @Benchmark
    public void getCompressedCount(Blackhole bh) {
        long count = xzInputStream.getCompressedCount();
        bh.consume(count);
    }

    @Benchmark
    public void builderInstantiation(Blackhole bh) throws IOException {
        // Test the builder pattern instantiation overhead
        XZCompressorInputStream stream = XZCompressorInputStream.builder().get();
        bh.consume(stream);
    }
}

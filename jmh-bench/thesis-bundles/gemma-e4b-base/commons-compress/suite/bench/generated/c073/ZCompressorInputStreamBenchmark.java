package bench.generated.c073;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZCompressorInputStreamBenchmark {

    private byte[] compressedPayload;
    private ByteArrayInputStream bais;
    private final byte[] readBuffer = new byte[4096];

    @Setup(Level.Trial)
    public void setup() {
        // NOTE: ZCompressorOutputStream is unavailable in the provided source.
        // We use a small, fixed byte array placeholder to ensure compilation and
        // allow the benchmark to run, simulating a compressed payload.
        // A real test would require the compressor dependency.
        compressedPayload = new byte[]{
            (byte) 0x1f, (byte) 0x9d, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        };
        bais = new ByteArrayInputStream(compressedPayload);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        // No resources to explicitly close at Trial level in this setup
    }

    /**
     * Benchmarks the static signature matching method.
     */
    @Benchmark
    public boolean testMatchesSignature(Blackhole bh) {
        // Use a small slice of the compressed payload as the signature
        byte[] signature = new byte[3];
        System.arraycopy(compressedPayload, 0, signature, 0, 3);
        
        boolean result = ZCompressorInputStream.matches(signature, 3);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks reading a chunk of data from the ZCompressorInputStream.
     * This simulates the primary decompression operation.
     */
    @Benchmark
    public void testReadChunk(Blackhole bh) throws IOException {
        // Reset stream position for consistent measurement
        bais.reset();
        
        // Initialize stream for measurement
        ZCompressorInputStream zcis = new ZCompressorInputStream(bais);

        int bytesRead = zcis.read(readBuffer, 0, readBuffer.length);
        zcis.close();
        bh.consume(bytesRead);
    }

    /**
     * Benchmarks reading a single byte from the ZCompressorInputStream.
     */
    @Benchmark
    public void testReadSingleByte(Blackhole bh) throws IOException {
        // Reset stream position for consistent measurement
        bais.reset();
        
        // Initialize stream for measurement
        ZCompressorInputStream zcis = new ZCompressorInputStream(bais);

        int byteRead = zcis.read();
        zcis.close();
        bh.consume(byteRead);
    }
}

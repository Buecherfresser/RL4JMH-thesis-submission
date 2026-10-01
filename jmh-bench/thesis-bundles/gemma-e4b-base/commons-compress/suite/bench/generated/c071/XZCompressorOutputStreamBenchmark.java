package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import org.tukaani.xz.LZMA2Options;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZCompressorOutputStreamBenchmark {

    private byte[] payload;
    private final int payloadSize = 4096; // 4KB payload

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Build the input payload once per trial
        payload = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            payload[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks the cost of writing a single chunk of data to the XZCompressorOutputStream.
     * The stream and output buffer are recreated for each invocation to ensure isolation.
     */
    @Benchmark
    public void writeSingleChunk(Blackhole bh) throws IOException {
        // Setup for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XZCompressorOutputStream compressor = XZCompressorOutputStream.builder()
                .setOutputStream(baos)
                .get();

        // Operation
        compressor.write(payload, 0, payloadSize);

        // Cleanup and consumption
        compressor.finish();
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks the cost of writing a chunk and then finishing the compression stream.
     * This simulates a complete, small compression operation.
     */
    @Benchmark
    public void fullCompressionCycle(Blackhole bh) throws IOException {
        // Setup for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XZCompressorOutputStream compressor = XZCompressorOutputStream.builder()
                .setOutputStream(baos)
                .get();

        // Operation 1: Write data
        compressor.write(payload, 0, payloadSize);

        // Operation 2: Finish compression
        compressor.finish();

        // Consumption
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks the cost of writing a smaller chunk of data.
     */
    @Benchmark
    public void writeSmallChunk(Blackhole bh) throws IOException {
        final int smallSize = 128;
        
        // Setup for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XZCompressorOutputStream compressor = XZCompressorOutputStream.builder()
                .setOutputStream(baos)
                .get();

        // Operation
        compressor.write(payload, 0, smallSize);

        // Cleanup and consumption
        compressor.finish();
        bh.consume(baos.toByteArray());
    }
}

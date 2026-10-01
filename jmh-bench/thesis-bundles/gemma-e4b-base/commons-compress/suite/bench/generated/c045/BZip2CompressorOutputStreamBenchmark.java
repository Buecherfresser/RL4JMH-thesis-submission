package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BZip2CompressorOutputStreamBenchmark {

    private byte[] inputData;
    private final int dataSize = 1024 * 10; // 10 KB input data

    @Setup(Level.Trial)
    public void setup() {
        // Create a representative input payload
        inputData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            // Fill with some semi-random data
            inputData[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks writing a large chunk of data to the BZip2CompressorOutputStream.
     */
    @Benchmark
    public byte[] writeLargeChunk(Blackhole bh) throws IOException {
        // Setup stream for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream compressor = new BZip2CompressorOutputStream(baos);

        try {
            // Core operation: write the entire input chunk
            compressor.write(inputData, 0, dataSize);
            
            // Finalize the stream
            compressor.finish();
            
            // Consume the result
            return baos.toByteArray();
        } finally {
            compressor.close();
        }
    }

    /**
     * Benchmarks writing a single byte repeatedly to the BZip2CompressorOutputStream.
     */
    @Benchmark
    public byte[] writeSingleByte(Blackhole bh) throws IOException {
        // Setup stream for this invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream compressor = new BZip2CompressorOutputStream(baos);

        try {
            // Core operation: write a single byte
            for (int i = 0; i < 100; i++) {
                compressor.write(0xAA);
            }
            
            // Finalize the stream
            compressor.finish();
            
            // Consume the result
            return baos.toByteArray();
        } finally {
            compressor.close();
        }
    }

    /**
     * Benchmarks the finalization step (finish()) after data has been written.
     * Note: This benchmark assumes the stream is already partially filled with data.
     */
    @Benchmark
    public byte[] finishStream(Blackhole bh) throws IOException {
        // Setup stream and write some data first
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream compressor = new BZip2CompressorOutputStream(baos);

        try {
            // Write a representative chunk of data
            compressor.write(inputData, 0, dataSize / 2);
            
            // Core operation: finish the compression block
            compressor.finish();
            
            // Consume the result
            return baos.toByteArray();
        } finally {
            compressor.close();
        }
    }

    /**
     * Benchmarks the closing step (close()) of the stream.
     */
    @Benchmark
    public byte[] closeStream(Blackhole bh) throws IOException {
        // Setup stream and write some data first
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream compressor = new BZip2CompressorOutputStream(baos);

        try {
            // Write a representative chunk of data
            compressor.write(inputData, 0, dataSize / 2);
            
            // Core operation: close the stream (which calls finish())
            compressor.close();
            
            // Consume the result
            return baos.toByteArray();
        } finally {
            // No further action needed as close() handles cleanup
        }
    }
}

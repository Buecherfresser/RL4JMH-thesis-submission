package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class DeflateCompressorInputStreamBenchmark {

    private byte[] compressedData;
    private ByteArrayInputStream inputStream;
    private DeflateCompressorInputStream deflateInputStream;

    // Constants for payload size
    private static final int PAYLOAD_SIZE = 1024 * 1024; // 1 MB compressed data

    @Setup
    public void setup() throws IOException {
        // 1. Generate a representative compressed payload
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Use standard Java zip utilities to create compressed data
        java.util.zip.DeflaterOutputStream dos = new java.util.zip.DeflaterOutputStream(baos);
        
        // Write some arbitrary data to compress
        byte[] rawData = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            rawData[i] = (byte) (i % 256);
        }
        dos.write(rawData);
        dos.finish();
        
        this.compressedData = baos.toByteArray();
        
        // 2. Prepare the input stream for the benchmark
        this.inputStream = new ByteArrayInputStream(compressedData);
        
        // 3. Initialize the DeflateCompressorInputStream
        this.deflateInputStream = new DeflateCompressorInputStream(this.inputStream);
    }

    @Benchmark
    public void readSingleByte(Blackhole bh) throws IOException {
        // Test reading a single byte
        int result = deflateInputStream.read();
        bh.consume(result);
    }

    @Benchmark
    public void readChunk(Blackhole bh) throws IOException {
        // Test reading a chunk of data
        byte[] buffer = new byte[4096];
        int result = deflateInputStream.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void readEntireStream(Blackhole bh) throws IOException {
        // Test reading the entire stream content
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int bytesRead;
        
        // We must manually loop here because the benchmark method must consume the result
        while ((bytesRead = deflateInputStream.read()) != -1) {
            output.write(bytesRead);
        }
        
        bh.consume(output.toByteArray());
    }
}

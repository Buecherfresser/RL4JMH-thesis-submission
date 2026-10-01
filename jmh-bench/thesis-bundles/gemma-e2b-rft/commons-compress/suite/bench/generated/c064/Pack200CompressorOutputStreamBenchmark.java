package bench.generated.c064;

import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Pack200CompressorOutputStreamBenchmark {

    private byte[] payload;
    private Map<String, String> properties;

    @Setup
    public void setup() throws IOException {
        // 1. Define a representative payload size (e.g., 1MB of random data)
        int payloadSize = 1024 * 1024; // 1 MB
        this.payload = new byte[payloadSize];
        for (int i = 0; i < payloadSize; i++) {
            payload[i] = (byte) (i % 256);
        }

        // 2. Setup for properties test
        this.properties = new HashMap<>();
        properties.put("mode", "fast");
        properties.put("level", "9");
    }

    @Benchmark
    public void compressBasic(Blackhole bh) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Initialize the compressor stream
        Pack200CompressorOutputStream compressorOutputStream = new Pack200CompressorOutputStream(outputStream);

        // Write the entire payload to the compressor stream
        for (byte b : payload) {
            compressorOutputStream.write(b);
        }
        
        // Finalize the compression
        compressorOutputStream.finish();
        
        // Consume the result
        byte[] compressedData = outputStream.toByteArray();
        bh.consume(compressedData);
    }

    @Benchmark
    public void compressWithProperties(Blackhole bh) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Initialize the compressor stream with properties
        Pack200CompressorOutputStream propsCompressor = new Pack200CompressorOutputStream(
            outputStream, 
            properties
        );

        // Write the entire payload
        for (byte b : payload) {
            propsCompressor.write(b);
        }
        
        // Finalize the compression
        propsCompressor.finish();
        
        // Consume the result
        byte[] compressedData = outputStream.toByteArray();
        bh.consume(compressedData);
    }
}

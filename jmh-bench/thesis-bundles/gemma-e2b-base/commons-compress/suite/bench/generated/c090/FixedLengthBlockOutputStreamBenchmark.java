package bench.generated.c090;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.FixedLengthBlockOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FixedLengthBlockOutputStreamBenchmark {

    private static final int BLOCK_SIZE = 4096;
    private static final int TOTAL_DATA_SIZE = 1024 * 1024 * 4; // 4 MB payload

    private ByteArrayOutputStream outputStream;
    private FixedLengthBlockOutputStream fixedLengthOutputStream;
    private byte[] inputData;

    @Setup
    public void setup() throws IOException {
        // 1. Prepare input data
        inputData = new byte[TOTAL_DATA_SIZE];
        // Fill input data with some pattern (e.g., sequential bytes)
        for (int i = 0; i < TOTAL_DATA_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Setup the output stream sink
        outputStream = new ByteArrayOutputStream();

        // 3. Initialize the SUT
        // We use the OutputStream constructor
        fixedLengthOutputStream = new FixedLengthBlockOutputStream(outputStream, BLOCK_SIZE);
    }

    @Benchmark
    public void writeBytesArray(Blackhole bh) throws IOException {
        // Write the entire input data array in one go
        fixedLengthOutputStream.write(inputData, 0, inputData.length);
        
        // Consume the result (though the result is in the stream, we consume the operation)
        bh.consume(outputStream.size());
    }

    @Benchmark
    public void writeByteBuffer(Blackhole bh) throws IOException {
        // Create a ByteBuffer from the input data for testing the ByteBuffer path
        ByteBuffer buffer = ByteBuffer.wrap(inputData);
        fixedLengthOutputStream.write(buffer);
        
        bh.consume(outputStream.size());
    }

    @Benchmark
    public void writeSingleByte(Blackhole bh) throws IOException {
        // Test writing a single byte repeatedly (simulating small writes)
        for (int i = 0; i < 1000; i++) {
            fixedLengthOutputStream.write((byte) (i % 256));
        }
        bh.consume(outputStream.size());
    }

    @Benchmark
    public void closeStream(Blackhole bh) throws IOException {
        // Test the close operation, which should trigger a final flush
        fixedLengthOutputStream.close();
        bh.consume(outputStream.size());
    }
}

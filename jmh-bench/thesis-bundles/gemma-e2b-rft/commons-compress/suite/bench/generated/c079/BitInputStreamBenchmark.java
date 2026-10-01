package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.BitInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class BitInputStreamBenchmark {

    private BitInputStream bitInputStream;
    private byte[] testData;
    private static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    private static final int DATA_SIZE = 4096; // 4KB payload

    @Setup
    public void setup() throws IOException {
        // 1. Create a fixed payload (test data)
        testData = new byte[DATA_SIZE];
        // Fill with some non-zero data to ensure bit operations are exercised
        for (int i = 0; i < DATA_SIZE; i++) {
            testData[i] = (byte) (i % 256);
        }

        // 2. Create the InputStream
        InputStream inputStream = new ByteArrayInputStream(testData);

        // 3. Initialize the BitInputStream
        bitInputStream = new BitInputStream(inputStream, LITTLE_ENDIAN);
    }

    @Benchmark
    public void readBit(Blackhole bh) throws IOException {
        // Test reading a single bit
        int bit = bitInputStream.readBit();
        bh.consume(bit);
    }

    @Benchmark
    public void readBits_Small(Blackhole bh) throws IOException {
        // Test reading a small number of bits (e.g., 8 bits)
        long result = bitInputStream.readBits(8);
        bh.consume(result);
    }

    @Benchmark
    public void readBits_Medium(Blackhole bh) throws IOException {
        // Test reading a medium number of bits (e.g., 64 bits, max cache size)
        long result = bitInputStream.readBits(64);
        bh.consume(result);
    }

    @Benchmark
    public void readBits_Large(Blackhole bh) throws IOException {
        // Test reading a large number of bits (e.g., 1024 bits)
        // This tests the cache management and processBitsGreater57 logic heavily.
        long result = bitInputStream.readBits(1024);
        bh.consume(result);
    }

    @Benchmark
    public void readBits_Boundary(Blackhole bh) throws IOException {
        // Test reading a number slightly larger than the cache size (e.g., 65 bits)
        long result = bitInputStream.readBits(65);
        bh.consume(result);
    }
}

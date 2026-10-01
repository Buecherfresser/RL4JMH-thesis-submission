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
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class BitInputStreamBenchmark {

    // --- State Fields ---
    private InputStream inputStream;
    private BitInputStream bitInputStream;
    private byte[] testData; // Changed from final to allow initialization in @Setup
    private final ByteOrder littleEndian = ByteOrder.LITTLE_ENDIAN;
    private final ByteOrder bigEndian = ByteOrder.BIG_ENDIAN;

    // Constants for input size
    private static final int DATA_SIZE = 1024 * 1024; // 1MB of data

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create a large, fixed payload of data
        testData = new byte[DATA_SIZE];
        // Fill with some non-zero data to ensure stream activity
        for (int i = 0; i < DATA_SIZE; i++) {
            testData[i] = (byte) (i % 256);
        }

        // 2. Wrap the data in a ByteArrayInputStream
        inputStream = new ByteArrayInputStream(testData);

        // 3. Initialize BitInputStream instances for different byte orders
        // We create the primary instance here.
        bitInputStream = new BitInputStream(inputStream, littleEndian);
    }

    @Benchmark
    public void readBits_SmallCount(Blackhole bh) throws IOException {
        // Test reading a small number of bits (e.g., 10)
        long result = bitInputStream.readBits(10);
        bh.consume(result);
    }

    @Benchmark
    public void readBits_MaxCache(Blackhole bh) throws IOException {
        // Test reading the maximum cache size (63 bits)
        long result = bitInputStream.readBits(63);
        bh.consume(result);
    }

    @Benchmark
    public void readBit(Blackhole bh) throws IOException {
        // Test reading a single bit
        int result = bitInputStream.readBit();
        bh.consume(result);
    }

    @Benchmark
    public void readBits_BigEndian(Blackhole bh) throws IOException {
        // Re-initialize BitInputStream for Big Endian test
        BitInputStream bigEndianStream = new BitInputStream(inputStream, bigEndian);
        long result = bigEndianStream.readBits(32);
        bh.consume(result);
    }

    @Benchmark
    public void readBits_BigEndian_MaxCache(Blackhole bh) throws IOException {
        // Re-initialize BitInputStream for Big Endian test
        BitInputStream bigEndianStream = new BitInputStream(inputStream, bigEndian);
        long result = bigEndianStream.readBits(63);
        bh.consume(result);
    }
}

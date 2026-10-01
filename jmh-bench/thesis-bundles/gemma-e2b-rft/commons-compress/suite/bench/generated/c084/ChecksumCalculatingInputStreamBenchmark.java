package bench.generated.c084;

import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class ChecksumCalculatingInputStreamBenchmark {

    // State fields for inputs and objects created in @Setup
    private Checksum crc32Checksum;
    private InputStream inputStream;
    private byte[] inputData;

    private static final int DATA_SIZE = 4 * 1024 * 1024; // 4 MB payload

    @Setup
    public void setup() {
        // 1. Create a large byte array payload
        this.inputData = new byte[DATA_SIZE];
        // Fill with some data to ensure meaningful checksum calculation
        for (int i = 0; i < DATA_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }

        // 2. Initialize the Checksum object (CRC32 implements Checksum)
        this.crc32Checksum = new CRC32();

        // 3. Create the InputStream from the byte array
        this.inputStream = new ByteArrayInputStream(inputData);
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        // Create the subject instance using the pre-setup state
        ChecksumCalculatingInputStream checksumInputStream = 
            new ChecksumCalculatingInputStream(this.crc32Checksum, this.inputStream);

        // Call the method under test and consume the result
        long checksumValue = checksumInputStream.getValue();
        bh.consume(checksumValue);
    }
}

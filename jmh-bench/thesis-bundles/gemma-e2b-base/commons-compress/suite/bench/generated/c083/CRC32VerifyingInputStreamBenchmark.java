package bench.generated.c083;

import org.apache.commons.compress.utils.CRC32VerifyingInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.CRC32;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CRC32VerifyingInputStreamBenchmark {

    // State fields to hold pre-built inputs
    private byte[] testData;
    private InputStream inputStream;
    private long expectedCrc32;
    private CRC32VerifyingInputStream verifyingInputStream;

    @Setup
    public void setup() {
        // 1. Create a representative payload (e.g., 1MB of repeating data)
        int dataSize = 1024 * 1024; // 1 MB
        testData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            testData[i] = (byte) (i % 256);
        }

        // 2. Calculate the expected CRC32 for this data
        CRC32 crc = new CRC32();
        crc.update(testData);
        expectedCrc32 = crc.getValue();

        // 3. Create the input stream
        inputStream = new ByteArrayInputStream(testData);

        // 4. Initialize the subject under test
        // Using the constructor: CRC32VerifyingInputStream(InputStream in, long size, long expectedCrc32)
        verifyingInputStream = new CRC32VerifyingInputStream(inputStream, dataSize, expectedCrc32);
    }

    /**
     * Benchmark method to test reading the entire stream and verifying the CRC.
     */
    @Benchmark
    public void readAndVerifyData(Blackhole bh) {
        // Read all data from the verifying stream into a byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;

        try {
            while ((bytesRead = verifyingInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] result = baos.toByteArray();
            bh.consume(result);
        } catch (Exception e) {
            bh.consume(e);
        }
    }
}

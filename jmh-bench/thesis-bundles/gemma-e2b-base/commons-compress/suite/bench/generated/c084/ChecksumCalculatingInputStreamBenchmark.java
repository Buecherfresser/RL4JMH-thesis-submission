package bench.generated.c084;

import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.Checksum;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 5, time = 2)
public class ChecksumCalculatingInputStreamBenchmark {

    private byte[] testData;
    private Checksum testChecksum;
    private InputStream inputStream;

    @Setup
    public void setup() {
        // 1. Prepare fixed payload
        String dataString = "This is a test string for checksum calculation.";
        this.testData = dataString.getBytes();

        // 2. Prepare a fixed checksum (e.g., using java.util.zip.Checksum)
        this.testChecksum = new java.util.zip.CRC32();
        this.testChecksum.update(testData);

        // 3. Prepare the input stream for benchmarking
        this.inputStream = new ByteArrayInputStream(testData);
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        // Create the subject instance using the setup stream and checksum
        ChecksumCalculatingInputStream checksumStream = new ChecksumCalculatingInputStream(testChecksum, inputStream);

        // Call the method under test and consume the result
        long result = checksumStream.getValue();
        bh.consume(result);
    }
}

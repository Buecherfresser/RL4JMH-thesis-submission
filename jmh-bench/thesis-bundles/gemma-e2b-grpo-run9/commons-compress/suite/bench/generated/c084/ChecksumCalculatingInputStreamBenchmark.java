package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ChecksumCalculatingInputStreamBenchmark {

    // State fields to hold objects that are expensive to create or mutable
    private ChecksumCalculatingInputStream inputStream;
    private byte[] testData;
    private CRC32 crc32;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Initialize a concrete Checksum implementation
        this.crc32 = new CRC32();

        // 2. Prepare fixed payload data (must not be final literals)
        // Using a mutable array initialized here is acceptable for Trial setup
        this.testData = new byte[1024 * 10]; // 10KB of data
        for (int i = 0; i < this.testData.length; i++) {
            this.testData[i] = (byte) (i % 256);
        }
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        try {
            // Create a fresh input stream for each benchmark run to simulate real usage
            InputStream rawStream = new ByteArrayInputStream(this.testData);
            
            // Instantiate the class under test. This is the operation we are measuring.
            // Note: The constructor requires a Checksum object.
            this.inputStream = new ChecksumCalculatingInputStream(this.crc32, rawStream);
            
            // Call the method we want to benchmark.
            // We consume the result via Blackhole to prevent dead code elimination.
            long checksumValue = this.inputStream.getValue();
            bh.consume(checksumValue);
            
        } catch (Exception e) {
            // Catch exceptions that might occur during stream operations if the SUT throws them
            // In a real scenario, this might need more robust handling or specific exception handling
        } finally {
            // Clean up the instance if necessary, though JMH handles state cleanup
            this.inputStream = null;
        }
    }
}

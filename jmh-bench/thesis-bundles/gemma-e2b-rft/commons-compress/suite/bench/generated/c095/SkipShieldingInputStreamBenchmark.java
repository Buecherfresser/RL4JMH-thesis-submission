package bench.generated.c095;

import org.apache.commons.compress.utils.SkipShieldingInputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class SkipShieldingInputStreamBenchmark {

    private InputStream inputStream;
    private SkipShieldingInputStream skipInputStream;

    // Define a large payload for the input stream setup
    private static final int PAYLOAD_SIZE = 65536; // 64 KB

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create the raw payload data
        byte[] payload = new byte[PAYLOAD_SIZE];
        // Fill payload with some data (e.g., sequential bytes)
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            payload[i] = (byte) (i % 256);
        }

        // 2. Wrap the payload in a ByteArrayInputStream
        this.inputStream = new ByteArrayInputStream(payload);

        // 3. Create the SkipShieldingInputStream wrapper
        this.skipInputStream = new SkipShieldingInputStream(this.inputStream);
    }

    @Benchmark
    public void benchmarkSmallSkip(Blackhole bh) throws IOException {
        // Test a small skip request (less than buffer size)
        long skipAmount = 1024;
        long result = skipInputStream.skip(skipAmount);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMediumSkip(Blackhole bh) throws IOException {
        // Test a skip request close to the buffer size
        long skipAmount = 8192;
        long result = skipInputStream.skip(skipAmount);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkLargeSkip(Blackhole bh) throws IOException {
        // Test a large skip request (multiple buffer sizes)
        long skipAmount = 16384;
        long result = skipInputStream.skip(skipAmount);
        bh.consume(result);
    }
}

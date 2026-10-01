package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CountingInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingInputStreamBenchmark {

    private byte[] payload;
    private static final int PAYLOAD_SIZE = 1024;

    @Setup(Level.Trial)
    public void setup() {
        // Build a fixed payload once per trial
        payload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < PAYLOAD_SIZE; i++) {
            payload[i] = (byte) (i % 256);
        }
    }

    /**
     * Benchmarks the single byte read operation (read()).
     * The CountingInputStream and ByteArrayInputStream are created per invocation
     * to ensure a clean state for each measurement.
     */
    @Benchmark
    public int benchmarkReadSingleByte(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);

        // Execute the operation
        int result = cis.read();

        // Consume the result
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks reading a full buffer (read(byte[] b)).
     * The CountingInputStream and ByteArrayInputStream are created per invocation.
     */
    @Benchmark
    public int benchmarkReadFullBuffer(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);
        byte[] buffer = new byte[PAYLOAD_SIZE];

        // Execute the operation
        int result = cis.read(buffer);

        // Consume the result
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks reading a partial buffer (read(byte[] b, int off, int len)).
     * The CountingInputStream and ByteArrayInputStream are created per invocation.
     */
    @Benchmark
    public int benchmarkReadPartialBuffer(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);
        byte[] buffer = new byte[PAYLOAD_SIZE];
        int lengthToRead = PAYLOAD_SIZE / 2;

        // Execute the operation
        int result = cis.read(buffer, 0, lengthToRead);

        // Consume the result
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks retrieving the current byte count (getBytesRead()).
     * The CountingInputStream is created per invocation to ensure the counter is initialized.
     */
    @Benchmark
    public long benchmarkGetBytesRead(Blackhole bh) throws IOException {
        // Setup per invocation: must read something to make the counter non-zero
        ByteArrayInputStream bais = new ByteArrayInputStream(payload);
        CountingInputStream cis = new CountingInputStream(bais);

        // Perform a small read operation to set the counter state
        cis.read(new byte[1]);

        // Execute the operation
        long result = cis.getBytesRead();

        // Consume the result
        bh.consume(result);
        return result;
    }
}

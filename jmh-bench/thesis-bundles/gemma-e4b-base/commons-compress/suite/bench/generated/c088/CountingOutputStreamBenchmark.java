package bench.generated.c088;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.CountingOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CountingOutputStreamBenchmark {

    private byte[] inputData;
    private ByteArrayOutputStream underlyingStream;
    private CountingOutputStream countingOutputStream;

    // Setup runs once per trial
    @Setup(Level.Trial)
    public void setupTrial() throws IOException {
        // Create a representative input payload
        inputData = new byte[1024];
        for (int i = 0; i < inputData.length; i++) {
            inputData[i] = (byte) (i % 256);
        }
    }

    // Setup runs before every single benchmark invocation
    // Since CountingOutputStream is a mutating subject, we must reset its state (i.e., recreate it)
    // for accurate measurement of a single write operation.
    @Setup(Level.Invocation)
    public void setupInvocation() throws IOException {
        // Recreate the underlying stream and the SUT for a clean slate
        underlyingStream = new ByteArrayOutputStream();
        countingOutputStream = new CountingOutputStream(underlyingStream);
    }

    /**
     * Benchmarks writing a large chunk of data (1024 bytes) using the bulk write method.
     * This tests the overhead of delegation and counter update for a large operation.
     */
    @Benchmark
    public void benchmarkWriteBulk(Blackhole bh) throws IOException {
        countingOutputStream.write(inputData, 0, inputData.length);
        // Consume the result (the stream itself is void, but we ensure the operation completes)
        bh.consume(countingOutputStream.getBytesWritten());
    }

    /**
     * Benchmarks writing a single byte using the single-byte write method.
     * This tests the overhead of delegation and counter update for the smallest unit.
     */
    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) throws IOException {
        byte singleByte = inputData[0];
        countingOutputStream.write(singleByte);
        // Consume the result
        bh.consume(countingOutputStream.getBytesWritten());
    }

    /**
     * Benchmarks reading the current byte count from the stream.
     * This tests the efficiency of the getter method.
     */
    @Benchmark
    public void benchmarkGetBytesWritten(Blackhole bh) {
        long count = countingOutputStream.getBytesWritten();
        bh.consume(count);
    }
}

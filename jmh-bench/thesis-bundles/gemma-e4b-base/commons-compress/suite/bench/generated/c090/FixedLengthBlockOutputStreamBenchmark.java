package bench.generated.c090;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.FixedLengthBlockOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FixedLengthBlockOutputStreamBenchmark {

    private static final int BLOCK_SIZE = 4096;
    private static final int INPUT_CHUNK_SIZE = 100;

    private byte[] inputData;
    private ByteBuffer inputBuffer;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Prepare fixed input data once per trial
        inputData = new byte[INPUT_CHUNK_SIZE];
        for (int i = 0; i < INPUT_CHUNK_SIZE; i++) {
            inputData[i] = (byte) (i % 256);
        }
        inputBuffer = ByteBuffer.wrap(inputData);
    }

    /**
     * Benchmarks writing a byte array chunk using write(byte[] b, int offset, int length).
     * A fresh stream is created for each invocation to ensure clean state.
     */
    @Benchmark
    public void benchmarkWriteByteArray(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream stream = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);

        // Action
        stream.write(inputData, 0, INPUT_CHUNK_SIZE);

        // Flush and close to ensure the operation completes and padding is handled
        stream.flushBlock();
        stream.close();

        // Consume result
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks writing a ByteBuffer chunk using write(ByteBuffer src).
     * A fresh stream is created for each invocation to ensure clean state.
     */
    @Benchmark
    public void benchmarkWriteByteBuffer(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream stream = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);

        // Action
        // Reset buffer position for fresh write
        inputBuffer.rewind();
        stream.write(inputBuffer);

        // Flush and close
        stream.flushBlock();
        stream.close();

        // Consume result
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks writing a single byte using write(int b).
     * A fresh stream is created for each invocation to ensure clean state.
     */
    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream stream = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);

        // Action
        stream.write(0xAA);

        // Flush and close
        stream.flushBlock();
        stream.close();

        // Consume result
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks explicitly flushing the current block using flushBlock().
     * This tests the padding logic and underlying write mechanism.
     * A fresh stream is created for each invocation.
     */
    @Benchmark
    public void benchmarkFlushBlock(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream stream = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);

        // Write some data to fill the buffer partially
        stream.write(inputData, 0, INPUT_CHUNK_SIZE);

        // Action
        stream.flushBlock();

        // Consume result (the output stream content)
        bh.consume(baos.toByteArray());
    }

    /**
     * Benchmarks closing the stream using close().
     * This tests the final flush and underlying stream closure.
     * A fresh stream is created for each invocation.
     */
    @Benchmark
    public void benchmarkCloseStream(Blackhole bh) throws IOException {
        // Setup per invocation
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream stream = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);

        // Write some data
        stream.write(inputData, 0, INPUT_CHUNK_SIZE);

        // Action
        stream.close();

        // Consume result
        bh.consume(baos.toByteArray());
    }
}

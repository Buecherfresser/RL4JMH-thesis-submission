package bench.generated.c090;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.FixedLengthBlockOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FixedLengthBlockOutputStreamBenchmark {

    private static final int BLOCK_SIZE = 512;
    private byte[] payload;
    private FixedLengthBlockOutputStream reusableOpenStream;

    @Setup(Level.Trial)
    public void setUp() {
        // payload size is a multiple of BLOCK_SIZE to avoid needing flushBlock()
        payload = new byte[BLOCK_SIZE * 4];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
        // reusable stream for isOpen() benchmark
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        reusableOpenStream = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);
    }

    @Benchmark
    public int writeByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream out = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);
        out.write(payload, 0, payload.length);
        // No explicit close – payload length is a multiple of block size, so all data is already written
        return baos.size();
    }

    @Benchmark
    public int writeSingleByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream out = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);
        out.write(payload[0] & 0xFF);
        // Buffer not full, so nothing is written yet
        return baos.size();
    }

    @Benchmark
    public int writeByteBuffer() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream out = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        out.write(buffer);
        // Buffer is exactly filled by the payload, so data is written
        return baos.size();
    }

    @Benchmark
    public int isOpen() {
        return reusableOpenStream.isOpen() ? 1 : 0;
    }

    @Benchmark
    public int writePartialByteArray() throws IOException {
        // Write less than a full block to keep data in the internal buffer
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FixedLengthBlockOutputStream out = new FixedLengthBlockOutputStream(baos, BLOCK_SIZE);
        int half = BLOCK_SIZE / 2;
        out.write(payload, 0, half);
        // No data flushed yet
        return baos.size();
    }
}

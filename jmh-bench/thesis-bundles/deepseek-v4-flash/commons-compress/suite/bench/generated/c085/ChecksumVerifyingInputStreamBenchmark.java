package bench.generated.c085;

import org.openjdk.jmh.annotations.*;
import org.apache.commons.compress.utils.ChecksumVerifyingInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ChecksumVerifyingInputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        byte[] payload;
        byte[] smallPayload;
        long expectedChecksum;
        long smallExpectedChecksum;
        byte[] buffer;

        @Setup(Level.Trial)
        public void setup() {
            payload = new byte[1024];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (i * 31);
            }
            smallPayload = new byte[] { 42 };

            expectedChecksum = computeChecksum(payload);
            smallExpectedChecksum = computeChecksum(smallPayload);

            buffer = new byte[payload.length];
        }

        private long computeChecksum(byte[] data) {
            Checksum checksum = new CRC32();
            checksum.update(data, 0, data.length);
            return checksum.getValue();
        }
    }

    @Benchmark
    public int readBulk(BenchmarkState state) throws IOException {
        ChecksumVerifyingInputStream in = new ChecksumVerifyingInputStream(
                new CRC32(),
                new ByteArrayInputStream(state.payload),
                state.payload.length,
                state.expectedChecksum);
        return in.read(state.buffer, 0, state.buffer.length);
    }

    @Benchmark
    public int readSingleByte(BenchmarkState state) throws IOException {
        ChecksumVerifyingInputStream in = new ChecksumVerifyingInputStream(
                new CRC32(),
                new ByteArrayInputStream(state.smallPayload),
                state.smallPayload.length,
                state.smallExpectedChecksum);
        return in.read();
    }
}

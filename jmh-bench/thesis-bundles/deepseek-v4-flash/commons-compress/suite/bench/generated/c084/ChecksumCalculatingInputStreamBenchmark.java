package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ChecksumCalculatingInputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class DataState {
        byte[] payload;

        @Setup(Level.Trial)
        public void setup() {
            // 4 KiB of pseudo-random data
            payload = new byte[4096];
            java.util.Random rnd = new java.util.Random(12345L);
            rnd.nextBytes(payload);
        }
    }

    @Benchmark
    public long readAllBytes(DataState state) throws Exception {
        Checksum checksum = new CRC32();
        InputStream in = new ByteArrayInputStream(state.payload);
        ChecksumCalculatingInputStream cis = new ChecksumCalculatingInputStream(checksum, in);
        byte[] buffer = new byte[8192];
        int n;
        while ((n = cis.read(buffer, 0, buffer.length)) != -1) {
            // read all
        }
        return cis.getValue();
    }

    @Benchmark
    public long readByteByByte(DataState state) throws Exception {
        Checksum checksum = new CRC32();
        InputStream in = new ByteArrayInputStream(state.payload);
        ChecksumCalculatingInputStream cis = new ChecksumCalculatingInputStream(checksum, in);
        int b;
        while ((b = cis.read()) != -1) {
            // read all
        }
        return cis.getValue();
    }

    @Benchmark
    public void readAllBytesConsume(DataState state, Blackhole bh) throws Exception {
        Checksum checksum = new CRC32();
        InputStream in = new ByteArrayInputStream(state.payload);
        ChecksumCalculatingInputStream cis = new ChecksumCalculatingInputStream(checksum, in);
        byte[] buffer = new byte[8192];
        int n;
        while ((n = cis.read(buffer, 0, buffer.length)) != -1) {
            // read all
        }
        bh.consume(cis.getValue());
    }
}

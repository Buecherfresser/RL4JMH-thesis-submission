package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BZip2CompressorOutputStreamBenchmark {

    private byte[] payload;

    @Setup(Level.Trial)
    public void setUp() {
        int size = 64 * 1024; // 64 KiB payload
        payload = new byte[size];
        for (int i = 0; i < size; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
    }

    @Benchmark
    public byte[] compressDefaultBlockSize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream bzip = new BZip2CompressorOutputStream(baos);
        bzip.write(payload, 0, payload.length);
        bzip.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressBlockSize5() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream bzip = new BZip2CompressorOutputStream(baos, 5);
        bzip.write(payload, 0, payload.length);
        bzip.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressBlockSize9() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream bzip = new BZip2CompressorOutputStream(baos, 9);
        bzip.write(payload, 0, payload.length);
        bzip.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeSingleByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream bzip = new BZip2CompressorOutputStream(baos);
        bzip.write(payload[0] & 0xFF);
        bzip.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressEmpty() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BZip2CompressorOutputStream bzip = new BZip2CompressorOutputStream(baos);
        bzip.close();
        return baos.toByteArray();
    }
}

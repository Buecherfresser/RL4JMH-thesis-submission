package bench.generated.c057;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.Parameters;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream.BlockSize;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FramedLZ4CompressorOutputStreamBenchmark {

    @State(Scope.Benchmark)
    public static class DefaultState {
        byte[] data;
        Parameters params = Parameters.DEFAULT;

        @Setup(Level.Trial)
        public void setUp() {
            int size = 64 * 1024; // 64 KiB payload
            data = new byte[size];
            new Random(0x1234).nextBytes(data);
        }
    }

    @State(Scope.Benchmark)
    public static class NoChecksumState {
        byte[] data;
        Parameters params = new Parameters(BlockSize.K64, false, false, false);

        @Setup(Level.Trial)
        public void setUp() {
            int size = 64 * 1024;
            data = new byte[size];
            new Random(0x5678).nextBytes(data);
        }
    }

    @State(Scope.Benchmark)
    public static class BlockChecksumState {
        byte[] data;
        Parameters params = new Parameters(BlockSize.K64, true, true, false);

        @Setup(Level.Trial)
        public void setUp() {
            int size = 64 * 1024;
            data = new byte[size];
            new Random(0x9abc).nextBytes(data);
        }
    }

    // ----- write(byte[],int,int) benchmarks -----

    @Benchmark
    public byte[] writeBytesDefault(DefaultState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.write(s.data, 0, s.data.length);
        out.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeBytesNoChecksum(NoChecksumState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.write(s.data, 0, s.data.length);
        out.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeBytesBlockChecksum(BlockChecksumState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.write(s.data, 0, s.data.length);
        out.close();
        return baos.toByteArray();
    }

    // ----- write(int) benchmarks (single byte) -----

    @Benchmark
    public byte[] writeSingleByteDefault(DefaultState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.write(s.data[0] & 0xFF);
        out.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeSingleByteNoChecksum(NoChecksumState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.write(s.data[0] & 0xFF);
        out.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] writeSingleByteBlockChecksum(BlockChecksumState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.write(s.data[0] & 0xFF);
        out.close();
        return baos.toByteArray();
    }

    // ----- finish() benchmarks (empty stream) -----

    @Benchmark
    public byte[] finishDefault(DefaultState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.finish();
        out.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] finishNoChecksum(NoChecksumState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.finish();
        out.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] finishBlockChecksum(BlockChecksumState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.finish();
        out.close();
        return baos.toByteArray();
    }

    // ----- close() benchmarks (empty stream) -----

    @Benchmark
    public byte[] closeDefault(DefaultState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] closeNoChecksum(NoChecksumState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.close();
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] closeBlockChecksum(BlockChecksumState s) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FramedLZ4CompressorOutputStream out = new FramedLZ4CompressorOutputStream(baos, s.params);
        out.close();
        return baos.toByteArray();
    }
}

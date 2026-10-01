package bench.generated.c055;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockLZ4CompressorOutputStreamBenchmark {

    private byte[] payload;

    @Setup
    public void setup() {
        int size = 32 * 1024; // 32 KiB payload
        payload = new byte[size];
        new Random(0xdeadbeef).nextBytes(payload);
    }

    @Benchmark
    public int benchmarkWriteByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BlockLZ4CompressorOutputStream out = new BlockLZ4CompressorOutputStream(baos);
        out.write(payload, 0, payload.length);
        out.finish();
        return baos.toByteArray().length;
    }

    @Benchmark
    public int benchmarkWriteSingleByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BlockLZ4CompressorOutputStream out = new BlockLZ4CompressorOutputStream(baos);
        out.write(payload[0] & 0xFF);
        out.finish();
        return baos.toByteArray().length;
    }

    @Benchmark
    public int benchmarkPrefill() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BlockLZ4CompressorOutputStream out = new BlockLZ4CompressorOutputStream(baos);
        out.prefill(payload, 0, payload.length);
        out.finish();
        return baos.toByteArray().length;
    }
}

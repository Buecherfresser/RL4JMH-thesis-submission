package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipCompressorOutputStreamBenchmark {

    private static final int PAYLOAD_SIZE = 64 * 1024; // 64 KiB
    private static byte[] payload;

    @Setup(Level.Trial)
    public void initPayload() {
        payload = new byte[PAYLOAD_SIZE];
        for (int i = 0; i < payload.length; i++) {
            payload[i] = (byte) (i & 0xFF);
        }
    }

    @Benchmark
    public GzipCompressorOutputStream benchmarkConstructorDefault() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        return new GzipCompressorOutputStream(baos);
    }

    @Benchmark
    public GzipCompressorOutputStream benchmarkConstructorCustom() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GzipParameters params = new GzipParameters();
        params.setCompressionLevel(Deflater.BEST_COMPRESSION);
        params.setFileName("test.txt");
        params.setComment("benchmark");
        return new GzipCompressorOutputStream(baos, params);
    }

    @State(Scope.Thread)
    public static class DefaultStreamState {
        ByteArrayOutputStream baos;
        GzipCompressorOutputStream stream;

        @Setup(Level.Invocation)
        public void setUp() throws IOException {
            baos = new ByteArrayOutputStream();
            stream = new GzipCompressorOutputStream(baos);
        }
    }

    @State(Scope.Thread)
    public static class PrewrittenStreamState {
        ByteArrayOutputStream baos;
        GzipCompressorOutputStream stream;

        @Setup(Level.Invocation)
        public void setUp() throws IOException {
            baos = new ByteArrayOutputStream();
            stream = new GzipCompressorOutputStream(baos);
            stream.write(payload);
        }
    }

    @Benchmark
    public void benchmarkWriteByteArray(DefaultStreamState state, Blackhole bh) throws IOException {
        state.stream.write(payload);
        bh.consume(state.stream);
    }

    @Benchmark
    public void benchmarkWriteByteArrayOffset(DefaultStreamState state, Blackhole bh) throws IOException {
        state.stream.write(payload, 0, payload.length);
        bh.consume(state.stream);
    }

    @Benchmark
    public void benchmarkWriteInt(DefaultStreamState state, Blackhole bh) throws IOException {
        state.stream.write(0x5A);
        bh.consume(state.stream);
    }

    @Benchmark
    public void benchmarkFinish(PrewrittenStreamState state, Blackhole bh) throws IOException {
        state.stream.finish();
        bh.consume(state.stream);
    }

    @Benchmark
    public void benchmarkClose(PrewrittenStreamState state, Blackhole bh) throws IOException {
        state.stream.close();
        bh.consume(state.stream);
    }
}

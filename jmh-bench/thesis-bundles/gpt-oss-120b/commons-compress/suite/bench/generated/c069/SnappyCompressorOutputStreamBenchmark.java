package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.snappy.SnappyCompressorOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SnappyCompressorOutputStreamBenchmark {

    @Param({"256", "1024", "4096"})
    public int payloadSize;

    private byte[] payload;

    @Setup(Level.Trial)
    public void setUpPayload() {
        payload = new byte[payloadSize];
        Random rnd = new Random(12345);
        rnd.nextBytes(payload);
    }

    @Benchmark
    public void benchmarkWriteByteArray(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SnappyCompressorOutputStream out = new SnappyCompressorOutputStream(baos, payload.length);
        out.write(payload, 0, payload.length);
        bh.consume(out);
    }

    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SnappyCompressorOutputStream out = new SnappyCompressorOutputStream(baos, 1);
        out.write(payload[0] & 0xFF);
        bh.consume(out);
    }

    // State for finish() benchmark
    private ByteArrayOutputStream finishBaos;
    private SnappyCompressorOutputStream finishStream;
    private byte[] finishData;

    @Setup(Level.Invocation)
    public void setUpFinish() throws IOException {
        finishData = new byte[1024];
        new Random(123).nextBytes(finishData);
        finishBaos = new ByteArrayOutputStream();
        finishStream = new SnappyCompressorOutputStream(finishBaos, finishData.length);
        finishStream.write(finishData, 0, finishData.length);
    }

    @Benchmark
    public int benchmarkFinish() throws IOException {
        finishStream.finish();
        return finishBaos.size();
    }

    // State for close() benchmark
    private ByteArrayOutputStream closeBaos;
    private SnappyCompressorOutputStream closeStream;
    private byte[] closeData;

    @Setup(Level.Invocation)
    public void setUpClose() throws IOException {
        closeData = new byte[1024];
        new Random(456).nextBytes(closeData);
        closeBaos = new ByteArrayOutputStream();
        closeStream = new SnappyCompressorOutputStream(closeBaos, closeData.length);
        closeStream.write(closeData, 0, closeData.length);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        closeStream.close();
        bh.consume(closeStream);
    }
}

package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.zip.Deflater;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateParameters;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DeflateCompressorOutputStreamBenchmark {

    private byte[] payload;
    private byte[] smallPayload;
    private DeflateParameters defaultParams;
    private DeflateParameters bestCompressionParams;
    private DeflateParameters fastestParams;
    private DeflateParameters noZlibParams;

    @Setup(Level.Trial)
    public void setup() {
        payload = new byte[10240];
        smallPayload = new byte[1024];
        Random random = new Random(12345);
        random.nextBytes(payload);
        random.nextBytes(smallPayload);

        defaultParams = new DeflateParameters();
        // default: compression level -1 (default), with zlib header

        bestCompressionParams = new DeflateParameters();
        bestCompressionParams.setCompressionLevel(Deflater.BEST_COMPRESSION);

        fastestParams = new DeflateParameters();
        fastestParams.setCompressionLevel(Deflater.BEST_SPEED);

        noZlibParams = new DeflateParameters();
        noZlibParams.setWithZlibHeader(false);
    }

    @Benchmark
    public byte[] writeDefault() throws IOException {
        return compress(payload, defaultParams);
    }

    @Benchmark
    public byte[] writeBestCompression() throws IOException {
        return compress(payload, bestCompressionParams);
    }

    @Benchmark
    public byte[] writeFastest() throws IOException {
        return compress(payload, fastestParams);
    }

    @Benchmark
    public byte[] writeRawDeflate() throws IOException {
        return compress(payload, noZlibParams);
    }

    @Benchmark
    public byte[] writeAndFlush() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos)) {
            out.write(smallPayload, 0, smallPayload.length);
            out.flush();
        }
        return baos.toByteArray();
    }

    private byte[] compress(byte[] data, DeflateParameters params) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflateCompressorOutputStream out = new DeflateCompressorOutputStream(baos, params)) {
            out.write(data, 0, data.length);
        }
        return baos.toByteArray();
    }
}

package bench.generated.c061;

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
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.lzma.LZMACompressorOutputStream;
import org.apache.commons.compress.compressors.lzma.LZMACompressorInputStream;
import org.tukaani.xz.LZMA2Options;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMACompressorOutputStreamBenchmark {

    private byte[] rawData;
    private byte[] compressedData;

    @Setup
    public void setUp() throws IOException {
        // Prepare a deterministic payload (~64 KiB)
        rawData = new byte[64 * 1024];
        for (int i = 0; i < rawData.length; i++) {
            rawData[i] = (byte) (i & 0xFF);
        }

        // Pre‑compress the payload for decompression benchmarks
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZMACompressorOutputStream lzmaOut = new LZMACompressorOutputStream(baos)) {
            lzmaOut.write(rawData);
            lzmaOut.finish();
        }
        compressedData = baos.toByteArray();
    }

    @Benchmark
    public byte[] compressRawData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LZMACompressorOutputStream lzmaOut = new LZMACompressorOutputStream(baos)) {
            lzmaOut.write(rawData);
            lzmaOut.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressRawDataWithCustomOptions() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LZMACompressorOutputStream.Builder builder = LZMACompressorOutputStream.builder()
                .setOutputStream(baos);
        // Example of customizing options (dictionary size 8 MiB)
        LZMA2Options options = new LZMA2Options();
        options.setDictSize(8 * 1024 * 1024);
        builder.setLzma2Options(options);
        try (LZMACompressorOutputStream lzmaOut = builder.get()) {
            lzmaOut.write(rawData);
            lzmaOut.finish();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] decompressCompressedData() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        try (LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(bais)) {
            byte[] out = new byte[rawData.length];
            lzmaIn.read(out);
            return out;
        }
    }

    @Benchmark
    public byte[] decompressCompressedDataWithBlackhole(Blackhole bh) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        try (LZMACompressorInputStream lzmaIn = new LZMACompressorInputStream(bais)) {
            byte[] out = new byte[rawData.length];
            int read = lzmaIn.read(out);
            bh.consume(read);
            bh.consume(out);
            return out;
        }
    }
}

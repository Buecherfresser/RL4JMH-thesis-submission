package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamProvider;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.FramedLZ4CompressorOutputStream;
import org.apache.commons.compress.compressors.lz4.BlockLZ4CompressorOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CompressorStreamFactoryBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        CompressorStreamFactory factory = new CompressorStreamFactory();
        byte[] payload;
        byte[] gzipData;
        byte[] bzip2Data;
        byte[] deflateData;
        byte[] snappyFramedData;
        byte[] lz4FramedData;
        byte[] lz4BlockData;
        Set<String> gzipOnlySet;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            // Fixed payload of 1KB with a deterministic pattern
            payload = new byte[1024];
            for (int i = 0; i < payload.length; i++) {
                payload[i] = (byte) (i % 251);
            }

            gzipData = compress(payload, CompressorStreamFactory.GZIP);
            bzip2Data = compress(payload, CompressorStreamFactory.BZIP2);
            deflateData = compress(payload, CompressorStreamFactory.DEFLATE);
            snappyFramedData = compress(payload, CompressorStreamFactory.SNAPPY_FRAMED);
            lz4FramedData = compress(payload, CompressorStreamFactory.LZ4_FRAMED);
            lz4BlockData = compress(payload, CompressorStreamFactory.LZ4_BLOCK);

            gzipOnlySet = new HashSet<>();
            gzipOnlySet.add(CompressorStreamFactory.GZIP);
        }

        private byte[] compress(byte[] data, String format) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (CompressorOutputStream<?> cos = factory.createCompressorOutputStream(format, baos)) {
                cos.write(data);
            }
            return baos.toByteArray();
        }
    }

    // --- Input stream creation by name ---

    @Benchmark
    public CompressorInputStream createGzipInputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorInputStream(CompressorStreamFactory.GZIP,
                new ByteArrayInputStream(state.gzipData));
    }

    @Benchmark
    public CompressorInputStream createBzip2InputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorInputStream(CompressorStreamFactory.BZIP2,
                new ByteArrayInputStream(state.bzip2Data));
    }

    @Benchmark
    public CompressorInputStream createDeflateInputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorInputStream(CompressorStreamFactory.DEFLATE,
                new ByteArrayInputStream(state.deflateData));
    }

    @Benchmark
    public CompressorInputStream createSnappyFramedInputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorInputStream(CompressorStreamFactory.SNAPPY_FRAMED,
                new ByteArrayInputStream(state.snappyFramedData));
    }

    @Benchmark
    public CompressorInputStream createLz4FramedInputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorInputStream(CompressorStreamFactory.LZ4_FRAMED,
                new ByteArrayInputStream(state.lz4FramedData));
    }

    @Benchmark
    public CompressorInputStream createLz4BlockInputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorInputStream(CompressorStreamFactory.LZ4_BLOCK,
                new ByteArrayInputStream(state.lz4BlockData));
    }

    // --- Auto-detection ---

    @Benchmark
    public CompressorInputStream createAutoDetectInputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorInputStream(new ByteArrayInputStream(state.gzipData));
    }

    @Benchmark
    public CompressorInputStream createAutoDetectInputStreamWithSet(BenchmarkState state) throws Exception {
        return state.factory.createCompressorInputStream(new ByteArrayInputStream(state.gzipData), state.gzipOnlySet);
    }

    // --- Output stream creation by name ---

    @Benchmark
    public CompressorOutputStream<?> createGzipOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorOutputStream(CompressorStreamFactory.GZIP, new ByteArrayOutputStream());
    }

    @Benchmark
    public CompressorOutputStream<?> createBzip2OutputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorOutputStream(CompressorStreamFactory.BZIP2, new ByteArrayOutputStream());
    }

    @Benchmark
    public CompressorOutputStream<?> createDeflateOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorOutputStream(CompressorStreamFactory.DEFLATE, new ByteArrayOutputStream());
    }

    @Benchmark
    public CompressorOutputStream<?> createSnappyFramedOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorOutputStream(CompressorStreamFactory.SNAPPY_FRAMED, new ByteArrayOutputStream());
    }

    @Benchmark
    public CompressorOutputStream<?> createLz4FramedOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorOutputStream(CompressorStreamFactory.LZ4_FRAMED, new ByteArrayOutputStream());
    }

    @Benchmark
    public CompressorOutputStream<?> createLz4BlockOutputStream(BenchmarkState state) throws Exception {
        return state.factory.createCompressorOutputStream(CompressorStreamFactory.LZ4_BLOCK, new ByteArrayOutputStream());
    }

    // --- Static detection ---

    @Benchmark
    public String detectGzip(BenchmarkState state) throws Exception {
        return CompressorStreamFactory.detect(new ByteArrayInputStream(state.gzipData));
    }

    // --- Provider maps ---

    @Benchmark
    public SortedMap<String, CompressorStreamProvider> findAvailableInputStreamProviders() {
        return CompressorStreamFactory.findAvailableCompressorInputStreamProviders();
    }

    @Benchmark
    public SortedMap<String, CompressorStreamProvider> findAvailableOutputStreamProviders() {
        return CompressorStreamFactory.findAvailableCompressorOutputStreamProviders();
    }

    @Benchmark
    public SortedMap<String, CompressorStreamProvider> getInputStreamProviders(BenchmarkState state) {
        return state.factory.getCompressorInputStreamProviders();
    }

    @Benchmark
    public SortedMap<String, CompressorStreamProvider> getOutputStreamProviders(BenchmarkState state) {
        return state.factory.getCompressorOutputStreamProviders();
    }

    // --- Miscellaneous public methods ---

    @Benchmark
    public Boolean getDecompressUntilEOF(BenchmarkState state) {
        return state.factory.getDecompressUntilEOF();
    }
}

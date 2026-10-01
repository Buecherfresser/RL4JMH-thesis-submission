package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamProvider;
import org.apache.commons.compress.utils.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.Set;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CompressorStreamFactoryBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        byte[] rawData;
        Map<String, byte[]> compressedMap = new HashMap<>();
        CompressorStreamFactory factory = new CompressorStreamFactory();
    }

    @Setup(Level.Trial)
    public void setUp(BenchmarkState state) throws Exception {
        // generate a modest payload
        byte[] data = new byte[1024];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i & 0xFF);
        }
        state.rawData = data;

        String[] names = {
                CompressorStreamFactory.GZIP,
                CompressorStreamFactory.BZIP2,
                CompressorStreamFactory.DEFLATE,
                CompressorStreamFactory.XZ,
                CompressorStreamFactory.LZMA,
                CompressorStreamFactory.LZ4_FRAMED,
                CompressorStreamFactory.ZSTANDARD
        };

        for (String name : names) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                CompressorOutputStream<?> cos = state.factory.createCompressorOutputStream(name, baos);
                cos.write(state.rawData);
                cos.close();
                state.compressedMap.put(name, baos.toByteArray());
            } catch (Exception e) {
                // format not available – skip
            }
        }
    }

    // -------------------------------------------------------------------------
    // Detection benchmarks
    // -------------------------------------------------------------------------

    @Benchmark
    public String detectGzip(BenchmarkState s) throws Exception {
        byte[] data = s.compressedMap.get(CompressorStreamFactory.GZIP);
        if (data == null) return "";
        return CompressorStreamFactory.detect(new BufferedInputStream(new ByteArrayInputStream(data)));
    }

    @Benchmark
    public String detectBzip2(BenchmarkState s) throws Exception {
        byte[] data = s.compressedMap.get(CompressorStreamFactory.BZIP2);
        if (data == null) return "";
        return CompressorStreamFactory.detect(new BufferedInputStream(new ByteArrayInputStream(data)));
    }

    @Benchmark
    public String detectDeflate(BenchmarkState s) throws Exception {
        byte[] data = s.compressedMap.get(CompressorStreamFactory.DEFLATE);
        if (data == null) return "";
        return CompressorStreamFactory.detect(new BufferedInputStream(new ByteArrayInputStream(data)));
    }

    @Benchmark
    public String detectXz(BenchmarkState s) throws Exception {
        byte[] data = s.compressedMap.get(CompressorStreamFactory.XZ);
        if (data == null) return "";
        return CompressorStreamFactory.detect(new BufferedInputStream(new ByteArrayInputStream(data)));
    }

    // -------------------------------------------------------------------------
    // Decompression (input stream) benchmarks
    // -------------------------------------------------------------------------

    @Benchmark
    public int decompressGzip(BenchmarkState s) throws Exception {
        byte[] data = s.compressedMap.get(CompressorStreamFactory.GZIP);
        if (data == null) return 0;
        InputStream in = new ByteArrayInputStream(data);
        CompressorInputStream cis = s.factory.createCompressorInputStream(CompressorStreamFactory.GZIP, in);
        byte[] out = IOUtils.toByteArray(cis);
        return out.length;
    }

    @Benchmark
    public int decompressBzip2(BenchmarkState s) throws Exception {
        byte[] data = s.compressedMap.get(CompressorStreamFactory.BZIP2);
        if (data == null) return 0;
        InputStream in = new ByteArrayInputStream(data);
        CompressorInputStream cis = s.factory.createCompressorInputStream(CompressorStreamFactory.BZIP2, in);
        byte[] out = IOUtils.toByteArray(cis);
        return out.length;
    }

    @Benchmark
    public int decompressDeflate(BenchmarkState s) throws Exception {
        byte[] data = s.compressedMap.get(CompressorStreamFactory.DEFLATE);
        if (data == null) return 0;
        InputStream in = new ByteArrayInputStream(data);
        CompressorInputStream cis = s.factory.createCompressorInputStream(CompressorStreamFactory.DEFLATE, in);
        byte[] out = IOUtils.toByteArray(cis);
        return out.length;
    }

    // -------------------------------------------------------------------------
    // Compression (output stream) benchmarks
    // -------------------------------------------------------------------------

    @Benchmark
    public int compressGzip(BenchmarkState s) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompressorOutputStream<?> cos = s.factory.createCompressorOutputStream(CompressorStreamFactory.GZIP, baos);
        cos.write(s.rawData);
        cos.close();
        return baos.size();
    }

    @Benchmark
    public int compressBzip2(BenchmarkState s) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompressorOutputStream<?> cos = s.factory.createCompressorOutputStream(CompressorStreamFactory.BZIP2, baos);
        cos.write(s.rawData);
        cos.close();
        return baos.size();
    }

    @Benchmark
    public int compressDeflate(BenchmarkState s) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompressorOutputStream<?> cos = s.factory.createCompressorOutputStream(CompressorStreamFactory.DEFLATE, baos);
        cos.write(s.rawData);
        cos.close();
        return baos.size();
    }

    // -------------------------------------------------------------------------
    // Factory introspection benchmarks
    // -------------------------------------------------------------------------

    @Benchmark
    public SortedMap<String, CompressorStreamProvider> findInputProviders() {
        return CompressorStreamFactory.findAvailableCompressorInputStreamProviders();
    }

    @Benchmark
    public SortedMap<String, CompressorStreamProvider> findOutputProviders() {
        return CompressorStreamFactory.findAvailableCompressorOutputStreamProviders();
    }

    @Benchmark
    public CompressorStreamFactory getSingleton() {
        return CompressorStreamFactory.getSingleton();
    }

    @Benchmark
    public Boolean getDecompressUntilEOF(BenchmarkState s) {
        return s.factory.getDecompressUntilEOF();
    }

    @Benchmark
    public Set<String> getInputNames(BenchmarkState s) {
        return s.factory.getInputStreamCompressorNames();
    }

    @Benchmark
    public Set<String> getOutputNames(BenchmarkState s) {
        return s.factory.getOutputStreamCompressorNames();
    }
}

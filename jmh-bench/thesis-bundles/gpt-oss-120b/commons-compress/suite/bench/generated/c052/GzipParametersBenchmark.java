package bench.generated.c052;

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
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import java.time.Instant;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipParametersBenchmark {

    private GzipParameters baseParams;
    private GzipParameters paramsA;
    private GzipParameters paramsB;

    private int bufferSizeSample;
    private String commentSample;
    private int compressionLevelSample;
    private int deflateStrategySample;
    private String fileNameSample;
    private Charset charsetSample;
    private boolean headerCrcSample;
    private Instant instantSample;
    private long modificationTimeSample;
    private int osCodeSample;
    private GzipParameters.OS osEnumSample;

    @Setup
    public void setup() {
        bufferSizeSample = 1024;
        commentSample = "Benchmark comment";
        compressionLevelSample = Deflater.BEST_SPEED;
        deflateStrategySample = Deflater.DEFAULT_STRATEGY;
        fileNameSample = "test.txt";
        charsetSample = StandardCharsets.UTF_8;
        headerCrcSample = true;
        instantSample = Instant.now();
        modificationTimeSample = instantSample.getEpochSecond();
        osCodeSample = 3; // UNIX
        osEnumSample = GzipParameters.OS.UNIX;

        baseParams = new GzipParameters();
        baseParams.setBufferSize(bufferSizeSample);
        baseParams.setComment(commentSample);
        baseParams.setCompressionLevel(compressionLevelSample);
        baseParams.setDeflateStrategy(deflateStrategySample);
        baseParams.setFileName(fileNameSample);
        baseParams.setFileNameCharset(charsetSample);
        baseParams.setHeaderCRC(headerCrcSample);
        baseParams.setModificationInstant(instantSample);
        baseParams.setOperatingSystem(osCodeSample);
        // trailer fields left at defaults

        paramsA = new GzipParameters();
        paramsA.setComment("A");
        paramsB = new GzipParameters();
        paramsB.setComment("A");
    }

    // Getter benchmarks
    @Benchmark
    public int benchmarkGetBufferSize() {
        return baseParams.getBufferSize();
    }

    @Benchmark
    public String benchmarkGetComment() {
        return baseParams.getComment();
    }

    @Benchmark
    public int benchmarkGetCompressionLevel() {
        return baseParams.getCompressionLevel();
    }

    @Benchmark
    public int benchmarkGetDeflateStrategy() {
        return baseParams.getDeflateStrategy();
    }

    @Benchmark
    public String benchmarkGetFileName() {
        return baseParams.getFileName();
    }

    @Benchmark
    public Charset benchmarkGetFileNameCharset() {
        return baseParams.getFileNameCharset();
    }

    @Benchmark
    public boolean benchmarkGetHeaderCRC() {
        return baseParams.getHeaderCRC();
    }

    @Benchmark
    public Instant benchmarkGetModificationInstant() {
        return baseParams.getModificationInstant();
    }

    @Benchmark
    public long benchmarkGetModificationTime() {
        return baseParams.getModificationTime();
    }

    @Benchmark
    public int benchmarkGetOperatingSystem() {
        return baseParams.getOperatingSystem();
    }

    @Benchmark
    public GzipParameters.OS benchmarkGetOS() {
        return baseParams.getOS();
    }

    @Benchmark
    public long benchmarkGetTrailerCrc() {
        return baseParams.getTrailerCrc();
    }

    @Benchmark
    public long benchmarkGetTrailerISize() {
        return baseParams.getTrailerISize();
    }

    @Benchmark
    public String benchmarkToString() {
        return baseParams.toString();
    }

    // Setter benchmarks (each creates a fresh instance)
    @Benchmark
    public int benchmarkSetBufferSize() {
        GzipParameters p = new GzipParameters();
        p.setBufferSize(bufferSizeSample);
        return p.getBufferSize();
    }

    @Benchmark
    public String benchmarkSetComment() {
        GzipParameters p = new GzipParameters();
        p.setComment(commentSample);
        return p.getComment();
    }

    @Benchmark
    public int benchmarkSetCompressionLevel() {
        GzipParameters p = new GzipParameters();
        p.setCompressionLevel(compressionLevelSample);
        return p.getCompressionLevel();
    }

    @Benchmark
    public int benchmarkSetDeflateStrategy() {
        GzipParameters p = new GzipParameters();
        p.setDeflateStrategy(deflateStrategySample);
        return p.getDeflateStrategy();
    }

    @Benchmark
    public String benchmarkSetFileName() {
        GzipParameters p = new GzipParameters();
        p.setFileName(fileNameSample);
        return p.getFileName();
    }

    @Benchmark
    public Charset benchmarkSetFileNameCharset() {
        GzipParameters p = new GzipParameters();
        p.setFileNameCharset(charsetSample);
        return p.getFileNameCharset();
    }

    @Benchmark
    public boolean benchmarkSetHeaderCRC() {
        GzipParameters p = new GzipParameters();
        p.setHeaderCRC(headerCrcSample);
        return p.getHeaderCRC();
    }

    @Benchmark
    public Instant benchmarkSetModificationInstant() {
        GzipParameters p = new GzipParameters();
        p.setModificationInstant(instantSample);
        return p.getModificationInstant();
    }

    @Benchmark
    public long benchmarkSetModificationTime() {
        GzipParameters p = new GzipParameters();
        p.setModificationTime(modificationTimeSample);
        return p.getModificationTime();
    }

    @Benchmark
    public int benchmarkSetOperatingSystemByInt() {
        GzipParameters p = new GzipParameters();
        p.setOperatingSystem(osCodeSample);
        return p.getOperatingSystem();
    }

    @Benchmark
    public GzipParameters.OS benchmarkSetOSByEnum() {
        GzipParameters p = new GzipParameters();
        p.setOS(osEnumSample);
        return p.getOS();
    }

    // Equality and hash code benchmarks
    @Benchmark
    public boolean benchmarkEquals() {
        return paramsA.equals(paramsB);
    }

    @Benchmark
    public int benchmarkHashCode() {
        return baseParams.hashCode();
    }

    // OS enum benchmarks
    @Benchmark
    public int benchmarkOSFrom() {
        GzipParameters.OS os = GzipParameters.OS.from(osCodeSample);
        return os.type();
    }

    @Benchmark
    public int benchmarkOSType() {
        return osEnumSample.type();
    }
}

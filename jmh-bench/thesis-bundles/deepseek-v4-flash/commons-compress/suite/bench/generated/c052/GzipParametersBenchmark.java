package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import java.nio.charset.Charset;
import java.time.Instant;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipParametersBenchmark {

    private GzipParameters params;
    private GzipParameters equalParams;
    private GzipParameters populated;

    private int bufferSizeValue;
    private int compressionLevelValue;
    private int deflateStrategyValue;
    private int osCode;
    private int unknownOsCode;
    private long modTimeSeconds;
    private boolean headerCrcValue;
    private String commentValue;
    private String fileNameValue;
    private Charset charsetValue;
    private Instant modInstantValue;
    private GzipParameters.OS osValue;

    @Setup(Level.Trial)
    public void setup() {
        params = new GzipParameters();
        equalParams = new GzipParameters();
        populated = new GzipParameters();

        bufferSizeValue = 1024;
        compressionLevelValue = 6;
        deflateStrategyValue = 2;
        osCode = 3;
        unknownOsCode = 99;
        modTimeSeconds = 1_700_000_000L;
        headerCrcValue = true;
        commentValue = "benchmark comment";
        fileNameValue = "test.txt";
        charsetValue = Charset.forName("ISO-8859-1");
        modInstantValue = Instant.ofEpochSecond(modTimeSeconds);
        osValue = GzipParameters.OS.UNIX;

        populated.setBufferSize(bufferSizeValue);
        populated.setComment(commentValue);
        populated.setCompressionLevel(compressionLevelValue);
        populated.setDeflateStrategy(deflateStrategyValue);
        populated.setFileName(fileNameValue);
        populated.setFileNameCharset(charsetValue);
        populated.setHeaderCRC(headerCrcValue);
        populated.setModificationInstant(modInstantValue);
        populated.setOS(osValue);

        equalParams.setBufferSize(bufferSizeValue);
        equalParams.setComment(commentValue);
        equalParams.setCompressionLevel(compressionLevelValue);
        equalParams.setDeflateStrategy(deflateStrategyValue);
        equalParams.setFileName(fileNameValue);
        equalParams.setFileNameCharset(charsetValue);
        equalParams.setHeaderCRC(headerCrcValue);
        equalParams.setModificationInstant(modInstantValue);
        equalParams.setOS(osValue);
    }

    @Benchmark
    public int getBufferSize() {
        return populated.getBufferSize();
    }

    @Benchmark
    public String getComment() {
        return populated.getComment();
    }

    @Benchmark
    public int getCompressionLevel() {
        return populated.getCompressionLevel();
    }

    @Benchmark
    public int getDeflateStrategy() {
        return populated.getDeflateStrategy();
    }

    @Benchmark
    public String getFilename() {
        return populated.getFilename();
    }

    @Benchmark
    public String getFileName() {
        return populated.getFileName();
    }

    @Benchmark
    public Charset getFileNameCharset() {
        return populated.getFileNameCharset();
    }

    @Benchmark
    public boolean getHeaderCRC() {
        return populated.getHeaderCRC();
    }

    @Benchmark
    public Instant getModificationInstant() {
        return populated.getModificationInstant();
    }

    @Benchmark
    public long getModificationTime() {
        return populated.getModificationTime();
    }

    @Benchmark
    public int getOperatingSystem() {
        return populated.getOperatingSystem();
    }

    @Benchmark
    public GzipParameters.OS getOS() {
        return populated.getOS();
    }

    @Benchmark
    public long getTrailerCrc() {
        return populated.getTrailerCrc();
    }

    @Benchmark
    public long getTrailerISize() {
        return populated.getTrailerISize();
    }

    @Benchmark
    public GzipParameters setBufferSize() {
        params.setBufferSize(bufferSizeValue);
        return params;
    }

    @Benchmark
    public GzipParameters setComment() {
        params.setComment(commentValue);
        return params;
    }

    @Benchmark
    public GzipParameters setCompressionLevel() {
        params.setCompressionLevel(compressionLevelValue);
        return params;
    }

    @Benchmark
    public GzipParameters setDeflateStrategy() {
        params.setDeflateStrategy(deflateStrategyValue);
        return params;
    }

    @Benchmark
    public GzipParameters setFilename() {
        params.setFilename(fileNameValue);
        return params;
    }

    @Benchmark
    public GzipParameters setFileName() {
        params.setFileName(fileNameValue);
        return params;
    }

    @Benchmark
    public GzipParameters setFileNameCharset() {
        params.setFileNameCharset(charsetValue);
        return params;
    }

    @Benchmark
    public GzipParameters setHeaderCRC() {
        params.setHeaderCRC(headerCrcValue);
        return params;
    }

    @Benchmark
    public GzipParameters setModificationInstant() {
        params.setModificationInstant(modInstantValue);
        return params;
    }

    @Benchmark
    public GzipParameters setModificationTime() {
        params.setModificationTime(modTimeSeconds);
        return params;
    }

    @Benchmark
    public GzipParameters setOperatingSystem() {
        params.setOperatingSystem(osCode);
        return params;
    }

    @Benchmark
    public GzipParameters setOS() {
        params.setOS(osValue);
        return params;
    }

    @Benchmark
    public boolean equalsBenchmark() {
        return params.equals(equalParams);
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return params.hashCode();
    }

    @Benchmark
    public String toStringBenchmark() {
        return params.toString();
    }

    @Benchmark
    public GzipParameters.OS osFromKnown() {
        return GzipParameters.OS.from(osCode);
    }

    @Benchmark
    public GzipParameters.OS osFromUnknown() {
        return GzipParameters.OS.from(unknownOsCode);
    }

    @Benchmark
    public int osType() {
        return osValue.type();
    }
}

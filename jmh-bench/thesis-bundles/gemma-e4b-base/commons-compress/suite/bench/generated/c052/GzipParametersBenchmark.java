package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.commons.compress.compressors.gzip.GzipParameters.OS;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.zip.Deflater;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class GzipParametersBenchmark {

    private GzipParameters params;
    private String validComment;
    private String validFileName;
    private Charset utf8Charset;
    private Instant testInstant;
    private int testOSCode;
    private OS testOSEnum;
    private int testCompressionLevel;
    private int testDeflateStrategy;
    private long testTrailerCrc;
    private long testTrailerISize;
    private org.apache.commons.compress.compressors.gzip.ExtraField testExtraField;

    @Setup(Level.Trial)
    public void setup() {
        params = new GzipParameters();
        
        // Inputs for testing
        validComment = "Test comment for Gzip";
        validFileName = "testfile.txt";
        utf8Charset = Charset.forName("UTF-8");
        testInstant = Instant.now();
        testOSCode = 3; // Unix
        testOSEnum = OS.UNIX;
        testCompressionLevel = Deflater.DEFAULT_COMPRESSION;
        testDeflateStrategy = Deflater.DEFAULT_STRATEGY;
        testTrailerCrc = 12345L;
        testTrailerISize = 67890L;
        // ExtraField is complex; setting it to null is safe for benchmarking parameter setting
        testExtraField = null; 
    }

    @Benchmark
    public void testGetBufferSize(Blackhole bh) {
        bh.consume(params.getBufferSize());
    }

    @Benchmark
    public void testSetAndGetBufferSize(Blackhole bh) {
        params.setBufferSize(1024);
        bh.consume(params.getBufferSize());
    }

    @Benchmark
    public void testGetComment(Blackhole bh) {
        params.setComment(validComment);
        bh.consume(params.getComment());
    }

    @Benchmark
    public void testSetAndGetComment(Blackhole bh) {
        params.setComment(validComment);
        bh.consume(params.getComment());
    }

    @Benchmark
    public void testGetFileName(Blackhole bh) {
        params.setFileName(validFileName);
        bh.consume(params.getFileName());
    }

    @Benchmark
    public void testSetAndGetFileName(Blackhole bh) {
        params.setFileName(validFileName);
        bh.consume(params.getFileName());
    }

    @Benchmark
    public void testGetFileNameCharset(Blackhole bh) {
        params.setFileNameCharset(utf8Charset);
        bh.consume(params.getFileNameCharset());
    }

    @Benchmark
    public void testSetAndGetFileNameCharset(Blackhole bh) {
        params.setFileNameCharset(utf8Charset);
        bh.consume(params.getFileNameCharset());
    }

    @Benchmark
    public void testGetCompressionLevel(Blackhole bh) {
        params.setCompressionLevel(testCompressionLevel);
        bh.consume(params.getCompressionLevel());
    }

    @Benchmark
    public void testSetAndGetCompressionLevel(Blackhole bh) {
        params.setCompressionLevel(testCompressionLevel);
        bh.consume(params.getCompressionLevel());
    }

    @Benchmark
    public void testGetDeflateStrategy(Blackhole bh) {
        params.setDeflateStrategy(testDeflateStrategy);
        bh.consume(params.getDeflateStrategy());
    }

    @Benchmark
    public void testSetAndGetDeflateStrategy(Blackhole bh) {
        params.setDeflateStrategy(testDeflateStrategy);
        bh.consume(params.getDeflateStrategy());
    }

    @Benchmark
    public void testSetExtraField(Blackhole bh) {
        params.setExtraField(testExtraField);
        bh.consume(params.getExtraField());
    }

    @Benchmark
    public void testGetExtraField(Blackhole bh) {
        bh.consume(params.getExtraField());
    }

    @Benchmark
    public void testSetHeaderCRC(Blackhole bh) {
        params.setHeaderCRC(true);
        bh.consume(params.getHeaderCRC());
    }

    @Benchmark
    public void testGetHeaderCRC(Blackhole bh) {
        bh.consume(params.getHeaderCRC());
    }

    @Benchmark
    public void testSetModificationInstant(Blackhole bh) {
        params.setModificationInstant(testInstant);
        bh.consume(params.getModificationInstant());
    }

    @Benchmark
    public void testGetModificationInstant(Blackhole bh) {
        bh.consume(params.getModificationInstant());
    }

    @Benchmark
    public void testSetModificationTime(Blackhole bh) {
        params.setModificationTime(1672531200L);
        bh.consume(params.getModificationTime());
    }

    @Benchmark
    public void testGetModificationTime(Blackhole bh) {
        bh.consume(params.getModificationTime());
    }

    @Benchmark
    public void testSetOperatingSystemInt(Blackhole bh) {
        params.setOperatingSystem(testOSCode);
        bh.consume(params.getOperatingSystem());
    }

    @Benchmark
    public void testGetOperatingSystemInt(Blackhole bh) {
        bh.consume(params.getOperatingSystem());
    }

    @Benchmark
    public void testSetOperatingSystemEnum(Blackhole bh) {
        params.setOS(testOSEnum);
        bh.consume(params.getOS());
    }

    @Benchmark
    public void testGetOperatingSystemEnum(Blackhole bh) {
        bh.consume(params.getOS());
    }

    @Benchmark
    public void testGetTrailerCrc(Blackhole bh) {
        bh.consume(params.getTrailerCrc());
    }

    @Benchmark
    public void testGetTrailerISize(Blackhole bh) {
        bh.consume(params.getTrailerISize());
    }
}

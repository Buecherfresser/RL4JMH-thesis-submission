package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.xz.XZUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZUtilsBenchmark {

    private String standardFileName;
    private String tarFileName;
    private String compressedFileName;
    private byte[] xzSignature;
    private int signatureLength;

    @Setup(Level.Trial)
    public void setup() {
        // Input for filename mapping (standard file)
        standardFileName = "my_document.txt";
        
        // Input for filename mapping (tar file, which maps to .txz)
        tarFileName = "package.tar";
        
        // Input for filename mapping (already compressed file)
        compressedFileName = "archive.xz";

        // Input for matches() method
        // XZ Header Magic Bytes: { (byte) 0xFD, '7', 'z', 'X', 'Z', '\0' }
        xzSignature = new byte[] { (byte) 0xFD, '7', 'z', 'X', 'Z', '\0' };
        signatureLength = 6;
    }

    @Benchmark
    public String testGetCompressedFilename_StandardFile(Blackhole bh) {
        String result = XZUtils.getCompressedFileName(standardFileName);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String testGetCompressedFilename_TarFile(Blackhole bh) {
        String result = XZUtils.getCompressedFileName(tarFileName);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String testGetUncompressedFilename_CompressedFile(Blackhole bh) {
        String result = XZUtils.getUncompressedFileName(compressedFileName);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testIsCompressedFileName_StandardFile(Blackhole bh) {
        boolean result = XZUtils.isCompressedFileName(standardFileName);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testIsCompressedFileName_TarFile(Blackhole bh) {
        boolean result = XZUtils.isCompressedFileName(tarFileName);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testMatches_ValidSignature(Blackhole bh) {
        boolean result = XZUtils.matches(xzSignature, signatureLength);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testMatches_InvalidSignature(Blackhole bh) {
        // Create a slightly modified signature
        byte[] invalidSignature = new byte[signatureLength];
        System.arraycopy(xzSignature, 0, invalidSignature, 0, signatureLength);
        invalidSignature[0] = (byte) 0x00; // Change the first byte
        
        boolean result = XZUtils.matches(invalidSignature, signatureLength);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean testIsXZCompressionAvailable(Blackhole bh) {
        boolean result = XZUtils.isXZCompressionAvailable();
        bh.consume(result);
        return result;
    }
    
    @Benchmark
    public void testSetCacheXZAvailablity(Blackhole bh) {
        // Testing the setter call itself
        XZUtils.setCacheXZAvailablity(true);
        bh.consume(true);
    }
}

package bench.generated.c072;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.xz.XZUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class XZUtilsBenchmark {

    // Since XZUtils is a static utility class, we don't need instance fields
    // unless we were benchmarking a non-static method that required state.
    // We will rely on static calls for simplicity, as per the provided API.

    /**
     * Helper method to check the magic bytes.
     * This is a static method, so we call it directly.
     * @param signature The bytes to check.
     * @param length The number of bytes to check.
     * @return true if signature matches the .xz magic bytes, false otherwise.
     */
    private boolean checkMatches(byte[] signature, int length) {
        return XZUtils.matches(signature, length);
    }

    @Benchmark
    public void testGetCompressedFileName(Blackhole bh) {
        // Test with a standard filename
        String fileName = "testfile.txt";
        String result = XZUtils.getCompressedFileName(fileName);
        bh.consume(result);
    }

    @Benchmark
    public void testGetUncompressedFileName(Blackhole bh) {
        // Test with a compressed filename
        String fileName = "package.txz";
        String result = XZUtils.getUncompressedFileName(fileName);
        bh.consume(result);
    }

    @Benchmark
    public void testIsCompressedFileName(Blackhole bh) {
        // Test a file that should be considered compressed
        boolean result = XZUtils.isCompressedFileName("archive.xz");
        bh.consume(result);
    }

    @Benchmark
    public void testIsCompressedFileNameFalse(Blackhole bh) {
        // Test a file that should not be considered compressed
        boolean result = XZUtils.isCompressedFileName("document.txt");
        bh.consume(result);
    }

    @Benchmark
    public void testIsXZCompressionAvailable(Blackhole bh) {
        // This method relies on internal checks, which might involve reflection or class loading checks.
        // We call it to ensure it runs without error and consumes the result.
        boolean result = XZUtils.isXZCompressionAvailable();
        bh.consume(result);
    }

    @Benchmark
    public void testSetCacheXZAvailability(Blackhole bh) {
        // Test setting the cache availability (requires careful handling if state were involved,
        // but here we just call the static method).
        XZUtils.setCacheXZAvailablity(true);
        // Consume the result (void method)
    }

    @Benchmark
    public void testMatchesMagicBytes(Blackhole bh) {
        // Test with correct magic bytes
        byte[] magic = { (byte) 0xFD, '7', 'z', 'X', 'Z', '\0' };
        boolean result = checkMatches(magic, magic.length);
        bh.consume(result);
    }

    @Benchmark
    public void testMatchesMagicBytesFailure(Blackhole bh) {
        // Test with incorrect magic bytes
        byte[] wrongMagic = { (byte) 0xFE, '7', 'z', 'X', 'Z', '\0' };
        boolean result = checkMatches(wrongMagic, wrongMagic.length);
        bh.consume(result);
    }
}

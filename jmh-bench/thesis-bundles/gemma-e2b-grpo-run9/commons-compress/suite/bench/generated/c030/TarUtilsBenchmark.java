package bench.generated.c030;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.tar.TarUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarUtilsBenchmark {

    // State fields for reuse. Since TarUtils methods are static, we don't need an instance,
    // but we need to ensure inputs are not static final literals.

    private byte[] testBuffer;

    @Setup
    public void setup() {
        // Initialize a buffer. This is a simple, non-mutating setup.
        // We use a small, fixed size buffer for simplicity in benchmarking static methods.
        this.testBuffer = new byte[1024];
    }

    @Benchmark
    public void computeCheckSum(Blackhole bh) {
        // Test computeCheckSum with a simple buffer.
        // Since the method is static and read-only, we don't need to worry about state mutation.
        long result = TarUtils.computeCheckSum(testBuffer);
        bh.consume(result);
    }

    @Benchmark
    public void formatCheckSumOctalBytes(Blackhole bh) {
        // Test formatCheckSumOctalBytes.
        // We must ensure the buffer is large enough for the operation.
        try {
            // Use a buffer size that is likely to succeed for a small value.
            TarUtils.formatCheckSumOctalBytes(100L, testBuffer, 0, testBuffer.length);
        } catch (IllegalArgumentException e) {
            // Ignore exceptions if the buffer is too small for the test case.
        }
    }

    @Benchmark
    public void formatLongOctalBytes(Blackhole bh) {
        // Test formatLongOctalBytes.
        try {
            // Test with a value that should fit.
            TarUtils.formatLongOctalBytes(1000L, testBuffer, 0, testBuffer.length);
        } catch (IllegalArgumentException e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void formatLongOctalOrBinaryBytes(Blackhole bh) {
        // Test formatLongOctalOrBinaryBytes.
        try {
            // Test with a value that should fit (positive, small).
            TarUtils.formatLongOctalOrBinaryBytes(100L, testBuffer, 0, testBuffer.length);
        } catch (IllegalArgumentException e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void parseOctalOrBinary(Blackhole bh) {
        // Test parseOctalOrBinary.
        try {
            // Test with a buffer that might contain a leading MSB set (to test binary path).
            // Since testBuffer is initialized to zeros, this might default to octal path.
            long result = TarUtils.parseOctalOrBinary(testBuffer, 0, testBuffer.length);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions during parsing if the buffer is invalid.
        }
    }

    @Benchmark
    public void parseName(Blackhole bh) {
        // Test parseName with default encoding (which might throw IOException if we don't handle it,
        // but since we are benchmarking static methods, we rely on the internal try-catch logic).
        try {
            // Use a small slice of the buffer.
            String name = "testname";
            TarUtils.parseName(testBuffer, 0, 10);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void parseOctal(Blackhole bh) {
        // Test parseOctal.
        try {
            // Test with a buffer containing octal digits and padding.
            // Since testBuffer is all zeros, this might return 0L or throw if length is too small.
            long result = TarUtils.parseOctal(testBuffer, 0, 10);
            bh.consume(result);
        } catch (IllegalArgumentException e) {
            // Ignore exceptions
        }
    }
}

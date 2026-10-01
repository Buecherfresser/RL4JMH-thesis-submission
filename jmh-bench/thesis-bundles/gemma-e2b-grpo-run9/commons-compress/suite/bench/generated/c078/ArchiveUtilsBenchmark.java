package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.utils.ArchiveUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArchiveUtilsBenchmark {

    // State fields for read-only or reusable data
    private byte[] testBuffer1;
    private byte[] testBuffer2;
    private byte[] zeroBuffer;
    private String testString;

    @Setup
    public void setup() {
        // Initialize reusable byte arrays
        this.testBuffer1 = new byte[1024];
        this.testBuffer2 = new byte[1024];
        this.zeroBuffer = new byte[1024];
        this.testString = "Test String for Benchmarking";
    }

    @Benchmark
    public void testIsArrayZero(Blackhole bh) {
        // Test case 1: All zeros
        boolean result1 = ArchiveUtils.isArrayZero(zeroBuffer, 100);
        bh.consume(result1);

        // Test case 2: Not all zeros (modify a small part)
        byte[] nonZeroBuffer = new byte[100];
        nonZeroBuffer[0] = 1;
        boolean result2 = ArchiveUtils.isArrayZero(nonZeroBuffer, 100);
        bh.consume(result2);
    }

    @Benchmark
    public void testIsEqual(Blackhole bh) {
        // Test case 1: Equal buffers (using the same buffer twice)
        boolean result1 = ArchiveUtils.isEqual(testBuffer1, testBuffer1);
        bh.consume(result1);

        // Test case 2: Unequal buffers
        boolean result2 = ArchiveUtils.isEqual(testBuffer1, testBuffer2);
        bh.consume(result2);
    }

    @Benchmark
    public void testIsEqualWithNull(Blackhole bh) {
        // Test case 1: Equal buffers (using the same buffer twice)
        boolean result1 = ArchiveUtils.isEqualWithNull(testBuffer1, 0, 1024, testBuffer1, 0, 1024);
        bh.consume(result1);
    }

    @Benchmark
    public void testIsEqualWithNull_Unequal(Blackhole bh) {
        // Test case 2: Unequal buffers (using the same buffer twice)
        boolean result2 = ArchiveUtils.isEqualWithNull(testBuffer1, 0, 1024, testBuffer2, 0, 1024);
        bh.consume(result2);
    }

    @Benchmark
    public void testMatchAsciiBuffer(Blackhole bh) {
        // Test case 1: Match (using a known ASCII string)
        boolean result1 = ArchiveUtils.matchAsciiBuffer("Hello World", testBuffer1);
        bh.consume(result1);

        // Test case 2: Mismatch
        boolean result2 = ArchiveUtils.matchAsciiBuffer("Goodbye World", testBuffer1);
        bh.consume(result2);
    }

    @Benchmark
    public void testMatchAsciiBuffer_Offset(Blackhole bh) {
        // Test case 1: Match at offset 0
        boolean result1 = ArchiveUtils.matchAsciiBuffer("Hello World", testBuffer1, 0, 11);
        bh.consume(result1);

        // Test case 2: Mismatch at offset 0
        boolean result2 = ArchiveUtils.matchAsciiBuffer("Goodbye World", testBuffer1, 0, 11);
        bh.consume(result2);
    }

    @Benchmark
    public void testSanitize(Blackhole bh) {
        // Test case 1: Standard string
        String result1 = ArchiveUtils.sanitize(testString);
        bh.consume(result1);

        // Test case 2: Long string (to test truncation/sanitization logic)
        String longString = "A".repeat(500);
        String result2 = ArchiveUtils.sanitize(longString);
        bh.consume(result2);
    }
}

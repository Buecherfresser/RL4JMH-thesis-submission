package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.zstandard.ZstdUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZstdUtilsBenchmark {

    @Setup
    public void setup() {
        // No mutable state required for static utility calls.
    }

    @Benchmark
    public void testIsZstdCompressionAvailable(Blackhole bh) {
        // Test the static method that checks availability.
        bh.consume(ZstdUtils.isZstdCompressionAvailable());
    }

    @Benchmark
    public void testMatchesZstandardFrameMagic(Blackhole bh) {
        // Test the static method that checks the frame magic signature.
        // Use a signature that matches the ZSTANDARD_FRAME_MAGIC for a positive test.
        byte[] matchingSignature = { (byte) 0x28, (byte) 0xB5, (byte) 0x2F, (byte) 0xFD };
        bh.consume(ZstdUtils.matches(matchingSignature, 4));

        // Test a signature that should fail (wrong magic bytes)
        byte[] nonMatchingSignature = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
        bh.consume(ZstdUtils.matches(nonMatchingSignature, 4));
    }

    @Benchmark
    public void testSetCacheZstdAvailability(Blackhole bh) {
        // Test setting the cache availability.
        ZstdUtils.setCacheZstdAvailablity(true);
        // Removed call to getCachedZstdAvailability() as it is package-private and caused compilation errors.
    }
}

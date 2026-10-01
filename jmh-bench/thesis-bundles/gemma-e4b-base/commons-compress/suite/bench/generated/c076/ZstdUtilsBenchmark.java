package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.zstandard.ZstdUtils;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZstdUtilsBenchmark {

    private byte[] matchingSignature;
    private byte[] nonMatchingSignature;
    private int shortLength;

    @Setup
    public void setup() {
        // 1. Matching Signature (ZSTANDARD_FRAME_MAGIC: 4 bytes)
        // { (byte) 0x28, (byte) 0xB5, (byte) 0x2F, (byte) 0xFD }
        matchingSignature = new byte[] { (byte) 0x28, (byte) 0xB5, (byte) 0x2F, (byte) 0xFD };

        // 2. Non-Matching Signature (Random bytes)
        nonMatchingSignature = new byte[10];
        new Random().nextBytes(nonMatchingSignature);

        // 3. Short Length (less than ZSTANDARD_FRAME_MAGIC.length = 4)
        shortLength = 3;
    }

    // --- Benchmarks for ZstdUtils.matches(byte[] signature, int length) ---

    @Benchmark
    public boolean matches_ValidZstandardSignature(Blackhole bh) {
        boolean result = ZstdUtils.matches(matchingSignature, matchingSignature.length);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean matches_InvalidSignature(Blackhole bh) {
        boolean result = ZstdUtils.matches(nonMatchingSignature, nonMatchingSignature.length);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean matches_ShortSignature(Blackhole bh) {
        boolean result = ZstdUtils.matches(matchingSignature, shortLength);
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for ZstdUtils.isZstdCompressionAvailable() ---

    // Note: This method relies on internal state and potentially slow class loading/reflection.
    // We benchmark it to measure the cost of the check itself.
    @Benchmark
    public boolean isZstdCompressionAvailable_Check(Blackhole bh) {
        boolean result = ZstdUtils.isZstdCompressionAvailable();
        bh.consume(result);
        return result;
    }

    // --- Benchmarks for ZstdUtils.setCacheZstdAvailablity(boolean doCache) ---

    @Benchmark
    public void setCacheZstdAvailablity_SetToFalse(Blackhole bh) {
        ZstdUtils.setCacheZstdAvailablity(false);
        bh.consume(true); // Consume result to prevent dead code elimination
    }

    @Benchmark
    public void setCacheZstdAvailablity_SetToTrue(Blackhole bh) {
        ZstdUtils.setCacheZstdAvailablity(true);
        bh.consume(true); // Consume result to prevent dead code elimination
    }
}

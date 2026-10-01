package bench.generated.c062;

import org.apache.commons.compress.compressors.lzma.LZMAUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LZMAUtilsBenchmark {

    // Since LZMAUtils is static, we don't need instance fields for state,
    // but we can use them if we were testing non-static methods.
    // For static methods, we rely on the static initialization/state management.

    @Benchmark
    public void testMatches_True(Blackhole bh) {
        // Test with the correct magic bytes
        byte[] signature = {(byte) 0x5D, 0, 0};
        boolean result = LZMAUtils.matches(signature, signature.length);
        bh.consume(result);
    }

    @Benchmark
    public void testMatches_False(Blackhole bh) {
        // Test with incorrect bytes
        byte[] signature = {1, 2, 3};
        boolean result = LZMAUtils.matches(signature, signature.length);
        bh.consume(result);
    }

    @Benchmark
    public void testIsLZMACompressionAvailable(Blackhole bh) {
        // This method checks internal class availability, which might involve reflection/class loading.
        boolean result = LZMAUtils.isLZMACompressionAvailable();
        bh.consume(result);
    }

    @Benchmark
    public void testSetCacheAvailability(Blackhole bh) {
        // Test setting the cache state.
        LZMAUtils.setCacheLZMAAvailablity(true);
        // Consume the result (void method, so we just ensure the call happens)
        bh.consume(null);
    }

    // Note: Benchmarking methods relying on FileNameUtil (getCompressedFileName, etc.)
    // is difficult without mocking or knowing the exact internal logic paths,
    // as they depend on string manipulation and internal file name heuristics.
    // We focus on the core byte comparison and state manipulation methods.
}

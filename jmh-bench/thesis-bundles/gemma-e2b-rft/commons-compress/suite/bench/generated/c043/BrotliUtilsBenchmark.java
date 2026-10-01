package bench.generated.c043;

import org.apache.commons.compress.compressors.brotli.BrotliUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BrotliUtilsBenchmark {

    /**
     * Benchmarks the check for Brotli compression availability using the public API.
     */
    @Benchmark
    public void checkBrotliAvailability(Blackhole bh) {
        boolean available = BrotliUtils.isBrotliCompressionAvailable();
        bh.consume(available);
    }

    /**
     * Benchmarks setting the cache availability to DONT_CACHE.
     */
    @Benchmark
    public void setCacheToDontCache(Blackhole bh) {
        BrotliUtils.setCacheBrotliAvailablity(false);
        bh.consume(null);
    }

    /**
     * Benchmarks setting the cache availability to CACHED_AVAILABLE.
     */
    @Benchmark
    public void setCacheToCachedAvailable(Blackhole bh) {
        BrotliUtils.setCacheBrotliAvailablity(true);
        bh.consume(null);
    }
}

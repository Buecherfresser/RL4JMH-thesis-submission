package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.brotli.BrotliUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BrotliUtilsBenchmark {

    /**
     * Benchmarks the public static method that checks if Brotli compression is available.
     */
    @Benchmark
    public void checkAvailability(Blackhole bh) {
        // Call the public static method. The result is consumed by Blackhole.
        boolean available = BrotliUtils.isBrotliCompressionAvailable();
        bh.consume(available);
    }

    /**
     * Benchmarks the public static method that sets the caching availability flag.
     */
    @Benchmark
    public void setCacheAvailability(Blackhole bh) {
        // Call the public static method.
        BrotliUtils.setCacheBrotliAvailablity(true);
        bh.consume(null);
    }

    /**
     * Benchmarks a method that attempts to call the package-private method.
     * NOTE: This method is removed/modified because the original call to
     * getCachedBrotliAvailability() failed compilation due to access restrictions.
     */
    @Benchmark
    public void getCachedAvailability(Blackhole bh) {
        // We cannot call getCachedBrotliAvailability() as it is package-private.
        // We rely on public methods for benchmarking.
        BrotliUtils.isBrotliCompressionAvailable();
        bh.consume(null);
    }
}

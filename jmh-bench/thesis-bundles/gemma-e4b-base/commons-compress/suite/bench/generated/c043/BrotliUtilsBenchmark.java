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

    @Benchmark
    public void benchmarkIsBrotliCompressionAvailable(Blackhole bh) {
        // Tests the primary availability check, which involves internal class loading logic
        boolean available = BrotliUtils.isBrotliCompressionAvailable();
        bh.consume(available);
    }

    @Benchmark
    public void benchmarkSetCacheBrotliAvailablityToFalse(Blackhole bh) {
        // Tests resetting the cache state
        BrotliUtils.setCacheBrotliAvailablity(false);
        bh.consume(true); // Consume a dummy value
    }

    @Benchmark
    public void benchmarkSetCacheBrotliAvailablityToTrue(Blackhole bh) {
        // Tests forcing the availability check and caching the result
        BrotliUtils.setCacheBrotliAvailablity(true);
        bh.consume(true); // Consume a dummy value
    }
}

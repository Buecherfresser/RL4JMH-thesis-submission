package bench.generated.c043;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Field;
import org.apache.commons.compress.compressors.brotli.BrotliUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BrotliUtilsBenchmark {

    @State(Scope.Benchmark)
    public static class NoCacheState {
        @Setup(Level.Trial)
        public void setUp() {
            BrotliUtils.setCacheBrotliAvailablity(false);
        }
    }

    @State(Scope.Benchmark)
    public static class CachedState {
        @Setup(Level.Trial)
        public void setUp() {
            BrotliUtils.setCacheBrotliAvailablity(true);
        }
    }

    @State(Scope.Benchmark)
    public static class CacheToggleState {
        private Field cachedField;

        @Setup(Level.Trial)
        public void init() throws Exception {
            cachedField = BrotliUtils.class.getDeclaredField("cachedBrotliAvailability");
            cachedField.setAccessible(true);
        }

        Object getCached() throws IllegalAccessException {
            return cachedField.get(null);
        }
    }

    @Benchmark
    public boolean benchmarkIsBrotliAvailable_NoCache(NoCacheState state) {
        return BrotliUtils.isBrotliCompressionAvailable();
    }

    @Benchmark
    public boolean benchmarkIsBrotliAvailable_Cached(CachedState state) {
        return BrotliUtils.isBrotliCompressionAvailable();
    }

    @Benchmark
    public void benchmarkSetCacheBrotliAvailablity_true(CacheToggleState state, Blackhole bh) throws Exception {
        BrotliUtils.setCacheBrotliAvailablity(true);
        bh.consume(state.getCached());
    }

    @Benchmark
    public void benchmarkSetCacheBrotliAvailablity_false(CacheToggleState state, Blackhole bh) throws Exception {
        BrotliUtils.setCacheBrotliAvailablity(false);
        bh.consume(state.getCached());
    }
}

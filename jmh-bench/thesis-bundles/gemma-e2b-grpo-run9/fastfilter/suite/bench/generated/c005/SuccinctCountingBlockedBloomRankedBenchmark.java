package bench.generated.c005;

import org.fastfilter.Filter;
import org.fastfilter.bloom.count.SuccinctCountingBlockedBloomRanked;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SuccinctCountingBlockedBloomRankedBenchmark {

    @Benchmark
    public void benchmarkMayContain(Blackhole bh) {
        // Create a fresh instance for each benchmark run to ensure isolation
        Filter filter = SuccinctCountingBlockedBloomRanked.construct(new long[]{1L, 2L, 3L}, 8);
        long key = 100L;
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkCardinality(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        Filter filter = SuccinctCountingBlockedBloomRanked.construct(new long[]{1L, 2L, 3L}, 8);
        long cardinality = filter.cardinality();
        bh.consume(cardinality);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        Filter filter = SuccinctCountingBlockedBloomRanked.construct(new long[]{1L, 2L, 3L}, 8);
        long bitCount = filter.getBitCount();
        bh.consume(bitCount);
    }

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        Filter filter = SuccinctCountingBlockedBloomRanked.construct(new long[]{1L, 2L, 3L}, 8);
        long key = 42L;
        filter.add(key);
        // Consume the result (void method)
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        Filter filter = SuccinctCountingBlockedBloomRanked.construct(new long[]{1L, 2L, 3L}, 8);
        long key = 1L;
        filter.remove(key);
        // Consume the result (void method)
        bh.consume(null);
    }
}

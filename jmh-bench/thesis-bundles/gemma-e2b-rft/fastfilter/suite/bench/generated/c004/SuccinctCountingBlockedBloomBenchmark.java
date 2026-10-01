package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.Filter;
import org.fastfilter.bloom.count.SuccinctCountingBlockedBloom;
import org.fastfilter.utils.Hash;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SuccinctCountingBlockedBloomBenchmark {

    private SuccinctCountingBlockedBloom filter;
    private long[] keys;
    private final int BITS_PER_KEY = 16;
    private final int NUM_KEYS = 100000;
    private final Random random = new Random(42);

    @Setup
    public void setup() {
        // 1. Generate keys
        keys = new long[NUM_KEYS];
        for (int i = 0; i < NUM_KEYS; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the filter
        // Note: The static construct method handles the initial population.
        filter = SuccinctCountingBlockedBloom.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public void testMayContain(Blackhole bh) {
        long key = keys[random.nextInt(NUM_KEYS)];
        boolean result = filter.mayContain(key);
        bh.consume(result);
    }

    @Benchmark
    public void testAdd(Blackhole bh) {
        long key = keys[random.nextInt(NUM_KEYS)];
        filter.add(key);
        bh.consume(null);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        long key = keys[random.nextInt(NUM_KEYS)];
        filter.remove(key);
        bh.consume(null);
    }

    @Benchmark
    public void testCardinality(Blackhole bh) {
        long cardinality = filter.cardinality();
        bh.consume(cardinality);
    }
}

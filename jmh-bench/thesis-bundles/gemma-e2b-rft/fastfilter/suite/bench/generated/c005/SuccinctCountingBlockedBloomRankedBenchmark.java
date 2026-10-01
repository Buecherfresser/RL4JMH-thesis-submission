package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.fastfilter.bloom.count.SuccinctCountingBlockedBloomRanked;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class SuccinctCountingBlockedBloomRankedBenchmark {

    private SuccinctCountingBlockedBloomRanked filter;
    private long[] keys;
    private final int BITS_PER_KEY = 10;

    @Setup
    public void setup() {
        // 1. Generate a fixed set of keys for the filter construction
        Random random = new Random(42); // Fixed seed for reproducibility
        keys = new long[10000]; // Increased size slightly for better testing
        for (int i = 0; i < keys.length; i++) {
            keys[i] = random.nextLong();
        }

        // 2. Construct the filter instance using the static factory method
        // This involves adding all keys during construction.
        filter = SuccinctCountingBlockedBloomRanked.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public void mayContain(Blackhole bh) {
        long testKey = keys[0]; // Use a key that is likely present
        boolean result = filter.mayContain(testKey);
        bh.consume(result);
    }

    @Benchmark
    public void mayContainMiss(Blackhole bh) {
        // Generate a key guaranteed not to be in the set (by generating a new random long)
        long testKey = randomLong();
        boolean result = filter.mayContain(testKey);
        bh.consume(result);
    }

    @Benchmark
    public void add(Blackhole bh) {
        long keyToAdd = randomLong();
        filter.add(keyToAdd);
        bh.consume(null);
    }

    @Benchmark
    public void remove(Blackhole bh) {
        // Try to remove a key that is likely present
        long keyToRemove = keys[0];
        filter.remove(keyToRemove);
        bh.consume(null);
    }

    @Benchmark
    public void cardinality(Blackhole bh) {
        long count = filter.cardinality();
        bh.consume(count);
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        long count = filter.getBitCount();
        bh.consume(count);
    }

    /** Helper method to generate a random long for testing purposes. */
    private long randomLong() {
        return new Random().nextLong();
    }
}

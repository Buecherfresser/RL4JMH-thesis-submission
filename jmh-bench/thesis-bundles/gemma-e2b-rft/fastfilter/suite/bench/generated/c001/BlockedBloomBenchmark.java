package bench.generated.c001;

import org.fastfilter.Filter;
import org.fastfilter.bloom.BlockedBloom;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BlockedBloomBenchmark {

    private BlockedBloom bloomFilter;
    private List<Long> keys;
    private final int BITS_PER_KEY = 11;
    private final Random random = new Random(42); // Fixed seed for reproducibility

    @Setup
    public void setup() {
        // 1. Generate a large set of unique keys for construction
        int keyCount = 100_000;
        keys = new ArrayList<>(keyCount);
        for (int i = 0; i < keyCount; i++) {
            keys.add(random.nextLong());
        }

        // 2. Construct the BlockedBloom filter
        // BlockedBloom.construct handles adding all keys internally.
        bloomFilter = BlockedBloom.construct(keys.stream().mapToLong(Long::longValue).toArray(), BITS_PER_KEY);
    }

    @Benchmark
    public void mayContain(Blackhole bh) {
        // Test lookup for a key that is likely present (from the setup set)
        long testKey = keys.get(0);
        boolean result = bloomFilter.mayContain(testKey);
        bh.consume(result);
    }

    @Benchmark
    public void add(Blackhole bh) {
        // Test adding a new key
        long newKey = random.nextLong();
        bloomFilter.add(newKey);
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        // Test retrieving the space used by the filter
        long count = bloomFilter.getBitCount();
        bh.consume(count);
    }
}

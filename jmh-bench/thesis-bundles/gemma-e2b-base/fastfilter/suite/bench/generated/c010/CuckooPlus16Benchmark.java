package bench.generated.c010;

import org.fastfilter.cuckoo.CuckooPlus16;
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
public class CuckooPlus16Benchmark {

    private CuckooPlus16 filter;
    private List<Long> keys;
    private long[] testKeys;
    private final int KEY_COUNT = 10000;

    @Setup
    public void setup() {
        // 1. Generate a large set of unique keys for construction benchmarking
        keys = new ArrayList<>(KEY_COUNT);
        Random random = new Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < KEY_COUNT; i++) {
            keys.add(random.nextLong());
        }
        testKeys = keys.stream().mapToLong(Long::longValue).toArray();

        // 2. Construct the filter instance once for insertion/containment benchmarks
        // We use the static construct method here.
        filter = CuckooPlus16.construct(testKeys);
    }

    @Benchmark
    public void benchmarkInsert(Blackhole bh) {
        // Test insertion of a key that is likely already present or new
        long keyToInsert = testKeys[0];
        filter.insert(keyToInsert);
        bh.consume(keyToInsert);
    }

    @Benchmark
    public void benchmarkMayContainPresent(Blackhole bh) {
        // Test containment check for a key known to be in the set
        long keyToContain = testKeys[500];
        boolean result = filter.mayContain(keyToContain);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMayContainAbsent(Blackhole bh) {
        // Test containment check for a key known not to be in the set
        long absentKey = 999999999999999L;
        boolean result = filter.mayContain(absentKey);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        // Test retrieval of filter metrics
        long bitCount = filter.getBitCount();
        bh.consume(bitCount);
    }
}

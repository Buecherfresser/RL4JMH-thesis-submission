package bench.generated.c010;

import org.fastfilter.Filter;
import org.fastfilter.utils.Hash;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.fastfilter.cuckoo.CuckooPlus16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CuckooPlus16Benchmark {

    private CuckooPlus16 filter;
    private List<Long> keys;
    private long keyToInsert;
    private long keyToContain;
    private long keyNotPresent;

    // Fixed seed for reproducible key generation
    private final Random random = new Random(42);

    @Setup
    public void setup() {
        // 1. Generate a large set of unique keys for the filter construction
        int keyCount = 10000;
        keys = new ArrayList<>(keyCount);
        for (int i = 0; i < keyCount; i++) {
            // Generate unique long keys
            keys.add(random.nextLong());
        }

        // 2. Construct the CuckooPlus16 filter instance
        // We use the static constructor here, which handles the internal insertion logic.
        this.filter = CuckooPlus16.construct(keys.stream().mapToLong(Long::longValue).toArray());

        // 3. Select representative keys for testing
        // Key to insert: one of the keys we already inserted (guaranteed to be present)
        this.keyToInsert = keys.get(0);

        // Key to contain: another key we inserted (guaranteed to be present)
        this.keyToContain = keys.get(100);

        // Key not present: a key guaranteed not to be in the set (generate a new random one)
        this.keyNotPresent = random.nextLong();
    }

    @Benchmark
    public void insertKey(Blackhole bh) {
        // Test insertion of a key that is likely already present (stressing collision/rehash logic)
        filter.insert(keyToInsert);
        bh.consume(null);
    }

    @Benchmark
    public void mayContainPresent(Blackhole bh) {
        // Test containment check for a key known to be present
        boolean result = filter.mayContain(keyToContain);
        bh.consume(result);
    }

    @Benchmark
    public void mayContainAbsent(Blackhole bh) {
        // Test containment check for a key known to be absent
        boolean result = filter.mayContain(keyNotPresent);
        bh.consume(result);
    }

    @Benchmark
    public void getBitCount(Blackhole bh) {
        // Test reading the filter's space usage metric
        long count = filter.getBitCount();
        bh.consume(count);
    }
}

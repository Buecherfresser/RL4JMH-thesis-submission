package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.HashSet;
import org.fastfilter.cuckoo.CuckooPlus16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CuckooPlus16Benchmark {

    private long[] initialKeys;
    private long[] insertKeys;
    private CuckooPlus16[] filters;
    private int poolIndex;
    private int keyIndex;
    private CuckooPlus16 filterForLookup;
    private long[] presentKeys;
    private long[] absentKeys;
    private int presentIndex;
    private int absentIndex;

    @Setup(Level.Trial)
    public void setup() {
        int numInitial = 1000;
        int numInsert = 1000;
        int capacity = numInitial + 10000; // large enough for many insertions

        Random rand = new Random(42);
        initialKeys = new long[numInitial];
        for (int i = 0; i < numInitial; i++) {
            initialKeys[i] = rand.nextLong();
        }

        // Generate insert keys distinct from initial keys and from each other
        HashSet<Long> used = new HashSet<>();
        for (long k : initialKeys) used.add(k);
        insertKeys = new long[numInsert];
        for (int i = 0; i < numInsert; i++) {
            long key;
            do {
                key = rand.nextLong();
            } while (!used.add(key));
            insertKeys[i] = key;
        }

        // Build a pool of filters, each pre-populated with initialKeys
        filters = new CuckooPlus16[numInsert];
        for (int i = 0; i < numInsert; i++) {
            CuckooPlus16 f = new CuckooPlus16(capacity);
            for (long k : initialKeys) {
                f.insert(k);
            }
            filters[i] = f;
        }

        // Filter for lookup benchmarks (read-only)
        filterForLookup = CuckooPlus16.construct(initialKeys);
        presentKeys = initialKeys;
        absentKeys = new long[numInitial];
        for (int i = 0; i < numInitial; i++) {
            long key;
            do {
                key = rand.nextLong();
            } while (used.contains(key));
            absentKeys[i] = key;
        }
        presentIndex = 0;
        absentIndex = 0;
        poolIndex = 0;
        keyIndex = 0;
    }

    @Benchmark
    public CuckooPlus16 construct() {
        return CuckooPlus16.construct(initialKeys);
    }

    @Benchmark
    public void insert(Blackhole bh) {
        CuckooPlus16 f = filters[poolIndex];
        f.insert(insertKeys[keyIndex]);
        poolIndex = (poolIndex + 1) % filters.length;
        keyIndex = (keyIndex + 1) % insertKeys.length;
        bh.consume(f);
    }

    @Benchmark
    public boolean mayContainPresent() {
        long key = presentKeys[presentIndex];
        presentIndex = (presentIndex + 1) % presentKeys.length;
        return filterForLookup.mayContain(key);
    }

    @Benchmark
    public boolean mayContainAbsent() {
        long key = absentKeys[absentIndex];
        absentIndex = (absentIndex + 1) % absentKeys.length;
        return filterForLookup.mayContain(key);
    }

    @Benchmark
    public long getBitCount() {
        return filterForLookup.getBitCount();
    }
}

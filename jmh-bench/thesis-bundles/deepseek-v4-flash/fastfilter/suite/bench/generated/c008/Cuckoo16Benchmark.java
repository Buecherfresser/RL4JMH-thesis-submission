package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.fastfilter.cuckoo.Cuckoo16;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Cuckoo16Benchmark {

    private static final int NUM_KEYS = 10_000;
    private static final int INSERT_POOL_SIZE = 100;
    private static final int INSERT_CAPACITY = 100_000;
    private static final int INSERT_CYCLE = 50_000;

    private long[] constructKeys;
    private long[] positiveKeys;
    private long[] negativeKeys;
    private Cuckoo16 lookupFilter;
    private Cuckoo16[] insertFilters;
    private int insertIndex = 0;
    private int insertCount = 0;
    private long insertKeyCounter = 0;
    private int positiveIndex = 0;
    private int negativeIndex = 0;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random(12345);
        constructKeys = new long[NUM_KEYS];
        positiveKeys = new long[NUM_KEYS];
        for (int i = 0; i < NUM_KEYS; i++) {
            long key = random.nextLong();
            constructKeys[i] = key;
            positiveKeys[i] = key;
        }
        lookupFilter = Cuckoo16.construct(positiveKeys);

        // Generate negative keys (not in positiveKeys)
        negativeKeys = new long[NUM_KEYS];
        int negCount = 0;
        while (negCount < NUM_KEYS) {
            long candidate = random.nextLong();
            boolean found = false;
            for (long key : positiveKeys) {
                if (key == candidate) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                negativeKeys[negCount++] = candidate;
            }
        }

        // Pre-create a pool of filters for insert benchmarks
        insertFilters = new Cuckoo16[INSERT_POOL_SIZE];
        for (int i = 0; i < INSERT_POOL_SIZE; i++) {
            insertFilters[i] = new Cuckoo16(INSERT_CAPACITY);
        }
    }

    @Benchmark
    public Cuckoo16 construct() {
        return Cuckoo16.construct(constructKeys);
    }

    @Benchmark
    public boolean mayContainPositive() {
        int idx = (positiveIndex++ & Integer.MAX_VALUE) % positiveKeys.length;
        return lookupFilter.mayContain(positiveKeys[idx]);
    }

    @Benchmark
    public boolean mayContainNegative() {
        int idx = (negativeIndex++ & Integer.MAX_VALUE) % negativeKeys.length;
        return lookupFilter.mayContain(negativeKeys[idx]);
    }

    @Benchmark
    public void insert(Blackhole bh) {
        Cuckoo16 filter = insertFilters[insertIndex];
        long key = insertKeyCounter++;
        filter.insert(key);
        bh.consume(filter);

        // Cycle to a fresh filter before the current one fills up
        if (++insertCount >= INSERT_CYCLE) {
            insertCount = 0;
            insertIndex = (insertIndex + 1) % INSERT_POOL_SIZE;
        }
    }

    @Benchmark
    public long getBitCount() {
        return lookupFilter.getBitCount();
    }
}

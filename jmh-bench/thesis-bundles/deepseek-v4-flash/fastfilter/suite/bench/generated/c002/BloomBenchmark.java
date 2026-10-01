package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.bloom.Bloom;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BloomBenchmark {

    private long[] keys;
    private long[] absentKeys;
    private Bloom bloomForLookup;
    private Bloom bloomForAdd;
    private long lookupKeyPresent;
    private long lookupKeyAbsent;
    private long addKey;

    private static final int NUM_KEYS = 10_000;
    private static final double BITS_PER_KEY = 10.0;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(12345);
        keys = new long[NUM_KEYS];
        for (int i = 0; i < NUM_KEYS; i++) {
            keys[i] = rnd.nextLong();
        }
        // Generate absent keys that are not in the set
        absentKeys = new long[NUM_KEYS];
        for (int i = 0; i < NUM_KEYS; i++) {
            long candidate;
            do {
                candidate = rnd.nextLong();
            } while (contains(keys, candidate));
            absentKeys[i] = candidate;
        }

        bloomForLookup = Bloom.construct(keys, BITS_PER_KEY);
        // A fresh filter for add benchmarks (starts empty)
        bloomForAdd = Bloom.construct(new long[0], BITS_PER_KEY);

        lookupKeyPresent = keys[0];
        lookupKeyAbsent = absentKeys[0];
        addKey = keys[1];
    }

    private static boolean contains(long[] arr, long value) {
        for (long v : arr) {
            if (v == value) return true;
        }
        return false;
    }

    @Benchmark
    public Bloom construct() {
        return Bloom.construct(keys, BITS_PER_KEY);
    }

    @Benchmark
    public boolean mayContainPresent() {
        return bloomForLookup.mayContain(lookupKeyPresent);
    }

    @Benchmark
    public boolean mayContainAbsent() {
        return bloomForLookup.mayContain(lookupKeyAbsent);
    }

    @Benchmark
    public long add() {
        bloomForAdd.add(addKey);
        return bloomForAdd.getBitCount();
    }

    @Benchmark
    public long getBitCount() {
        return bloomForLookup.getBitCount();
    }

    @Benchmark
    public boolean supportsAdd() {
        return bloomForLookup.supportsAdd();
    }
}

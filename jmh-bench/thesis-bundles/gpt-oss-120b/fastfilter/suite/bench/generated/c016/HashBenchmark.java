package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.utils.Hash;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HashBenchmark {

    private static final int ARRAY_SIZE = 1024;

    private long[] longKeys;
    private long[] seeds;
    private int[] intHashes;
    private int reduceN;
    private int index;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        longKeys = new long[ARRAY_SIZE];
        seeds = new long[ARRAY_SIZE];
        intHashes = new int[ARRAY_SIZE];
        for (int i = 0; i < ARRAY_SIZE; i++) {
            longKeys[i] = rnd.nextLong();
            seeds[i] = rnd.nextLong();
            intHashes[i] = rnd.nextInt();
        }
        // n must be positive; choose a value in a reasonable range
        reduceN = 1 + Math.abs(rnd.nextInt(1000));
        index = 0;
    }

    @Benchmark
    public long benchHash64() {
        int i = index++;
        if (index >= ARRAY_SIZE) {
            index = 0;
        }
        return Hash.hash64(longKeys[i], seeds[i]);
    }

    @Benchmark
    public long benchRandomSeed() {
        return Hash.randomSeed();
    }

    @Benchmark
    public int benchReduce() {
        int i = index++;
        if (index >= ARRAY_SIZE) {
            index = 0;
        }
        return Hash.reduce(intHashes[i], reduceN);
    }

    @Benchmark
    public void benchSetSeed(Blackhole bh) {
        int i = index++;
        if (index >= ARRAY_SIZE) {
            index = 0;
        }
        long seed = seeds[i];
        Hash.setSeed(seed);
        bh.consume(seed);
    }
}

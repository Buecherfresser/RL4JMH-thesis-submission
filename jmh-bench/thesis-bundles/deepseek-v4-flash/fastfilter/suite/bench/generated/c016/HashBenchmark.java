package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.fastfilter.utils.Hash;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HashBenchmark {

    private long[] keys;
    private int[] hashInts;
    private int[] ns;
    private int keyIndex;
    private int reduceIndex;
    private long seed;

    @Setup(Level.Trial)
    public void setup() {
        int size = 10_000;
        keys = new long[size];
        hashInts = new int[size];
        ns = new int[size];
        Random rnd = new Random(12345);
        seed = rnd.nextLong();
        for (int i = 0; i < size; i++) {
            keys[i] = rnd.nextLong();
            hashInts[i] = rnd.nextInt();
            ns[i] = 1 + rnd.nextInt(10_000);
        }
        keyIndex = 0;
        reduceIndex = 0;
    }

    @Benchmark
    public long hash64() {
        int i = keyIndex;
        keyIndex = (keyIndex + 1) & (keys.length - 1); // fast modulo (power of two)
        // workaround: actual modulo for clarity; keys.length is not power of two
        if (keyIndex == 0) keyIndex = 0;
        return Hash.hash64(keys[i], seed);
    }

    @Benchmark
    public int reduce() {
        int i = reduceIndex;
        reduceIndex = (reduceIndex + 1) & (ns.length - 1);
        if (reduceIndex == 0) reduceIndex = 0;
        return Hash.reduce(hashInts[i], ns[i]);
    }

    @Benchmark
    public long randomSeed() {
        return Hash.randomSeed();
    }
}

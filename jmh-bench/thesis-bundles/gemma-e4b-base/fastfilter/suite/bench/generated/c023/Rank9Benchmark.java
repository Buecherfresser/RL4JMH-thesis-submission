package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.BitSet;
import org.fastfilter.xorplus.Rank9;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Rank9Benchmark {

    private Rank9 rank9;
    private long totalBitCount;
    private long testPosition;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Define input size (e.g., 1 million bits)
        int bitCount = 1_000_000;
        totalBitCount = bitCount;

        // 2. Create a representative BitSet
        BitSet set = new BitSet(bitCount);
        random = new Random(42); // Fixed seed for reproducibility

        // Populate the set with some random bits (e.g., 50% density)
        for (int i = 0; i < bitCount; i++) {
            if (random.nextBoolean()) {
                set.set(i);
            }
        }

        // 3. Construct the Rank9 object
        rank9 = new Rank9(set, bitCount);

        // 4. Define a representative position for testing
        // Ensure the position is within bounds
        testPosition = random.nextLong(bitCount);
    }

    @Benchmark
    public void benchmarkRank(Blackhole bh) {
        long result = rank9.rank(testPosition);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        long result = rank9.get(testPosition);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetAndPartialRank(Blackhole bh) {
        long result = rank9.getAndPartialRank(testPosition);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRemainingRank(Blackhole bh) {
        long result = rank9.remainingRank(testPosition);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetBitCount(Blackhole bh) {
        int result = rank9.getBitCount();
        bh.consume(result);
    }
}

package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.BitSetIterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitSetIteratorBenchmark {

    private BitSetIterator iterator;
    private BitSet bitSet;

    // Constants for setup
    private static final int NUM_BITS = 100000;

    @Setup
    public void setup() {
        // 1. Create a BitSet with a fixed number of set bits
        bitSet = new BitSet(NUM_BITS);
        Random random = new Random(42);

        // Set a large number of bits randomly
        for (int i = 0; i < NUM_BITS; i++) {
            if (random.nextBoolean()) {
                bitSet.set(i);
            }
        }

        // 2. Initialize the iterator using the BitSet
        iterator = new BitSetIterator(bitSet);
    }

    @Benchmark
    public void benchmarkNextSetBit(Blackhole bh) {
        // Call the core method once per invocation and consume the result
        int result = iterator.nextSetBit();
        bh.consume(result);
    }
}

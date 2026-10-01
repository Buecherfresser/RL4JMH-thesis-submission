package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.BitSetIterator;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitSetIteratorBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        BitSet bitSet;
        BitSetIterator iterator;

        @Setup(Level.Trial)
        public void setUp() {
            // Create a BitSet with a fixed number of bits and a deterministic pattern.
            int numBits = 1024; // enough to exercise the iterator logic
            bitSet = new BitSet(numBits);
            Random rnd = new Random(0xDEADBEEFL);
            // Set bits with ~50% density
            for (int i = 0; i < numBits; i++) {
                if (rnd.nextBoolean()) {
                    bitSet.set(i);
                }
            }
            // Ensure at least one bit is set to avoid immediate NO_MORE
            if (bitSet.cardinality() == 0) {
                bitSet.set(0);
            }
            iterator = new BitSetIterator(bitSet);
        }
    }

    @Benchmark
    public int nextSetBitReuseIterator(BenchmarkState state) {
        // Calls the subject method exactly once per invocation.
        return state.iterator.nextSetBit();
    }

    @Benchmark
    public int nextSetBitNewIterator(BenchmarkState state) {
        // Constructs a fresh iterator for each invocation and calls nextSetBit once.
        BitSetIterator it = new BitSetIterator(state.bitSet);
        return it.nextSetBit();
    }
}

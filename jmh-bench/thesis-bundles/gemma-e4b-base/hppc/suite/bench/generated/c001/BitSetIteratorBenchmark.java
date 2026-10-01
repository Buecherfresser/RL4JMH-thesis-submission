package bench.generated.c001;

import com.carrotsearch.hppc.BitSetIterator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class BitSetIteratorBenchmark {

    // Input data representation: long array and word count
    private long[] inputBits;
    private int inputNumWords;
    private static final int BITSET_SIZE = 1_000_000; // 1 million bits

    /**
     * Setup runs once per benchmark trial. We create and populate the large input data structure.
     */
    @Setup(Level.Trial)
    public void setupTrial() {
        // Calculate number of 64-bit words needed
        inputNumWords = (BITSET_SIZE + 63) / 64;
        inputBits = new long[inputNumWords];

        // Populate the long array with a pattern of set bits (50% density)
        // We set bits i and i+1 for i = 0, 2, 4, ...
        for (int i = 0; i < BITSET_SIZE; i += 2) {
            // Set bit i
            int wordIndex_i = i / 64;
            int bitOffset_i = i % 64;
            inputBits[wordIndex_i] |= (1L << bitOffset_i);

            // Set bit i + 1
            int wordIndex_i1 = (i + 1) / 64;
            int bitOffset_i1 = (i + 1) % 64;

            // Check bounds just in case, although BITSET_SIZE is chosen to fit
            if (wordIndex_i1 < inputNumWords) {
                inputBits[wordIndex_i1] |= (1L << bitOffset_i1);
            }
        }
    }

    /**
     * Benchmarks the cost of advancing the iterator to the next set bit.
     * Since BitSetIterator is stateful, we must create a fresh instance for every invocation.
     */
    @Benchmark
    public int nextSetBitOperation(Blackhole bh) {
        // Create a fresh iterator instance for every run, using the pre-built input data
        BitSetIterator iterator = new BitSetIterator(inputBits, inputNumWords);

        // Call the subject method exactly once
        int result = iterator.nextSetBit();

        // Consume the result
        bh.consume(result);
        return result;
    }
}

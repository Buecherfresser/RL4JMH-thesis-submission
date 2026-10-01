package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.BitBuffer;
import org.fastfilter.gcs.MonotoneList;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MonotoneListBenchmark {

    // --- State Fields ---
    private int[] monotoneData;
    private BitBuffer buffer;
    private MonotoneList monotoneList;

    // Constants for setup
    private static final int DATA_SIZE = 10000;

    @Setup
    public void setup() {
        // 1. Setup Monotone Data
        monotoneData = new int[DATA_SIZE];
        Random random = new Random(42);
        for (int i = 0; i < DATA_SIZE; i++) {
            // Generate a monotone sequence (e.g., linear increase with small noise)
            monotoneData[i] = i * 2 + random.nextInt(5);
        }

        // 2. Setup BitBuffer (Used for generation/loading)
        // We initialize a buffer large enough to hold the generated data structure.
        // Since we don't know the exact size beforehand, we start with a large capacity.
        buffer = new BitBuffer(1024 * 1024); // 1MB initial capacity

        // 3. Pre-generate the list for access benchmarks
        monotoneList = MonotoneList.generate(monotoneData, buffer);
    }

    // --- Benchmark Methods ---

    /**
     * Benchmark for generating a MonotoneList from an array and a BitBuffer.
     * Measures the time taken for the complex generation logic.
     */
    @Benchmark
    public void benchmarkGenerate(Blackhole bh) {
        // Re-initialize buffer for a clean generation test if needed, 
        // but for simplicity and focusing on the generation logic itself, 
        // we reuse the setup data structure.
        BitBuffer tempBuffer = new BitBuffer(1024 * 1024);
        MonotoneList generatedList = MonotoneList.generate(monotoneData, tempBuffer);
        bh.consume(generatedList);
    }

    /**
     * Benchmark for calculating the size of a MonotoneList from an array.
     * Measures the time taken for the size calculation logic.
     */
    @Benchmark
    public void benchmarkGetSize(Blackhole bh) {
        int size = MonotoneList.getSize(monotoneData);
        bh.consume(size);
    }

    /**
     * Benchmark for loading a MonotoneList from a BitBuffer.
     * Measures the time taken for deserialization.
     */
    @Benchmark
    public void benchmarkLoad(Blackhole bh) {
        // Create a fresh buffer state for loading simulation
        BitBuffer loadBuffer = new BitBuffer(1024 * 1024);
        // Simulate writing data into the buffer first (this is a necessary setup step for load)
        MonotoneList tempList = MonotoneList.generate(monotoneData, loadBuffer);
        
        MonotoneList loadedList = MonotoneList.load(loadBuffer);
        bh.consume(loadedList);
    }

    /**
     * Benchmark for accessing a single element using the get(i) method.
     * Measures the time taken for the lookup path.
     */
    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        int index = DATA_SIZE / 2;
        int result = monotoneList.get(index);
        bh.consume(result);
    }

    /**
     * Benchmark for accessing a pair of elements using the getPair(i) method.
     * Measures the time taken for the pair lookup path.
     */
    @Benchmark
    public void benchmarkGetPair(Blackhole bh) {
        int index = DATA_SIZE / 2;
        long result = monotoneList.getPair(index);
        bh.consume(result);
    }
}

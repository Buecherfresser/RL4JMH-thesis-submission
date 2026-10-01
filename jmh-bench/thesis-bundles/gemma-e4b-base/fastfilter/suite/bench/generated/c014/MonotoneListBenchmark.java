package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.MonotoneList;
import org.fastfilter.gcs.BitBuffer;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class MonotoneListBenchmark {

    private int[] inputData;
    private MonotoneList monotoneList;
    private BitBuffer serializedBuffer;

    private static final int DATA_SIZE = 1000;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Prepare input data (must be monotone)
        inputData = new int[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            inputData[i] = i * 2; // Simple monotone sequence
        }

        // 2. Generate the list and capture the serialized buffer state
        BitBuffer bufferForGeneration = new BitBuffer(0);
        monotoneList = MonotoneList.generate(inputData, bufferForGeneration);
        
        // 3. Store the serialized buffer state for the 'load' benchmark
        // Since BitBuffer is mutable and we need a clean copy for load testing, 
        // we must re-serialize the data into a new buffer instance.
        // We use a fresh buffer and re-run the generation process to capture the exact serialized state.
        serializedBuffer = new BitBuffer(0);
        MonotoneList.generate(inputData, serializedBuffer);
    }

    // --- Construction/Serialization Benchmarks ---

    @Benchmark
    public void benchmarkGenerate(Blackhole bh) {
        // Re-generate the list in every invocation to measure construction time accurately
        BitBuffer bufferForGeneration = new BitBuffer(0);
        MonotoneList list = MonotoneList.generate(inputData, bufferForGeneration);
        bh.consume(list);
    }

    @Benchmark
    public void benchmarkGetSize(Blackhole bh) {
        int size = MonotoneList.getSize(inputData);
        bh.consume(size);
    }

    // --- Deserialization/Loading Benchmarks ---

    @Benchmark
    public void benchmarkLoad(Blackhole bh) {
        // Load the list from the pre-serialized buffer
        MonotoneList list = MonotoneList.load(serializedBuffer);
        bh.consume(list);
    }

    // --- Access Benchmarks ---

    @Benchmark
    public void benchmarkGetSingleElement(Blackhole bh) {
        // Access a random element
        int index = DATA_SIZE / 2;
        int value = monotoneList.get(index);
        bh.consume(value);
    }

    @Benchmark
    public void benchmarkGetPairElement(Blackhole bh) {
        // Access a pair of consecutive elements
        int index = DATA_SIZE / 2;
        long pair = monotoneList.getPair(index);
        bh.consume(pair);
    }
}

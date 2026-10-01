package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.Sort;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortBenchmark {

    private long[] data;
    private final int DATA_SIZE = 50000;
    private final java.util.Random random = new java.util.Random(42);

    @Setup
    public void setup() {
        // Build a large, reproducible input array of long values.
        data = new long[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] = random.nextLong();
        }
    }

    @Benchmark
    public void sortFullArray(Blackhole bh) {
        // Call the method to sort the entire array.
        Sort.sortUnsigned(data);
        bh.consume(data);
    }

    @Benchmark
    public void sortSubArray(Blackhole bh) {
        // Test sorting a specific segment of the array.
        int offset = DATA_SIZE / 4;
        int length = DATA_SIZE / 2;

        // Call the method to sort a specific segment of the array.
        Sort.sortUnsigned(data, offset, length);
        bh.consume(data);
    }
}

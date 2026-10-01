package bench.generated.c015;

import org.fastfilter.gcs.Sort;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortBenchmark {

    private long[] data;
    private final int DATA_SIZE = 100_000;
    private final Random random = new Random(42);

    @Setup
    public void setup() {
        data = new long[DATA_SIZE];
        // Initialize data with random long values
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] = random.nextLong();
        }
    }

    @Benchmark
    public void sortFullArray(Blackhole bh) {
        Sort.sortUnsigned(data);
        bh.consume(data);
    }

    @Benchmark
    public void sortSubArray(Blackhole bh) {
        // Test sorting a large middle segment
        int offset = DATA_SIZE / 4;
        int length = DATA_SIZE / 2;
        Sort.sortUnsigned(data, offset, length);
        bh.consume(data);
    }
}

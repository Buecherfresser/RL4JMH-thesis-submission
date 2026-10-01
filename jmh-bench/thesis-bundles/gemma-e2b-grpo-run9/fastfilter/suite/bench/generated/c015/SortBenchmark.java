package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Arrays;

import org.fastfilter.gcs.Sort;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortBenchmark {

    @Setup
    public void setup() {
        // No complex setup needed for this static method.
    }

    @Benchmark
    public void benchmarkSortUnsigned(Blackhole bh) {
        // Rule 8: Create a mutable array locally to measure the cost of the operation.
        // This ensures we measure the cost of sorting an unsorted state.
        long[] data = new long[1024];
        Random random = new Random();
        for (int i = 0; i < data.length; i++) {
            // Populate with non-final, non-literal values
            data[i] = random.nextLong();
        }

        // Call the subject method.
        Sort.sortUnsigned(data);
        
        // Rule 7: Consume the result.
        bh.consume(data);
    }
}

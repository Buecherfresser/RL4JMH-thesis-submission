package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.fastfilter.gcs.Sort;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortBenchmark {

    private long[] unsorted;
    private int offset;
    private int len;

    @Setup
    public void setup() {
        int size = 1 << 14; // 16384 elements
        Random rnd = new Random(12345L);
        unsorted = new long[size];
        for (int i = 0; i < size; i++) {
            unsorted[i] = rnd.nextLong();
        }
        offset = size / 4;          // start sorting a quarter into the array
        len = size / 2;             // sort half of the array
    }

    @Benchmark
    public long sortUnsignedFullArray() {
        long[] data = unsorted.clone();
        Sort.sortUnsigned(data);
        return data[0];
    }

    @Benchmark
    public long sortUnsignedPartialArray() {
        long[] data = unsorted.clone();
        Sort.sortUnsigned(data, offset, len);
        return data[offset];
    }
}

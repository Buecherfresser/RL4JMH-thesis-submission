package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import org.fastfilter.gcs.Sort;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortBenchmark {

    @Param({"10000"})
    private int size;

    private long[][] pool;
    private int index = 0;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random(12345);
        int poolSize = 100;
        pool = new long[poolSize][];
        for (int i = 0; i < poolSize; i++) {
            long[] arr = new long[size];
            for (int j = 0; j < size; j++) {
                arr[j] = random.nextLong();
            }
            pool[i] = arr;
        }
    }

    private long[] nextArray() {
        long[] src = pool[index];
        index = (index + 1) % pool.length;
        long[] copy = new long[size];
        System.arraycopy(src, 0, copy, 0, size);
        return copy;
    }

    @Benchmark
    public long[] sortFull() {
        long[] arr = nextArray();
        Sort.sortUnsigned(arr);
        return arr;
    }

    @Benchmark
    public long[] sortSubrange() {
        long[] arr = nextArray();
        int offset = 100;
        int len = size - 200;
        Sort.sortUnsigned(arr, offset, len);
        return arr;
    }
}

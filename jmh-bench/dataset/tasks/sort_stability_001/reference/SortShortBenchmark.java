package bench.generated;

import bench.SortShort;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Benchmark)
public class SortShortBenchmark {

    @Param({"1024"})
    public int batches;

    @Param({"32"})
    public int len;

    private int[][] data;

    @Setup
    public void setup() {
        Random rng = new Random(61);
        data = new int[batches][len];
        for (int i = 0; i < batches; i++) {
            for (int j = 0; j < len; j++) data[i][j] = rng.nextInt(10_000);
        }
    }

    @Benchmark
    public long sort() {
        return SortShort.sortAndSum(data);
    }
}

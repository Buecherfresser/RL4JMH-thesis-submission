package bench.generated;

import bench.Quicksort;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Benchmark)
public class QuicksortBenchmark {

    @Param({"5000"})
    public int size;

    private int[] template;
    private int[] work;

    @Setup(Level.Trial)
    public void setupTrial() {
        template = new int[size];
        for (int i = 0; i < size; i++) {
            template[i] = i;
        }
        work = new int[size];
    }

    @Setup(Level.Invocation)
    public void freshCopy() {
        System.arraycopy(template, 0, work, 0, size);
    }

    @Benchmark
    public int[] sort() {
        Quicksort.sort(work);
        return work;
    }
}

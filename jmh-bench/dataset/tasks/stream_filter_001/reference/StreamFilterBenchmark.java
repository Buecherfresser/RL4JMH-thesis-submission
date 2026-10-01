package bench.generated;

import bench.StreamFilter;
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
public class StreamFilterBenchmark {

    @Param({"200000"})
    public int size;

    private int[] data;

    @Setup
    public void setup() {
        Random rng = new Random(13);
        data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = rng.nextInt(1000);
        }
    }

    @Benchmark
    public int countOver() {
        return StreamFilter.countOver(data, 500);
    }
}

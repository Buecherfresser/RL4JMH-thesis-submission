package bench.generated;

import bench.SumLong;
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
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Benchmark)
public class SumLongBenchmark {

    @Param({"2000000"})
    public int size;

    @Param({"8"})
    public int stride;

    private long[] data;

    @Setup
    public void setup() {
        Random rng = new Random(67);
        data = new long[size];
        for (int i = 0; i < size; i++) data[i] = rng.nextLong();
    }

    @Benchmark
    public long sumStride() {
        return SumLong.sumStride(data, stride);
    }
}

package bench.generated;

import bench.Prefix;
import java.util.ArrayList;
import java.util.List;
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
public class PrefixBenchmark {

    @Param({"100000"})
    public int size;

    @Param({"10000"})
    public int prefix;

    private List<Integer> list;

    @Setup
    public void setup() {
        Random rng = new Random(29);
        list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(rng.nextInt());
        }
    }

    @Benchmark
    public long sumPrefix() {
        return Prefix.sumPrefix(list, prefix);
    }
}

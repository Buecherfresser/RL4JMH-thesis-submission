package bench;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ChangelogFilterBenchmark {

    @Param({"2048"})
    public int size;

    private List<Integer> input;
    private int threshold;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        ArrayList<Integer> data = new ArrayList<>(size);
        for (int i = 0; i < size; i++) data.add(rng.nextInt(1000));
        input = data;
        threshold = 500;
    }

    @Benchmark
    public void retain(Blackhole bh) {
        bh.consume(ChangelogFilter.retain(input, threshold));
    }
}

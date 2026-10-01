package bench.generated;

import bench.SetLookup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
public class SetLookupBenchmark {

    @Param({"10000"})
    public int setSize;

    @Param({"1000"})
    public int probeCount;

    private Set<Integer> set;
    private List<Integer> probes;

    @Setup
    public void setup() {
        Random rng = new Random(7);
        set = new HashSet<>(setSize * 2);
        for (int i = 0; i < setSize; i++) {
            set.add(rng.nextInt());
        }
        probes = new ArrayList<>(probeCount);
        for (int i = 0; i < probeCount; i++) {
            probes.add(rng.nextInt());
        }
    }

    @Benchmark
    public int countHits() {
        return SetLookup.countHits(set, probes);
    }
}

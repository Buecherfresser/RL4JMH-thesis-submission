package bench.generated;

import bench.Intersect;
import java.util.HashSet;
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
public class IntersectBenchmark {

    @Param({"200"})
    public int small;

    @Param({"20000"})
    public int big;

    private Set<Integer> a;
    private Set<Integer> b;

    @Setup
    public void setup() {
        Random rng = new Random(53);
        a = new HashSet<>(small * 2);
        for (int i = 0; i < small; i++) a.add(rng.nextInt());
        b = new HashSet<>(big * 2);
        for (int i = 0; i < big; i++) b.add(rng.nextInt());
    }

    @Benchmark
    public int size() {
        return Intersect.sizeOfIntersection(a, b);
    }
}

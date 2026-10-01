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
import java.util.Random;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class AdvancerBenchmark {

    @Param({"4096"})
    public int size;

    @Param({"256"})
    public int queries;

    private int[] arr;
    private int[] targets;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        arr = new int[size];
        int v = 0;
        for (int i = 0; i < size; i++) {
            v += 1 + rng.nextInt(8);
            arr[i] = v;
        }
        targets = new int[queries];
        for (int i = 0; i < queries; i++) targets[i] = rng.nextInt(v + 16);
    }

    @Benchmark
    public void advance(Blackhole bh) {
        for (int i = 0; i < queries; i++) bh.consume(Advancer.advance(arr, targets[i]));
    }
}

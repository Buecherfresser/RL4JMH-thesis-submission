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
public class PartitionerBenchmark {

    @Param({"4096"})
    public int count;

    @Param({"64"})
    public int partitions;

    private int[] keys;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        keys = new int[count];
        for (int i = 0; i < count; i++) keys[i] = rng.nextInt();
    }

    @Benchmark
    public void partition(Blackhole bh) {
        for (int i = 0; i < count; i++) bh.consume(Partitioner.partition(keys[i], partitions));
    }
}

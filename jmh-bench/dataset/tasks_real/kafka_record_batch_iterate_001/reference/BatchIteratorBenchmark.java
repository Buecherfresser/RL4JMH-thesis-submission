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
public class BatchIteratorBenchmark {

    @Param({"128"})
    public int batches;

    @Param({"32"})
    public int batchSize;

    private List<List<Integer>> input;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        List<List<Integer>> data = new ArrayList<>(batches);
        for (int b = 0; b < batches; b++) {
            ArrayList<Integer> batch = new ArrayList<>(batchSize);
            for (int i = 0; i < batchSize; i++) batch.add(rng.nextInt(1000));
            data.add(batch);
        }
        input = data;
    }

    @Benchmark
    public void sum(Blackhole bh) {
        bh.consume(BatchIterator.sum(input));
    }
}

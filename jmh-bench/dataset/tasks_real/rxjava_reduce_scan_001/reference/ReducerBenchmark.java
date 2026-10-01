package bench;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ReducerBenchmark {

    @Param({"4096"})
    public int size;

    private long[] data;

    @Setup
    public void setup() {
        Random rng = new Random(7L);
        data = new long[size];
        for (int i = 0; i < size; i++) {
            data[i] = rng.nextLong();
        }
    }

    @Benchmark
    public long reduceSum() {
        return Reducer.reduce(data, 0L);
    }
}

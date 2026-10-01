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
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class NumericComparatorBenchmark {

    @Param({"64"})
    public int comparators;

    @Param({"128"})
    public int size;

    @Param({"4"})
    public int queried;

    private long[][] sources;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        sources = new long[comparators][];
        for (int i = 0; i < comparators; i++) {
            long[] data = new long[size];
            for (int j = 0; j < size; j++) {
                data[j] = rng.nextLong();
            }
            sources[i] = data;
        }
    }

    @Benchmark
    public void constructManyQueryFew(Blackhole bh) {
        NumericComparator[] cs = new NumericComparator[comparators];
        for (int i = 0; i < comparators; i++) {
            cs[i] = new NumericComparator(sources[i]);
        }
        for (int i = 0; i < queried; i++) {
            bh.consume(cs[i].min());
        }
        bh.consume(cs);
    }
}

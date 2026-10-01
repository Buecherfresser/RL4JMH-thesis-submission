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
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class ConcatenatorBenchmark {

    @Param({"32"})
    public int parts;

    private String[] input;

    @Setup
    public void setup() {
        input = new String[parts];
        for (int i = 0; i < parts; i++) input[i] = "part" + i + "_";
    }

    @Benchmark
    public void join(Blackhole bh) {
        bh.consume(Concatenator.join(input));
    }
}

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
public class RestoreCounterBenchmark {

    @Param({"1024"})
    public int steps;

    private RestoreCounter counter;

    @Setup
    public void setup() {
        counter = new RestoreCounter();
    }

    @Benchmark
    public void incrementBy(Blackhole bh) {
        bh.consume(counter.incrementBy(steps));
    }
}

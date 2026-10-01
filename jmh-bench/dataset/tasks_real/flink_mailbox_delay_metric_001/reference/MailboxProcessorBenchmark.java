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
public class MailboxProcessorBenchmark {

    @Param({"2048"})
    public int size;

    private int[] input;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        input = new int[size];
        for (int i = 0; i < size; i++) input[i] = rng.nextInt(1000);
    }

    @Benchmark
    public void process(Blackhole bh) {
        bh.consume(MailboxProcessor.process(input));
    }
}

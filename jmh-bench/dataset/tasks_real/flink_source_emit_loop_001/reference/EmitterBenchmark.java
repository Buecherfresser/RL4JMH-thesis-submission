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
public class EmitterBenchmark {

    @Param({"1024"})
    public int size;

    private int[] src;
    private int[] dst;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        src = new int[size];
        dst = new int[size];
        for (int i = 0; i < size; i++) src[i] = rng.nextInt(1000);
    }

    @Benchmark
    public void emitAll(Blackhole bh) {
        bh.consume(Emitter.emitAll(src, dst));
        bh.consume(dst);
    }
}

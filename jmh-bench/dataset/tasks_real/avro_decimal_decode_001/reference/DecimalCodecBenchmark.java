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
public class DecimalCodecBenchmark {

    @Param({"256"})
    public int calls;

    @Param({"16"})
    public int payload;

    private byte[][] inputs;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        inputs = new byte[calls][payload];
        for (int i = 0; i < calls; i++) rng.nextBytes(inputs[i]);
    }

    @Benchmark
    public void decode(Blackhole bh) {
        for (int i = 0; i < calls; i++) bh.consume(DecimalCodec.decode(inputs[i], 4));
    }
}

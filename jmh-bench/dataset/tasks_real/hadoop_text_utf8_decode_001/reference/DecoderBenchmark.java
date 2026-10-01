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
public class DecoderBenchmark {

    @Param({"256"})
    public int calls;

    @Param({"64"})
    public int len;

    private byte[][] inputs;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        inputs = new byte[calls][len];
        for (int i = 0; i < calls; i++) {
            for (int j = 0; j < len; j++) inputs[i][j] = (byte) ('a' + rng.nextInt(26));
        }
    }

    @Benchmark
    public void decode(Blackhole bh) {
        for (int i = 0; i < calls; i++) bh.consume(Decoder.decode(inputs[i]));
    }
}

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
public class IntInputBenchmark {

    @Param({"1024"})
    public int bufferBytes;

    @Param({"256"})
    public int reads;

    private byte[] buf;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        buf = new byte[bufferBytes];
        rng.nextBytes(buf);
    }

    @Benchmark
    public void readInt(Blackhole bh) {
        for (int i = 0; i < reads; i++) {
            bh.consume(IntInput.readInt(buf, i * 4));
        }
    }
}

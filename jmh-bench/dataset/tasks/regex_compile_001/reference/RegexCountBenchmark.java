package bench.generated;

import bench.RegexCount;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Benchmark)
public class RegexCountBenchmark {

    @Param({"64"})
    public int length;

    private String text;

    @Setup
    public void setup() {
        Random rng = new Random(17);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int r = rng.nextInt(64);
            if (r < 50) {
                sb.append((char) ('a' + (r % 26)));
            } else {
                sb.append(' ');
            }
        }
        text = sb.toString();
    }

    @Benchmark
    public int countMatches() {
        return RegexCount.countMatches(text);
    }
}

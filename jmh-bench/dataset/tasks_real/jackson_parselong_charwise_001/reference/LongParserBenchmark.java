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
public class LongParserBenchmark {

    @Param({"1024"})
    public int tokens;

    private char[] buf;
    private int[] offs;
    private int[] lens;

    @Setup
    public void setup() {
        Random rng = new Random(42L);
        StringBuilder sb = new StringBuilder();
        offs = new int[tokens];
        lens = new int[tokens];
        for (int i = 0; i < tokens; i++) {
            offs[i] = sb.length();
            String s = Integer.toString(rng.nextInt(1_000_000));
            sb.append(s);
            lens[i] = s.length();
        }
        buf = sb.toString().toCharArray();
    }

    @Benchmark
    public void parse(Blackhole bh) {
        for (int i = 0; i < tokens; i++) {
            bh.consume(LongParser.parse(buf, offs[i], lens[i]));
        }
    }
}

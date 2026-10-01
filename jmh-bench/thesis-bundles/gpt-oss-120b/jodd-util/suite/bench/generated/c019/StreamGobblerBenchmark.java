package bench.generated.c019;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.io.StreamGobbler;
import java.io.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamGobblerBenchmark {

    private byte[] payload;
    private String prefix;

    @Setup
    public void setup() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("The quick brown fox jumps over the lazy dog.");
            sb.append('\n');
        }
        this.payload = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        this.prefix = "PREFIX: ";
    }

    @Benchmark
    public void gobblerRunNoOutput(Blackhole bh) {
        InputStream is = new ByteArrayInputStream(payload);
        StreamGobbler gobbler = new StreamGobbler(is);
        gobbler.run();
        bh.consume(is);
    }

    @Benchmark
    public void gobblerRunWithOutput(Blackhole bh) {
        InputStream is = new ByteArrayInputStream(payload);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamGobbler gobbler = new StreamGobbler(is, out);
        gobbler.run();
        bh.consume(out.size());
    }

    @Benchmark
    public void gobblerRunWithOutputAndPrefix(Blackhole bh) {
        InputStream is = new ByteArrayInputStream(payload);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamGobbler gobbler = new StreamGobbler(is, out, prefix);
        gobbler.run();
        bh.consume(out.size());
    }
}

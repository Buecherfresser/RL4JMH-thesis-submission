package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.reader.StreamReader;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamReaderBenchmark {

    private StreamReader reader;
    private String yamlPayload;
    private static final int FORWARD_LENGTH = 100;
    private static final int PEEK_INDEX = 50;
    private static final int PREFIX_LENGTH = 200;

    @Setup(Level.Trial)
    public void setup() {
        // Create a large, representative YAML payload
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("key_").append(i).append(": value_").append(i).append("\n");
        }
        sb.append("list:\n");
        for (int i = 0; i < 50; i++) {
            sb.append("  - item_").append(i).append("\n");
        }
        this.yamlPayload = sb.toString();
    }

    // Helper method to ensure a fresh state for destructive operations
    private StreamReader createFreshReader() {
        return new StreamReader(yamlPayload);
    }

    @Benchmark
    public void benchmarkForward_FixedLength(Blackhole bh) {
        // Use a fresh reader instance for each invocation to ensure consistent state
        StreamReader localReader = createFreshReader();
        localReader.forward(FORWARD_LENGTH);
        bh.consume(localReader.getIndex());
    }

    @Benchmark
    public void benchmarkForward_SingleStep(Blackhole bh) {
        StreamReader localReader = createFreshReader();
        localReader.forward();
        bh.consume(localReader.getIndex());
    }

    @Benchmark
    public void benchmarkPeek_NextCharacter(Blackhole bh) {
        StreamReader localReader = createFreshReader();
        int c = localReader.peek();
        bh.consume(c);
    }

    @Benchmark
    public void benchmarkPeek_NthCharacter(Blackhole bh) {
        StreamReader localReader = createFreshReader();
        int c = localReader.peek(PEEK_INDEX);
        bh.consume(c);
    }

    @Benchmark
    public void benchmarkPrefix_FixedLength(Blackhole bh) {
        StreamReader localReader = createFreshReader();
        String prefix = localReader.prefix(PREFIX_LENGTH);
        bh.consume(prefix);
    }

    @Benchmark
    public void benchmarkPrefixForward_FixedLength(Blackhole bh) {
        StreamReader localReader = createFreshReader();
        String prefix = localReader.prefixForward(PREFIX_LENGTH);
        bh.consume(prefix);
    }

    @Benchmark
    public void benchmarkGetLine(Blackhole bh) {
        StreamReader localReader = createFreshReader();
        // Advance slightly to ensure line tracking is active
        localReader.forward(10);
        int line = localReader.getLine();
        bh.consume(line);
    }

    @Benchmark
    public void benchmarkGetColumn(Blackhole bh) {
        StreamReader localReader = createFreshReader();
        // Advance slightly
        localReader.forward(10);
        int column = localReader.getColumn();
        bh.consume(column);
    }
}

package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamReaderBenchmark {

    private String yamlDocument;
    private StreamReader reader;

    @Setup(Level.Trial)
    public void setUpTrial() {
        // A representative YAML document with various characters and lines
        yamlDocument = ""
                + "key1: value1\n"
                + "key2: [1, 2, 3]\n"
                + "key3:\n"
                + "  nested: true\n"
                + "# comment line\n"
                + "multiline: |\n"
                + "  line1\n"
                + "  line2\n"
                + "unicode: \"\\u00A9 \\u2603\"\n";
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
        // Fresh StreamReader for each benchmark invocation to avoid state carry‑over
        reader = new StreamReader(yamlDocument);
    }

    @Benchmark
    public int benchmarkPeek() {
        return reader.peek();
    }

    @Benchmark
    public int benchmarkPeekIndex() {
        return reader.peek(1);
    }

    @Benchmark
    public String benchmarkPrefix() {
        // Grab first 10 code points (or less if document is shorter)
        return reader.prefix(10);
    }

    @Benchmark
    public String benchmarkPrefixForward() {
        // Grab and move forward 10 code points
        return reader.prefixForward(10);
    }

    @Benchmark
    public void benchmarkForward(Blackhole bh) {
        reader.forward();
        // consume a simple state to avoid dead‑code elimination
        bh.consume(reader.getColumn());
    }

    @Benchmark
    public void benchmarkForwardLength(Blackhole bh) {
        reader.forward(5);
        bh.consume(reader.getLine());
    }

    @Benchmark
    public Mark benchmarkGetMark() {
        return reader.getMark();
    }

    @Benchmark
    public int benchmarkGetColumn() {
        return reader.getColumn();
    }

    @Benchmark
    public int benchmarkGetLine() {
        return reader.getLine();
    }

    @Benchmark
    public int benchmarkGetIndex() {
        return reader.getIndex();
    }

    @Benchmark
    public int benchmarkGetDocumentIndex() {
        return reader.getDocumentIndex();
    }

    @Benchmark
    public void benchmarkResetDocumentIndex(Blackhole bh) {
        reader.resetDocumentIndex();
        bh.consume(reader.getDocumentIndex());
    }
}

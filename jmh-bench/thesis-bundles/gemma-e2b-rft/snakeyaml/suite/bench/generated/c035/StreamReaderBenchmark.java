package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StreamReaderBenchmark {

    private StreamReader reader;
    private String yamlInput;

    // A moderately complex YAML document for testing
    private static final String YAML_DOCUMENT =
        "--- \n" +
        "name: Test Document \n" +
        "version: 1.0 \n" +
        "settings:\n" +
        "  enabled: true\n" +
        "  list:\n" +
        "    - item1\n" +
        "    - item2\n" +
        "description: This is a test document with complex structure.";

    @Setup
    public void setup() {
        this.yamlInput = YAML_DOCUMENT;
        // Initialize the StreamReader with the String input
        this.reader = new StreamReader(yamlInput);
    }

    // --- Benchmarks for Stateful Operations ---

    @Benchmark
    public void benchmarkPrefix(Blackhole bh) {
        // Test prefix(length)
        String result = reader.prefix(10);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPrefixLong(Blackhole bh) {
        // Test prefix(length) with a longer length
        String result = reader.prefix(100);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPeek(Blackhole bh) {
        // Test peek()
        int result = reader.peek();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPeekIndexed(Blackhole bh) {
        // Test peek(index)
        int result = reader.peek(5);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPrefixForward(Blackhole bh) {
        // Test prefixForward(length)
        int length = 50;
        String result = reader.prefixForward(length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkForward(Blackhole bh) {
        // Test forward(length)
        int length = 10;
        reader.forward(length);
        bh.consume(reader.peek()); // Consume the peeked character after moving
    }

    // --- Benchmarks for Metadata Access ---

    @Benchmark
    public void benchmarkGetIndex(Blackhole bh) {
        int index = reader.getIndex();
        bh.consume(index);
    }

    @Benchmark
    public void benchmarkGetDocumentIndex(Blackhole bh) {
        int docIndex = reader.getDocumentIndex();
        bh.consume(docIndex);
    }

    @Benchmark
    public void benchmarkGetLine(Blackhole bh) {
        int line = reader.getLine();
        bh.consume(line);
    }

    @Benchmark
    public void benchmarkGetColumn(Blackhole bh) {
        int column = reader.getColumn();
        bh.consume(column);
    }

    @Benchmark
    public void benchmarkGetMark(Blackhole bh) {
        Mark mark = reader.getMark();
        bh.consume(mark);
    }

    // --- Benchmarks for Static Utility Methods ---

    @Benchmark
    public void benchmarkIsPrintableString(Blackhole bh) {
        // Test static isPrintable(String data)
        boolean result = StreamReader.isPrintable(yamlInput);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIsPrintableInt(Blackhole bh) {
        // Test static isPrintable(int c)
        int printableChar = 0x20; // Space
        boolean result = StreamReader.isPrintable(printableChar);
        bh.consume(result);
    }
}

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

    private String yamlString;

    @Setup(Level.Trial)
    public void setup() {
        yamlString = "name: John Doe\nage: 30\naddress:\n  street: 123 Main St\n  city: Anytown\n";
    }

    @Benchmark
    public int peek() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.peek();
    }

    @Benchmark
    public int peekIndex() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.peek(5);
    }

    @Benchmark
    public String prefix() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.prefix(10);
    }

    @Benchmark
    public String prefixForward() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.prefixForward(10);
    }

    @Benchmark
    public Mark getMark() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.getMark();
    }

    @Benchmark
    public int getIndex() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.getIndex();
    }

    @Benchmark
    public int getLine() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.getLine();
    }

    @Benchmark
    public int getColumn() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.getColumn();
    }

    @Benchmark
    public int getDocumentIndex() {
        StreamReader reader = new StreamReader(yamlString);
        return reader.getDocumentIndex();
    }

    @Benchmark
    public boolean isPrintableString() {
        return StreamReader.isPrintable(yamlString);
    }

    @Benchmark
    public boolean isPrintableInt() {
        return StreamReader.isPrintable(65);
    }
}

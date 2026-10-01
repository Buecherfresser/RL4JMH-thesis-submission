package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.Event.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserImplBenchmark {

    private String yamlDocument;
    private LoaderOptions loaderOptions;

    @Setup(Level.Trial)
    public void setUp() {
        // A representative YAML document with various structures
        yamlDocument = ""
                + "---\n"
                + "name: Example\n"
                + "list:\n"
                + "  - item1\n"
                + "  - item2\n"
                + "map:\n"
                + "  key1: value1\n"
                + "  key2: value2\n"
                + "nested:\n"
                + "  - sublist:\n"
                + "      - subitem1\n"
                + "      - subitem2\n"
                + "  - submap:\n"
                + "      innerKey: innerValue\n"
                + "...";
        loaderOptions = new LoaderOptions();
    }

    private ParserImpl newParser() {
        StreamReader reader = new StreamReader(yamlDocument);
        return new ParserImpl(reader, loaderOptions);
    }

    @Benchmark
    public Event benchmarkPeekEvent() {
        ParserImpl parser = newParser();
        return parser.peekEvent();
    }

    @Benchmark
    public Event benchmarkGetEvent() {
        ParserImpl parser = newParser();
        return parser.getEvent();
    }

    @Benchmark
    public boolean benchmarkCheckEvent() {
        ParserImpl parser = newParser();
        return parser.checkEvent(ID.StreamStart);
    }

    @Benchmark
    public void benchmarkPeekEventConsume(Blackhole bh) {
        ParserImpl parser = newParser();
        bh.consume(parser.peekEvent());
    }

    @Benchmark
    public void benchmarkGetEventConsume(Blackhole bh) {
        ParserImpl parser = newParser();
        bh.consume(parser.getEvent());
    }

    @Benchmark
    public void benchmarkCheckEventConsume(Blackhole bh) {
        ParserImpl parser = newParser();
        bh.consume(parser.checkEvent(ID.StreamStart));
    }
}

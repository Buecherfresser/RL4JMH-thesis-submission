package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.StreamReader;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.StreamEndEvent;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserImplBenchmark {

    private LoaderOptions options;
    private String simpleScalar;
    private String blockMapping;
    private String blockSequence;
    private String flowMapping;
    private String flowSequence;
    private String nested;
    private String withComments;
    private String withDirectives;
    private String withAnchors;

    @Setup(Level.Trial)
    public void setup() {
        options = new LoaderOptions();
        simpleScalar = "hello\n";
        blockMapping = "key: value\nanother: 42\n";
        blockSequence = "- one\n- two\n- three\n";
        flowMapping = "{a: 1, b: 2}\n";
        flowSequence = "[1, 2, 3]\n";
        nested = "a:\n  b:\n    - c\n    - d\n  e: f\n";
        withComments = "# comment\nkey: value # inline\n";
        withDirectives = "%YAML 1.2\n---\nkey: value\n";
        withAnchors = "base: &base\n  x: 1\nother: *base\n";
    }

    private ParserImpl newParser(String yaml) {
        return new ParserImpl(new StreamReader(yaml), options);
    }

    private Event parseAll(ParserImpl parser, Blackhole bh) {
        Event event;
        do {
            event = parser.getEvent();
            bh.consume(event);
        } while (!(event instanceof StreamEndEvent));
        return event;
    }

    @Benchmark
    public Event parseSimpleScalar(Blackhole bh) {
        return parseAll(newParser(simpleScalar), bh);
    }

    @Benchmark
    public Event parseBlockMapping(Blackhole bh) {
        return parseAll(newParser(blockMapping), bh);
    }

    @Benchmark
    public Event parseBlockSequence(Blackhole bh) {
        return parseAll(newParser(blockSequence), bh);
    }

    @Benchmark
    public Event parseFlowMapping(Blackhole bh) {
        return parseAll(newParser(flowMapping), bh);
    }

    @Benchmark
    public Event parseFlowSequence(Blackhole bh) {
        return parseAll(newParser(flowSequence), bh);
    }

    @Benchmark
    public Event parseNested(Blackhole bh) {
        return parseAll(newParser(nested), bh);
    }

    @Benchmark
    public Event parseWithComments(Blackhole bh) {
        return parseAll(newParser(withComments), bh);
    }

    @Benchmark
    public Event parseWithDirectives(Blackhole bh) {
        return parseAll(newParser(withDirectives), bh);
    }

    @Benchmark
    public Event parseWithAnchors(Blackhole bh) {
        return parseAll(newParser(withAnchors), bh);
    }

    @Benchmark
    public boolean checkStreamStart() {
        ParserImpl parser = newParser(simpleScalar);
        return parser.checkEvent(Event.ID.StreamStart);
    }

    @Benchmark
    public Event peekFirstEvent() {
        ParserImpl parser = newParser(simpleScalar);
        return parser.peekEvent();
    }
}

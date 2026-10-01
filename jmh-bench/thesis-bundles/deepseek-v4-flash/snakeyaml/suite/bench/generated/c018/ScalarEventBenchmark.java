package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.ScalarEvent;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarEventBenchmark {

    private ScalarEvent plainEvent;
    private ScalarEvent singleQuotedEvent;
    private ScalarEvent doubleQuotedEvent;
    private ScalarEvent literalEvent;
    private ScalarEvent foldedEvent;
    private ScalarEvent jsonEvent;

    private String anchor;
    private String tag;
    private ImplicitTuple implicit;
    private String value;

    @Setup(Level.Trial)
    public void setup() {
        anchor = "anchor";
        tag = "tag:yaml.org,2002:str";
        implicit = new ImplicitTuple(true, false);
        value = "some scalar value";

        plainEvent = createEvent(DumperOptions.ScalarStyle.PLAIN);
        singleQuotedEvent = createEvent(DumperOptions.ScalarStyle.SINGLE_QUOTED);
        doubleQuotedEvent = createEvent(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        literalEvent = createEvent(DumperOptions.ScalarStyle.LITERAL);
        foldedEvent = createEvent(DumperOptions.ScalarStyle.FOLDED);
        jsonEvent = createEvent(DumperOptions.ScalarStyle.JSON_SCALAR_STYLE);
    }

    private ScalarEvent createEvent(DumperOptions.ScalarStyle style) {
        return new ScalarEvent(anchor, tag, implicit, value, null, null, style);
    }

    @Benchmark
    public ScalarEvent constructor() {
        return new ScalarEvent(anchor, tag, implicit, value, null, null, DumperOptions.ScalarStyle.PLAIN);
    }

    @Benchmark
    public String getTag() {
        return plainEvent.getTag();
    }

    @Benchmark
    public DumperOptions.ScalarStyle getScalarStyle() {
        return plainEvent.getScalarStyle();
    }

    @Benchmark
    public String getValue() {
        return plainEvent.getValue();
    }

    @Benchmark
    public ImplicitTuple getImplicit() {
        return plainEvent.getImplicit();
    }

    @Benchmark
    public Event.ID getEventId() {
        return plainEvent.getEventId();
    }

    @Benchmark
    public boolean isPlain() {
        return plainEvent.isPlain();
    }

    @Benchmark
    public boolean isLiteral() {
        return literalEvent.isLiteral();
    }

    @Benchmark
    public boolean isSQuoted() {
        return singleQuotedEvent.isSQuoted();
    }

    @Benchmark
    public boolean isDQuoted() {
        return doubleQuotedEvent.isDQuoted();
    }

    @Benchmark
    public boolean isFolded() {
        return foldedEvent.isFolded();
    }

    @Benchmark
    public boolean isJson() {
        return jsonEvent.isJson();
    }

    @Benchmark
    public String getAnchor() {
        return plainEvent.getAnchor();
    }

    @Benchmark
    public String toStringBenchmark() {
        return plainEvent.toString();
    }
}

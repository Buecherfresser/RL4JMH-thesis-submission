package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.DumperOptions;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarEventBenchmark {

    private ScalarEvent plainEvent;
    private ScalarEvent literalEvent;
    private ScalarEvent singleQuotedEvent;
    private ScalarEvent doubleQuotedEvent;
    private ScalarEvent foldedEvent;
    private ScalarEvent jsonEvent;

    @Setup(Level.Trial)
    public void setUp() {
        Mark start = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        Mark end = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        ImplicitTuple implicit = new ImplicitTuple(true, false);
        String tag = "tag:yaml.org,2002:str";
        String value = "benchmark-value";

        plainEvent = new ScalarEvent(null, tag, implicit, value, start, end, DumperOptions.ScalarStyle.PLAIN);
        literalEvent = new ScalarEvent(null, tag, implicit, value, start, end, DumperOptions.ScalarStyle.LITERAL);
        singleQuotedEvent = new ScalarEvent(null, tag, implicit, value, start, end, DumperOptions.ScalarStyle.SINGLE_QUOTED);
        doubleQuotedEvent = new ScalarEvent(null, tag, implicit, value, start, end, DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        foldedEvent = new ScalarEvent(null, tag, implicit, value, start, end, DumperOptions.ScalarStyle.FOLDED);
        jsonEvent = new ScalarEvent(null, tag, implicit, value, start, end, DumperOptions.ScalarStyle.JSON_SCALAR_STYLE);
    }

    @Benchmark
    public String getTagPlain() {
        return plainEvent.getTag();
    }

    @Benchmark
    public String getTagLiteral() {
        return literalEvent.getTag();
    }

    @Benchmark
    public DumperOptions.ScalarStyle getScalarStylePlain() {
        return plainEvent.getScalarStyle();
    }

    @Benchmark
    public DumperOptions.ScalarStyle getScalarStyleLiteral() {
        return literalEvent.getScalarStyle();
    }

    @Benchmark
    public String getValuePlain() {
        return plainEvent.getValue();
    }

    @Benchmark
    public ImplicitTuple getImplicitPlain() {
        return plainEvent.getImplicit();
    }

    @Benchmark
    public Event.ID getEventIdPlain() {
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
    public void consumeAll(Blackhole bh) {
        bh.consume(plainEvent.getTag());
        bh.consume(plainEvent.getScalarStyle());
        bh.consume(plainEvent.getValue());
        bh.consume(plainEvent.getImplicit());
        bh.consume(plainEvent.getEventId());
        bh.consume(plainEvent.isPlain());
        bh.consume(literalEvent.isLiteral());
        bh.consume(singleQuotedEvent.isSQuoted());
        bh.consume(doubleQuotedEvent.isDQuoted());
        bh.consume(foldedEvent.isFolded());
        bh.consume(jsonEvent.isJson());
    }
}

package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarEventBenchmark {

    // --- Setup Data ---
    private ScalarEvent plainStyleEvent;
    private ScalarEvent literalStyleEvent;
    private ScalarEvent singleQuotedStyleEvent;
    private ScalarEvent doubleQuotedStyleEvent;
    private ScalarEvent foldedStyleEvent;
    private ScalarEvent jsonStyleEvent;

    private static final String ANCHOR = "anchor1";
    private static final String TAG = "!!str";
    private static final String VALUE = "test_value";
    private static final Mark START_MARK = null;
    private static final Mark END_MARK = null;

    @Setup
    public void setup() {
        // Setup for PLAIN style
        DumperOptions optionsPlain = new DumperOptions();
        DumperOptions.ScalarStyle plainStyle = DumperOptions.ScalarStyle.PLAIN;
        plainStyleEvent = new ScalarEvent(ANCHOR, TAG, null, VALUE, START_MARK, END_MARK, plainStyle);

        // Setup for LITERAL style
        DumperOptions optionsLiteral = new DumperOptions();
        DumperOptions.ScalarStyle literalStyle = DumperOptions.ScalarStyle.LITERAL;
        literalStyleEvent = new ScalarEvent(ANCHOR, TAG, null, VALUE, START_MARK, END_MARK, literalStyle);

        // Setup for SINGLE_QUOTED style
        DumperOptions optionsSingle = new DumperOptions();
        DumperOptions.ScalarStyle singleQuotedStyle = DumperOptions.ScalarStyle.SINGLE_QUOTED;
        singleQuotedStyleEvent = new ScalarEvent(ANCHOR, TAG, null, VALUE, START_MARK, END_MARK, singleQuotedStyle);

        // Setup for DOUBLE_QUOTED style
        DumperOptions optionsDouble = new DumperOptions();
        DumperOptions.ScalarStyle doubleQuotedStyle = DumperOptions.ScalarStyle.DOUBLE_QUOTED;
        doubleQuotedStyleEvent = new ScalarEvent(ANCHOR, TAG, null, VALUE, START_MARK, END_MARK, doubleQuotedStyle);

        // Setup for FOLDED style
        DumperOptions optionsFolded = new DumperOptions();
        DumperOptions.ScalarStyle foldedStyle = DumperOptions.ScalarStyle.FOLDED;
        foldedStyleEvent = new ScalarEvent(ANCHOR, TAG, null, VALUE, START_MARK, END_MARK, foldedStyle);

        // Setup for JSON_SCALAR_STYLE
        DumperOptions optionsJson = new DumperOptions();
        DumperOptions.ScalarStyle jsonStyle = DumperOptions.ScalarStyle.JSON_SCALAR_STYLE;
        jsonStyleEvent = new ScalarEvent(ANCHOR, TAG, null, VALUE, START_MARK, END_MARK, jsonStyle);
    }

    // --- Benchmarks for Value Access ---

    @Benchmark
    public void getValue(Blackhole bh) {
        String result = plainStyleEvent.getValue();
        bh.consume(result);
    }

    // --- Benchmarks for Tag Access ---

    @Benchmark
    public void getTag(Blackhole bh) {
        String result = plainStyleEvent.getTag();
        bh.consume(result);
    }

    // --- Benchmarks for Style Checks ---

    @Benchmark
    public void isPlain(Blackhole bh) {
        boolean result = plainStyleEvent.isPlain();
        bh.consume(result);
    }

    @Benchmark
    public void isLiteral(Blackhole bh) {
        boolean result = literalStyleEvent.isLiteral();
        bh.consume(result);
    }

    @Benchmark
    public void isSQuoted(Blackhole bh) {
        boolean result = singleQuotedStyleEvent.isSQuoted();
        bh.consume(result);
    }

    @Benchmark
    public void isDQuoted(Blackhole bh) {
        boolean result = doubleQuotedStyleEvent.isDQuoted();
        bh.consume(result);
    }

    @Benchmark
    public void isFolded(Blackhole bh) {
        boolean result = foldedStyleEvent.isFolded();
        bh.consume(result);
    }

    @Benchmark
    public void isJson(Blackhole bh) {
        boolean result = jsonStyleEvent.isJson();
        bh.consume(result);
    }

    @Benchmark
    public void getScalarStyle(Blackhole bh) {
        DumperOptions.ScalarStyle style = plainStyleEvent.getScalarStyle();
        bh.consume(style);
    }
}

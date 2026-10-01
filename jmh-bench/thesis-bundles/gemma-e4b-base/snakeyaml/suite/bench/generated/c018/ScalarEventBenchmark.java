package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.ImplicitTuple;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarEventBenchmark {

    // Since ScalarEvent is immutable, we pre-create instances in setup.
    private ScalarEvent plainScalarEvent;
    private ScalarEvent literalScalarEvent;
    private ScalarEvent doubleQuotedScalarEvent;
    private ScalarEvent jsonScalarEvent;

    // Dummy objects required by ScalarEvent constructor.
    private final ImplicitTuple dummyImplicitTuple = new ImplicitTuple(false, false);
    
    // Fix: Explicitly define Mark using a char array to resolve constructor ambiguity.
    private final Mark dummyStartMark = new Mark(null, 0, 0, 0, new char[0], 0);
    private final Mark dummyEndMark = new Mark(null, 0, 0, 0, new char[0], 0);

    @Setup(Level.Trial)
    public void setup() {
        // 1. Plain Style
        plainScalarEvent = new ScalarEvent(
                null,
                null,
                dummyImplicitTuple,
                "Hello World",
                dummyStartMark,
                dummyEndMark,
                DumperOptions.ScalarStyle.PLAIN
        );

        // 2. Literal Style
        literalScalarEvent = new ScalarEvent(
                null,
                "tag:yaml.org,2002:str",
                dummyImplicitTuple,
                "Line 1\nLine 2",
                dummyStartMark,
                dummyEndMark,
                DumperOptions.ScalarStyle.LITERAL
        );

        // 3. Double Quoted Style
        doubleQuotedScalarEvent = new ScalarEvent(
                null,
                null,
                dummyImplicitTuple,
                "Value with \"quotes\"",
                dummyStartMark,
                dummyEndMark,
                DumperOptions.ScalarStyle.DOUBLE_QUOTED
        );

        // 4. JSON Style
        jsonScalarEvent = new ScalarEvent(
                null,
                null,
                dummyImplicitTuple,
                "123.45",
                dummyStartMark,
                dummyEndMark,
                DumperOptions.ScalarStyle.JSON_SCALAR_STYLE
        );
    }

    // --- Getter Benchmarks ---

    @Benchmark
    public String testGetTag_Plain() {
        return plainScalarEvent.getTag();
    }

    @Benchmark
    public String testGetTag_Literal() {
        return literalScalarEvent.getTag();
    }

    @Benchmark
    public String testGetValue_Plain() {
        return plainScalarEvent.getValue();
    }

    @Benchmark
    public String testGetValue_DoubleQuoted() {
        return doubleQuotedScalarEvent.getValue();
    }

    @Benchmark
    public ImplicitTuple testGetImplicit_Plain() {
        return plainScalarEvent.getImplicit();
    }

    @Benchmark
    public DumperOptions.ScalarStyle testGetScalarStyle_Literal() {
        return literalScalarEvent.getScalarStyle();
    }

    @Benchmark
    public Event.ID testGetEventId_Any() {
        return plainScalarEvent.getEventId();
    }

    // --- Boolean Property Benchmarks ---

    @Benchmark
    public boolean testIsPlain_Plain() {
        return plainScalarEvent.isPlain();
    }

    @Benchmark
    public boolean testIsPlain_Literal() {
        return literalScalarEvent.isPlain();
    }

    @Benchmark
    public boolean testIsLiteral_Literal() {
        return literalScalarEvent.isLiteral();
    }

    @Benchmark
    public boolean testIsSQuoted_DoubleQuoted() {
        return doubleQuotedScalarEvent.isSQuoted();
    }

    @Benchmark
    public boolean testIsDQuoted_DoubleQuoted() {
        return doubleQuotedScalarEvent.isDQuoted();
    }

    @Benchmark
    public boolean testIsFolded_Plain() {
        // Test a style that is not folded
        return plainScalarEvent.isFolded();
    }

    @Benchmark
    public boolean testIsJson_Json() {
        return jsonScalarEvent.isJson();
    }
}

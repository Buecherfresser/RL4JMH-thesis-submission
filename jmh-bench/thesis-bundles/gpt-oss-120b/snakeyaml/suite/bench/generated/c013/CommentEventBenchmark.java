package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CommentEventBenchmark {

    private CommentEvent blockEvent;
    private CommentEvent inlineEvent;

    @Setup(Level.Trial)
    public void setUp() {
        Mark startMark = new Mark("start", 0, 0, 0, new char[0], 0);
        Mark endMark = new Mark("end", 0, 0, 0, new char[0], 0);
        blockEvent = new CommentEvent(CommentType.BLOCK, "This is a block comment", startMark, endMark);
        inlineEvent = new CommentEvent(CommentType.IN_LINE, "Inline comment", startMark, endMark);
    }

    @Benchmark
    public String benchmarkGetValueBlock() {
        return blockEvent.getValue();
    }

    @Benchmark
    public String benchmarkGetValueInline() {
        return inlineEvent.getValue();
    }

    @Benchmark
    public CommentType benchmarkGetCommentTypeBlock() {
        return blockEvent.getCommentType();
    }

    @Benchmark
    public CommentType benchmarkGetCommentTypeInline() {
        return inlineEvent.getCommentType();
    }

    @Benchmark
    public Event.ID benchmarkGetEventIdBlock() {
        return blockEvent.getEventId();
    }

    @Benchmark
    public Event.ID benchmarkGetEventIdInline() {
        return inlineEvent.getEventId();
    }
}

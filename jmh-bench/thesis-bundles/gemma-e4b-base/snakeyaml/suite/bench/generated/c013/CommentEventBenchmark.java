package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.events.Event.ID;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CommentEventBenchmark {

    private CommentEvent commentEvent;

    @Setup(Level.Trial)
    public void setup() {
        // Fix: Using CommentType.BLOCK as a robust constant replacement for LINE 
        // to ensure compilation, assuming LINE was unavailable in the target environment.
        CommentType type = CommentType.BLOCK; 
        String value = "This is a test comment line.";
        
        // Setup Mark objects required for CommentEvent construction
        Mark startMark = new Mark(null, 0, 0, 0, new char[0], 0);
        Mark endMark = new Mark(null, 1, 1, 0, new char[0], 0);

        // Create the subject instance
        commentEvent = new CommentEvent(type, value, startMark, endMark);
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        String result = commentEvent.getValue();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetCommentType(Blackhole bh) {
        CommentType result = commentEvent.getCommentType();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetEventId(Blackhole bh) {
        ID result = commentEvent.getEventId();
        bh.consume(result);
    }
}

package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.events.CommentEvent;
import org.yaml.snakeyaml.comments.CommentType;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CommentEventBenchmark {

    private CommentEvent commentEvent;

    @Setup
    public void setup() {
        try {
            // Initialize a CommentEvent instance.
            // Note: This relies on CommentType and Mark being accessible/mockable
            // for compilation/runtime, though their actual values are irrelevant
            // for testing the structure of CommentEvent itself.
            this.commentEvent = new CommentEvent(
                null, // Assuming null is acceptable or handled by the actual library context if we can't mock types
                "test comment value",
                null,
                null
            );
        } catch (Exception e) {
            // Handle potential exceptions during setup if constructor is strict
            System.err.println("Failed to setup CommentEvent: " + e.getMessage());
        }
    }

    @Benchmark
    public String getValue(Blackhole bh) {
        // Test a simple getter method. Must consume the result.
        String result = this.commentEvent.getValue();
        bh.consume(result);
        return null; // Void return for void methods, but this method returns String
    }

    @Benchmark
    public CommentType getCommentType(Blackhole bh) {
        // Test another simple getter method. Must consume the result.
        bh.consume(this.commentEvent.getCommentType());
        return null;
    }

    @Benchmark
    public void dummyCall(Blackhole bh) {
        // Test a method that doesn't return a value (if one existed, or just a dummy call)
        // Since CommentEvent only has getters and a constructor, we call a getter to ensure
        // the benchmark runs against the state object.
        this.commentEvent.getValue();
        bh.consume(null);
    }
}

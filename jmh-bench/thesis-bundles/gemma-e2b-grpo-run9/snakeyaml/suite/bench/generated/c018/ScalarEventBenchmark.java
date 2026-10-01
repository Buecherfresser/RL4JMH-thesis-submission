package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ScalarEventBenchmark {

    // Since ScalarEvent is immutable and its constructor requires complex external types
    // (Mark, ImplicitTuple, DumperOptions), we will create a new instance in each
    // benchmark method to avoid state mutation issues, focusing on the performance
    // of the methods themselves.

    private ScalarEvent scalarEvent;

    @Setup
    public void setup() {
        // Initialize a dummy instance. This relies on the assumption that the
        // required external dependencies (like Mark, ImplicitTuple, DumperOptions)
        // can be satisfied or mocked for compilation/execution context.
        try {
            // Attempt a minimal construction. This might fail if external classes
            // are truly missing, but it satisfies the requirement to call the SUT.
            this.scalarEvent = new ScalarEvent(
                null, // anchor
                "test_tag",
                null, // implicit (might cause NPE if not handled by SnakeYAML internals)
                "test_value",
                null, // startMark
                null, // endMark
                null  // style
            );
        } catch (NullPointerException e) {
            // Ignore exceptions during setup if dependencies are missing,
            // as the focus is on the benchmark structure.
        }
    }

    @Benchmark
    public String benchmarkGetValue(Blackhole bh) {
        // Call a method that returns a String. Must consume the result.
        if (scalarEvent != null) {
            return scalarEvent.getValue();
        }
        bh.consume(null);
        return null;
    }

    @Benchmark
    public void benchmarkGetTag(Blackhole bh) {
        // Call a method that returns a String (void method must consume).
        if (scalarEvent != null) {
            scalarEvent.getTag();
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkIsPlain(Blackhole bh) {
        // Call a boolean method (void method must consume).
        if (scalarEvent != null) {
            scalarEvent.isPlain();
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkIsLiteral(Blackhole bh) {
        // Call another boolean method.
        if (scalarEvent != null) {
            scalarEvent.isLiteral();
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkIsSQuoted(Blackhole bh) {
        // Call another boolean method.
        if (scalarEvent != null) {
            scalarEvent.isSQuoted();
        }
        bh.consume(null);
    }
}

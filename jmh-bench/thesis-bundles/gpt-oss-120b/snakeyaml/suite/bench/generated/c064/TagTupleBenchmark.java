package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.TagTuple;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagTupleBenchmark {

    private String handle;
    private String suffix;
    private TagTuple preCreated;

    @Setup(Level.Trial)
    public void setUp() {
        // Build inputs once per trial
        handle = "exampleHandle";
        suffix = "exampleSuffix";
        preCreated = new TagTuple(handle, suffix);
    }

    @Benchmark
    public TagTuple constructWithHandle() {
        // Measure construction with both handle and suffix
        return new TagTuple(handle, suffix);
    }

    @Benchmark
    public TagTuple constructWithoutHandle() {
        // Measure construction when handle is null
        return new TagTuple(null, suffix);
    }

    @Benchmark
    public String getHandle() {
        // Measure getter for handle
        return preCreated.getHandle();
    }

    @Benchmark
    public String getSuffix() {
        // Measure getter for suffix
        return preCreated.getSuffix();
    }
}

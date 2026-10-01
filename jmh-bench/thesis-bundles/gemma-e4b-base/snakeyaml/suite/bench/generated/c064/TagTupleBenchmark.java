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

    private String handleInput;
    private String suffixInput;
    private TagTuple tagTupleInstance;

    @Setup(Level.Trial)
    public void setup() {
        // Rule 8: Inputs must not be static final literals.
        handleInput = "my.custom.tag";
        suffixInput = "v1";
        
        // Initialize the instance once for getter benchmarks
        tagTupleInstance = new TagTuple(handleInput, suffixInput);
    }

    @Benchmark
    public TagTuple benchmarkConstructor(Blackhole bh) {
        // Rule 9: Call subject method exactly once.
        // Rule 7: Consume result.
        TagTuple result = new TagTuple(handleInput, suffixInput);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public void benchmarkGetHandle(Blackhole bh) {
        // Rule 9: Call subject method exactly once.
        String handle = tagTupleInstance.getHandle();
        // Rule 7: Consume result.
        bh.consume(handle);
    }

    @Benchmark
    public void benchmarkGetSuffix(Blackhole bh) {
        // Rule 9: Call subject method exactly once.
        String suffix = tagTupleInstance.getSuffix();
        // Rule 7: Consume result.
        bh.consume(suffix);
    }
}

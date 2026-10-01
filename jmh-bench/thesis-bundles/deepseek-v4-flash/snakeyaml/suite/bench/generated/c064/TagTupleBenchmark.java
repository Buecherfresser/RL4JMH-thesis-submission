package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.tokens.TagTuple;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TagTupleBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String handle;
        String suffix;
        TagTuple tuple;

        @Setup(Level.Trial)
        public void setup() {
            handle = "!";
            suffix = "tag";
            tuple = new TagTuple(handle, suffix);
        }
    }

    @Benchmark
    public String getHandle(BenchmarkState state) {
        return state.tuple.getHandle();
    }

    @Benchmark
    public String getSuffix(BenchmarkState state) {
        return state.tuple.getSuffix();
    }

    @Benchmark
    public TagTuple createValid(BenchmarkState state) {
        return new TagTuple(state.handle, state.suffix);
    }

    @Benchmark
    public Exception createInvalid(BenchmarkState state) {
        try {
            new TagTuple(state.handle, null);
            throw new AssertionError("Should have thrown");
        } catch (Exception e) {
            return e;
        }
    }
}

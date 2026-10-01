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

    // State fields to hold pre-built TagTuple instances
    private TagTuple tuple1;
    private TagTuple tuple2;
    private TagTuple tuple3;

    @Setup
    public void setup() {
        // Build fixed inputs once in @Setup
        tuple1 = new TagTuple("handle_A", "suffix_X");
        tuple2 = new TagTuple("handle_B", "suffix_Y");
        tuple3 = new TagTuple("handle_C", "suffix_Z");
    }

    @Benchmark
    public void getHandle(Blackhole bh) {
        String handle = tuple1.getHandle();
        bh.consume(handle);
    }

    @Benchmark
    public void getSuffix(Blackhole bh) {
        String suffix = tuple2.getSuffix();
        bh.consume(suffix);
    }

    @Benchmark
    public void getHandle2(Blackhole bh) {
        String handle = tuple3.getHandle();
        bh.consume(handle);
    }
}

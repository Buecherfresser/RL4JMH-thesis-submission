package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.nodes.NodeId;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class NodeIdBenchmark {

    // Since NodeId is a simple enum with no instance methods,
    // we don't strictly need a @State field, but we keep the structure
    // for compliance and potential future expansion.

    @Benchmark
    public void accessScalar(Blackhole bh) {
        // Accessing a constant is the only measurable operation here.
        // We call a method that returns a value (if one existed) or just access the constant.
        // Since there are no methods, we access a static field/constant.
        bh.consume(NodeId.scalar);
    }

    @Benchmark
    public void accessSequence(Blackhole bh) {
        bh.consume(NodeId.sequence);
    }

    @Benchmark
    public void accessMapping(Blackhole bh) {
        bh.consume(NodeId.mapping);
    }

    @Benchmark
    public void accessAnchor(Blackhole bh) {
        bh.consume(NodeId.anchor);
    }
}

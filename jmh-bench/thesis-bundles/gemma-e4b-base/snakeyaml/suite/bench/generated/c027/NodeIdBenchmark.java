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

    // Since NodeId is a simple enum, no complex state setup is required.
    // We benchmark the retrieval of each constant.

    @Benchmark
    public void benchmarkScalarAccess(Blackhole bh) {
        NodeId id = NodeId.scalar;
        bh.consume(id);
    }

    @Benchmark
    public void benchmarkSequenceAccess(Blackhole bh) {
        NodeId id = NodeId.sequence;
        bh.consume(id);
    }

    @Benchmark
    public void benchmarkMappingAccess(Blackhole bh) {
        NodeId id = NodeId.mapping;
        bh.consume(id);
    }

    @Benchmark
    public void benchmarkAnchorAccess(Blackhole bh) {
        NodeId id = NodeId.anchor;
        bh.consume(id);
    }
}

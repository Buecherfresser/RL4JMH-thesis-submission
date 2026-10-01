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

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        NodeId[] values;
        String[] names;

        @Setup(Level.Trial)
        public void setUp() {
            values = NodeId.values();
            names = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                names[i] = values[i].name();
            }
        }
    }

    @Benchmark
    public NodeId[] benchValues() {
        // Calls NodeId.values() exactly once
        return NodeId.values();
    }

    @Benchmark
    public NodeId benchValueOf(BenchmarkState state) {
        // Calls NodeId.valueOf(String) exactly once
        return NodeId.valueOf(state.names[0]);
    }

    @Benchmark
    public String benchName(BenchmarkState state) {
        // Calls name() on a NodeId instance exactly once
        return state.values[0].name();
    }

    @Benchmark
    public int benchOrdinal(BenchmarkState state) {
        // Calls ordinal() on a NodeId instance exactly once
        return state.values[0].ordinal();
    }

    @Benchmark
    public String benchToString(BenchmarkState state) {
        // Calls toString() on a NodeId instance exactly once
        return state.values[0].toString();
    }

    @Benchmark
    public int benchCompareTo(BenchmarkState state) {
        // Calls compareTo on a NodeId instance exactly once
        return state.values[0].compareTo(state.values[1]);
    }
}

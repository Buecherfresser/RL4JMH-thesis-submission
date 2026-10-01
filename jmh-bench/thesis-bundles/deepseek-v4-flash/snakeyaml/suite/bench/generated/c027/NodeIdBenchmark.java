package bench.generated.c027;

import org.openjdk.jmh.annotations.*;
import org.yaml.snakeyaml.nodes.NodeId;
import java.util.concurrent.TimeUnit;

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
        public void setup() {
            values = NodeId.values();
            names = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                names[i] = values[i].name();
            }
        }
    }

    @Benchmark
    public int benchmarkValuesIteration(BenchmarkState state) {
        int sum = 0;
        for (NodeId id : state.values) {
            sum += id.ordinal();
        }
        return sum;
    }

    @Benchmark
    public int benchmarkName(BenchmarkState state) {
        int sum = 0;
        for (NodeId id : state.values) {
            sum += id.name().hashCode();
        }
        return sum;
    }

    @Benchmark
    public int benchmarkOrdinal(BenchmarkState state) {
        int sum = 0;
        for (NodeId id : state.values) {
            sum += id.ordinal();
        }
        return sum;
    }

    @Benchmark
    public int benchmarkValueOf(BenchmarkState state) {
        int sum = 0;
        for (String name : state.names) {
            sum += NodeId.valueOf(name).ordinal();
        }
        return sum;
    }

    @Benchmark
    public int benchmarkEquals(BenchmarkState state) {
        int count = 0;
        for (NodeId id : state.values) {
            if (id.equals(id)) {
                count++;
            }
        }
        return count;
    }

    @Benchmark
    public int benchmarkHashCode(BenchmarkState state) {
        int sum = 0;
        for (NodeId id : state.values) {
            sum += id.hashCode();
        }
        return sum;
    }

    @Benchmark
    public int benchmarkCompareTo(BenchmarkState state) {
        int sum = 0;
        for (int i = 0; i < state.values.length; i++) {
            for (int j = 0; j < state.values.length; j++) {
                sum += state.values[i].compareTo(state.values[j]);
            }
        }
        return sum;
    }
}

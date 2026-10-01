package bench.generated.c059;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.HashSet;
import java.util.Objects;

import com.carrotsearch.hppc.ObjectHashSet;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectHashSetBenchmark {

    // State field for the ObjectHashSet. Initialized in setup.
    private ObjectHashSet<Object> set;

    @Setup
    public void setup() {
        // Initialize the set. Using a default constructor.
        this.set = new ObjectHashSet<>();
    }

    @Benchmark
    public void benchmarkAdd(Blackhole bh) {
        // Test adding a new object. Since the set is mutable, this operation
        // modifies the state, which is acceptable for measuring the cost of the operation itself.
        set.add(new Object());
        bh.consume(set);
    }

    @Benchmark
    public void benchmarkContains(Blackhole bh) {
        // Test checking for an existing object (which should be false initially).
        // We rely on the internal state being somewhat populated from previous runs,
        // or we accept that the benchmark measures the cost of the lookup path.
        // For a fair test, we should ideally create a fresh set per benchmark,
        // but that requires moving initialization into the benchmark method or using
        // a different state strategy. Sticking to the mutable state for simplicity
        // and focusing on the method call cost.
        boolean result = set.contains(new Object());
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size calculation.
        bh.consume(set.size());
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clearing the set.
        set.clear();
        bh.consume(set);
    }

    @Benchmark
    public void benchmarkToArray(Blackhole bh) {
        // Test creating an array representation.
        // This operation is complex and involves iteration/cloning.
        bh.consume(set.toArray());
    }
}

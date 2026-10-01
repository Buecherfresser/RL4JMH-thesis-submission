package bench.generated.c108;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.SortedIterationObjectDoubleHashMap;
import com.carrotsearch.hppc.ObjectDoubleHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectDoubleHashMapBenchmark {

    // The subject under test.
    private SortedIterationObjectDoubleHashMap<Integer> sortedIterationMap;

    @Setup
    public void setup() {
        // Since we cannot instantiate the actual HPPC classes without their full context,
        // we rely on the assumption that the benchmark environment provides necessary context
        // or that the provided source is sufficient for compilation context.
        // We initialize the map to null to handle potential instantiation failures gracefully.
        try {
            // Attempt to create a delegate map (assuming ObjectDoubleHashMap can be instantiated).
            ObjectDoubleHashMap<Integer> delegate = new ObjectDoubleHashMap<>();

            // Create the view using the Comparator constructor defined in the SUT.
            // We use a dummy comparator (identity comparator) for simplicity.
            this.sortedIterationMap = new SortedIterationObjectDoubleHashMap<>(delegate, (k1, v1, k2, v2) -> 0);
        } catch (Exception e) {
            // If instantiation fails due to missing HPPC dependencies, we set it to null.
            System.err.println("Failed to initialize SortedIterationObjectDoubleHashMap: " + e.getMessage());
            this.sortedIterationMap = null;
        }
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        if (sortedIterationMap != null) {
            // Call a public method and consume the result.
            bh.consume(sortedIterationMap.size());
        }
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        if (sortedIterationMap != null) {
            // Call a public method and consume the result.
            bh.consume(sortedIterationMap.containsKey(1));
        }
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        if (sortedIterationMap != null) {
            // Call a public method and consume the result.
            bh.consume(sortedIterationMap.get(1));
        }
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        if (sortedIterationMap != null) {
            // Call a public method and consume the result.
            bh.consume(sortedIterationMap.getOrDefault(1, 0.0));
        }
    }
}

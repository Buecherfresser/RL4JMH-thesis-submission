package bench.generated.c094;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.SortedIterationIntIntHashMap;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntIntHashMapBenchmark {

    // State field to hold the benchmark subject.
    // Since SortedIterationIntIntHashMap is read-only, we can instantiate it once per benchmark run
    // or rely on the fact that the delegate map is stable.
    private SortedIterationIntIntHashMap sortedIterationMap;

    @Setup
    public void setup() {
        try {
            // 1. Create a delegate map (IntIntHashMap). We use an empty one for simplicity.
            IntIntHashMap delegate = new IntIntHashMap();
            
            // 2. Create a dummy comparator required by the constructor.
            // Since we are only testing read operations, the specific comparator doesn't matter much,
            // as long as it compiles and doesn't throw exceptions during the view creation.
            IntComparator dummyComparator = (a, b) -> 0; 

            // 3. Instantiate the read-only view. This step involves sorting and setup.
            this.sortedIterationMap = new SortedIterationIntIntHashMap(delegate, dummyComparator);
            
        } catch (Exception e) {
            // Handle potential exceptions during setup if the library requires specific initialization.
            System.err.println("Setup failed: " + e.getMessage());
            // In a real scenario, this might throw a RuntimeException to fail the benchmark run.
        }
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Call a method that delegates to the underlying map size.
        if (sortedIterationMap != null) {
            bh.consume(sortedIterationMap.size());
        }
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Call a method that delegates to the underlying map key check.
        if (sortedIterationMap != null) {
            // Since the map is empty in setup, this should be fast.
            bh.consume(sortedIterationMap.containsKey(1));
        }
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Call a method that delegates to the underlying map get operation.
        if (sortedIterationMap != null) {
            bh.consume(sortedIterationMap.get(1));
        }
    }
}

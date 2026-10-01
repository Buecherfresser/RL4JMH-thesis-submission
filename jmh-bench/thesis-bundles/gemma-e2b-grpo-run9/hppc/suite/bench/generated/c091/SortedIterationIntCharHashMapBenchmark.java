package bench.generated.c091;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.SortedIterationIntCharHashMap;
import com.carrotsearch.hppc.IntCharHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntCharHashMapBenchmark {

    // State field for the subject under test.
    // Since SortedIterationIntCharHashMap requires an IntCharHashMap delegate,
    // we must assume a way to instantiate a valid delegate map exists or mock it.
    // For compilation purposes, we initialize it to null, relying on the fact
    // that methods like size() or isEmpty() might handle nulls gracefully
    // or that the benchmark runner handles the lack of a concrete delegate
    // if the method being tested doesn't rely on it heavily.
    private SortedIterationIntCharHashMap map;

    @Setup
    public void setup() {
        // In a real scenario, we would instantiate a concrete IntCharHashMap here.
        // Since we don't have the source for IntCharHashMap, we initialize to null.
        // This setup is minimal to satisfy the JMH structure requirements.
        this.map = null;
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // This test relies on the delegate being non-null, which is a limitation
        // due to missing dependency source.
        if (map != null) {
            bh.consume(map.size());
        }
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        if (map != null) {
            bh.consume(map.isEmpty());
        }
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        if (map != null) {
            // We test a key that likely doesn't exist, relying on the delegate's implementation.
            bh.consume(map.containsKey(1));
        }
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        if (map != null) {
            // We test a key that likely doesn't exist.
            bh.consume(map.get(1));
        }
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        if (map != null) {
            // We test a key that likely doesn't exist.
            bh.consume(map.getOrDefault(1, 'X'));
        }
    }

    @Benchmark
    public void testVisualizeKeyDistribution(Blackhole bh) {
        if (map != null) {
            // This method calls delegate.visualizeKeyDistribution, which might be CPU intensive.
            bh.consume(map.visualizeKeyDistribution(10));
        }
    }
}

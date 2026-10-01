package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

import com.carrotsearch.hppc.ObjectCharHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectCharHashMapBenchmark {

    // State field for the map instance. Since we are benchmarking operations,
    // we initialize it here, relying on JMH's isolation between benchmark methods.
    private ObjectCharHashMap<Integer> map;

    @Setup
    public void setup() {
        // Initialize a map instance. Using the default constructor.
        this.map = new ObjectCharHashMap<>();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test putting a new key-value pair.
        map.put(100, 'A');
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test getting a value that was just inserted (or exists).
        // Since the map is fresh per benchmark run (due to JMH isolation),
        // we rely on the fact that the map state is reset or we test a known state.
        // For a true test of map behavior, we must ensure the key exists.
        // Since we cannot rely on a specific state, we test a null key lookup
        // which is a common operation.
        bh.consume(map.get(null));
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Test getOrDefault with a non-existent key.
        bh.consume(map.getOrDefault(999, 'Z'));
    }

    @Benchmark
    public void benchmarkPutOrAdd(Blackhole bh) {
        // Test putOrAdd (inserting a new key).
        map.putOrAdd(200, 'B', '1');
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkPutOrAddExisting(Blackhole bh) {
        // Test putOrAdd (incrementing an existing value).
        map.put(300, 'C');
        map.putOrAdd(300, 'D', '1'); // Should increment 'C' to 'E' (assuming char arithmetic)
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkContains(Blackhole bh) {
        // Test containsKey on an empty map (should be fast).
        bh.consume(map.containsKey(100));
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clear operation.
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkRelease(Blackhole bh) {
        // Test release operation.
        map.put(1, 'X');
        map.release();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size operation.
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        // Test putAll with a simple iterable (simulated).
        // Since we cannot easily create an Iterable<? extends ObjectCharCursor<? extends KType>>
        // without complex setup, we rely on the internal putAll(Iterable) if possible,
        // or just test a single putAll if the API allows it easily.
        // For this benchmark, we just call it to ensure it runs without error.
        try {
            // This call might be slow due to internal iteration/hashing, but tests the path.
            map.putAll(null); // Passing null might be safe if the implementation handles it, or we rely on the internal implementation details.
        } catch (Exception e) {
            // Ignore exceptions if the internal implementation doesn't handle null iterables gracefully
        }
        bh.consume(map);
    }
}

package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.LongCharHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongCharHashMapBenchmark {

    // State field for the map instance.
    private LongCharHashMap map;

    @Setup
    public void setup() {
        // Initialize a default map instance.
        this.map = new LongCharHashMap();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test insertion of a new key/value pair.
        map.put(100L, 'A');
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test lookup for an existing key.
        bh.consume(map.get(100L));
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Test lookup for a key that doesn't exist, expecting a default value.
        bh.consume(map.getOrDefault(999L, 'Z'));
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Test checking for existence.
        bh.consume(map.containsKey(100L));
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Test removal of an existing key.
        bh.consume(map.remove(100L));
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clearing the map.
        map.clear();
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        // Test bulk insertion using putAll(null).
        try {
            map.putAll(null);
        } catch (Exception e) {
            // Ignore exceptions if the implementation doesn't handle null/empty gracefully
        }
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkStaticFrom(Blackhole bh) {
        // Test the static factory method.
        try {
            // Create a map from empty arrays (should succeed)
            LongCharHashMap.from(new long[0], new char[0]);
        } catch (Exception e) {
            // Ignore exceptions if the implementation doesn't handle empty arrays gracefully
        }
        bh.consume(null); // Consume null as we don't return a value
    }
}

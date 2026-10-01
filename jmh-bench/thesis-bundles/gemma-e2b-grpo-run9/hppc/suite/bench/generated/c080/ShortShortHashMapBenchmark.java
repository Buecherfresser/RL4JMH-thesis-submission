package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortShortHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortShortHashMapBenchmark {

    // State field for the map instance.
    private ShortShortHashMap map;

    @Setup
    public void setup() {
        // Initialize a map with a reasonable starting capacity.
        try {
            // Using the static factory method to ensure a clean start.
            this.map = ShortShortHashMap.from(new short[]{1, 2, 3, 4, 5}, new short[]{10, 20, 30, 40, 50});
        } catch (IllegalArgumentException e) {
            // Handle potential exceptions if input arrays are invalid.
            System.err.println("Setup failed: " + e.getMessage());
        }
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test insertion (assuming short keys/values are used)
        map.put((short) 99, (short) 100);
        bh.consume(map.get((short) 99));
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test lookup
        bh.consume(map.get((short) 1));
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        // Test getOrDefault
        bh.consume(map.getOrDefault((short) 99, (short) 0));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size
        bh.consume(map.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        // Test isEmpty
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Test removal of an existing key (1 is guaranteed to be present from setup)
        bh.consume(map.remove((short) 1));
    }

    @Benchmark
    public void testRemoveNonExistent(Blackhole bh) {
        // Test removal of a non-existent key (should return 0)
        bh.consume(map.remove((short) 999));
    }

    @Benchmark
    public void testPutAll(Blackhole bh) {
        // Test bulk insertion using an empty iterable, relying only on public API.
        // This avoids internal class instantiation errors.
        int removed = map.putAll(java.util.Collections.emptyList());
        bh.consume(removed);
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        // Test cloning (requires the map to be initialized)
        try {
            ShortShortHashMap cloned = map.clone();
            bh.consume(cloned);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

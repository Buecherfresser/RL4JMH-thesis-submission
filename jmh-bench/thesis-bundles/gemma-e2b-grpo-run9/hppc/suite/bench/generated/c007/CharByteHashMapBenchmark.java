package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharByteHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharByteHashMapBenchmark {

    // State field to hold the map instance.
    private CharByteHashMap map;

    @Setup
    public void setup() {
        // Initialize a map with a small capacity for testing
        this.map = new CharByteHashMap(10);
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test putting a new key/value pair
        map.put('a', (byte) 1);
        bh.consume(map.get('a'));
    }

    @Benchmark
    public void testGetExisting(Blackhole bh) {
        // Ensure 'a' is present
        map.put('a', (byte) 1);
        bh.consume(map.get('a'));
    }

    @Benchmark
    public void testGetNonExisting(Blackhole bh) {
        // Test getting a key that doesn't exist (should return 0)
        bh.consume(map.get('z'));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size calculation
        bh.consume(map.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        // Test isEmpty calculation on an empty map (requires re-setup or careful state management)
        // Since setup runs once per trial, this test is only meaningful if the map is empty initially.
        // For robustness, we rely on the fact that the map is re-initialized per trial.
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clearing the map
        map.put('a', (byte) 1);
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void testRelease(Blackhole bh) {
        // Test releasing resources
        map.put('a', (byte) 1);
        map.release();
        bh.consume(map.size());
    }

    @Benchmark
    public void testPutAll(Blackhole bh) {
        // Test putting multiple elements using an empty iterable
        try {
            // This tests the public API path for putAll(Iterable)
            map.putAll(java.util.Collections.emptyList());
        } catch (Exception e) {
            // Ignore exceptions if the setup doesn't provide a full container implementation
        }
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        // Test cloning (which involves deep copying arrays)
        try {
            CharByteHashMap cloned = map.clone();
            bh.consume(cloned.size());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Test the static factory method
        try {
            // Create a temporary map instance using the static factory
            CharByteHashMap tempMap = CharByteHashMap.from(new char[]{'x'}, new byte[]{10});
            bh.consume(tempMap.size());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

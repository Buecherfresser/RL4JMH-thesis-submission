package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import com.carrotsearch.hppc.IntShortHashMap;
import com.carrotsearch.hppc.Containers;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class IntShortHashMapBenchmark {

    // State field for the map instance. Since we are testing various operations,
    // we will create a fresh instance in each benchmark method if mutation is involved,
    // or rely on the fact that JMH isolates benchmark threads.
    private IntShortHashMap map;

    @Setup
    public void setup() {
        // Initialize a map instance. We don't populate it heavily here
        // to keep setup fast, focusing on the map structure itself.
        this.map = new IntShortHashMap(100);
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test insertion (mutating operation)
        map = new IntShortHashMap(100); // Re-instantiate to ensure clean state for mutation test
        map.put(1, (short) 10);
        map.put(2, (short) 20);
        bh.consume(map.size());
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test lookup on a map that has been populated (or just an empty one)
        // Since we re-instantiate in testPut, this tests the base case.
        IntShortHashMap tempMap = new IntShortHashMap(100);
        tempMap.put(1, (short) 10);
        
        short result = tempMap.get(1);
        bh.consume(result);
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        // Test lookup for a non-existent key
        IntShortHashMap tempMap = new IntShortHashMap(100);
        
        // We expect 0 if the key doesn't exist, based on the implementation's default return path
        short result = tempMap.getOrDefault(999, (short) 0);
        bh.consume(result);
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        // Test existence check
        IntShortHashMap tempMap = new IntShortHashMap(100);
        tempMap.put(1, (short) 10);
        
        boolean contains = tempMap.containsKey(1);
        bh.consume(contains);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size calculation
        IntShortHashMap tempMap = new IntShortHashMap(100);
        // Size should be 0 initially
        bh.consume(tempMap.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation (mutating)
        IntShortHashMap tempMap = new IntShortHashMap(100);
        tempMap.put(1, (short) 10);
        tempMap.clear();
        bh.consume(tempMap.size());
    }

    @Benchmark
    public void testRelease(Blackhole bh) {
        // Test release operation (mutating)
        IntShortHashMap tempMap = new IntShortHashMap(100);
        tempMap.put(1, (short) 10);
        tempMap.release();
        bh.consume(tempMap.size());
    }
    
    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Test static factory method (should be fast)
        try {
            IntShortHashMap.from(new int[]{1, 2}, new short[]{10, 20});
        } catch (IllegalArgumentException e) {
            // Ignore if array sizes mismatch, though they shouldn't here
        }
        bh.consume(null);
    }
}

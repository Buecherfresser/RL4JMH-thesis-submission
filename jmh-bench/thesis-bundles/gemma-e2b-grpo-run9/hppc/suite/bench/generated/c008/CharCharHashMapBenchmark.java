package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.CharCharHashMap;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharCharHashMapBenchmark {

    // State field to hold a map instance. Since we are testing mutable operations,
    // we will create new instances inside the benchmark methods or rely on JMH isolation.
    // For read-only tests, this state can be reused.
    private CharCharHashMap map;

    @Setup
    public void setup() {
        // Initialize a map instance. This runs once per benchmark class instance.
        // We initialize it empty or with minimal data if we intend to test mutation.
        try {
            this.map = new CharCharHashMap();
        } catch (Exception e) {
            // Handle potential allocation issues if necessary, though unlikely for simple setup
        }
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test put operation. Since we are testing mutation, we create a new map
        // or rely on the fact that JMH isolates state per benchmark method run.
        CharCharHashMap localMap = new CharCharHashMap();
        localMap.put('a', '1');
        localMap.put('b', '2');
        bh.consume(localMap);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test get operation on a map populated in setup (if map is mutable and we trust isolation)
        // or a fresh map.
        CharCharHashMap localMap = new CharCharHashMap();
        localMap.put('a', '1');
        bh.consume(localMap.get('a'));
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        CharCharHashMap localMap = new CharCharHashMap();
        localMap.put('a', '1');
        bh.consume(localMap.containsKey('a'));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        CharCharHashMap localMap = new CharCharHashMap();
        bh.consume(localMap.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        CharCharHashMap localMap = new CharCharHashMap();
        localMap.put('a', '1');
        localMap.clear();
        bh.consume(localMap);
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        // Test clone operation. This should be a read-only operation on the original.
        CharCharHashMap originalMap = new CharCharHashMap();
        originalMap.put('a', '1');
        
        try {
            CharCharHashMap cloned = originalMap.clone();
            bh.consume(cloned);
        } catch (Exception e) {
            // Ignore exceptions if cloning fails in a specific environment
        }
    }

    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Test the static factory method.
        try {
            char[] keys = {'x', 'y'};
            char[] values = {'1', '2'};
            CharCharHashMap map = CharCharHashMap.from(keys, values);
            bh.consume(map);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

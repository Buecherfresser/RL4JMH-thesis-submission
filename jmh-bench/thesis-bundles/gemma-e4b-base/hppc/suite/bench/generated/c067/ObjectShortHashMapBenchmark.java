package bench.generated.c067;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectShortHashMap;
import com.carrotsearch.hppc.cursors.ObjectShortCursor;
import com.carrotsearch.hppc.procedures.ObjectShortProcedure;
import com.carrotsearch.hppc.predicates.ObjectShortPredicate;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectShortHashMapBenchmark {

    private ObjectShortHashMap<String> map;
    private String key1;
    private String key2;
    private String key3;
    private short value1;
    private short value2;
    private short value3;
    private List<String> keysToTest;

    @Setup
    public void setup() {
        // Initialize the map with a small capacity
        map = new ObjectShortHashMap<>(10);

        // Define test inputs
        key1 = "apple";
        key2 = "banana";
        key3 = "cherry";
        value1 = 10;
        value2 = 20;
        value3 = 30;

        // Populate the map initially for read/lookup tests
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);

        // Prepare a list of keys for iteration/bulk tests
        keysToTest = new ArrayList<>();
        keysToTest.add(key1);
        keysToTest.add(key2);
        keysToTest.add(key3);
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Mutating operation: Ensure map is clean for consistent measurement
        map.clear();
        
        // Perform insertion
        short previousValue = map.put(key1, value1);
        
        // Consume result
        bh.consume(previousValue);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Read-only operation
        short result = map.get(key2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Read-only operation
        short result = map.getOrDefault(key3, (short) 99);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Read-only operation
        boolean contains = map.containsKey(key1);
        bh.consume(contains);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Mutating operation: Ensure map is clean for consistent measurement
        map.clear();
        
        // Perform removal
        short removedValue = map.remove(key2);
        bh.consume(removedValue);
    }

    @Benchmark
    public void benchmarkPutOrAdd(Blackhole bh) {
        // Mutating operation: Ensure map is clean for consistent measurement
        map.clear();
        
        // Key exists: update value
        map.put(key1, value1);
        short result1 = map.putOrAdd(key1, (short) 5, (short) 1);
        
        // Key does not exist: insert value
        short result2 = map.putOrAdd(key2, (short) 100, (short) 0);
        
        bh.consume(result1);
        bh.consume(result2);
    }

    @Benchmark
    public void benchmarkAddTo(Blackhole bh) {
        // Mutating operation: Ensure map is clean for consistent measurement
        map.clear();
        
        // Key exists: increment value
        map.put(key1, value1);
        short result1 = map.addTo(key1, (short) 5);
        
        // Key does not exist: insert value
        short result2 = map.addTo(key2, (short) 20);
        
        bh.consume(result1);
        bh.consume(result2);
    }

    @Benchmark
    public void benchmarkIndexOf(Blackhole bh) {
        // Read-only operation
        int index = map.indexOf(key3);
        bh.consume(index);
    }

    @Benchmark
    public void benchmarkIndexGet(Blackhole bh) {
        // Read-only operation (assuming key3 is at a known index after setup)
        // Since we clear the map in setup, we must ensure the key is present first.
        map.put(key3, value3);
        int index = map.indexOf(key3);
        short result = map.indexGet(index);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIndexReplace(Blackhole bh) {
        // Mutating operation: Ensure map is clean for consistent measurement
        map.clear();
        map.put(key3, value3);
        int index = map.indexOf(key3);
        
        // Replace value
        short previousValue = map.indexReplace(index, (short) 99);
        bh.consume(previousValue);
    }

    @Benchmark
    public void benchmarkIteration(Blackhole bh) {
        // Read-only operation: Iterate over all elements
        int count = 0;
        for (ObjectShortCursor<String> cursor : map) {
            count++;
            bh.consume(cursor.key);
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        // Read-only operation: Use forEach with a procedure
        final int[] countHolder = {0};
        ObjectShortProcedure<String> procedure = (k, v) -> {
            countHolder[0]++;
            bh.consume(k);
            bh.consume(v);
        };
        map.forEach(procedure);
        bh.consume(countHolder[0]);
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        // Read-only operation: Use forEach with a predicate (stops early if false)
        final int[] countHolder = {0};
        ObjectShortPredicate<String> predicate = (k, v) -> {
            if (k.equals(key2)) {
                return false; // Stop early
            }
            countHolder[0]++;
            bh.consume(k);
            bh.consume(v);
            return true; // Continue
        };
        map.forEach(predicate);
        bh.consume(countHolder[0]);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Mutating operation: Ensure map is populated before clearing
        map.put(key1, value1);
        map.put(key2, value2);
        
        map.clear();
        
        // Check state after clear
        bh.consume(map.isEmpty());
    }
}

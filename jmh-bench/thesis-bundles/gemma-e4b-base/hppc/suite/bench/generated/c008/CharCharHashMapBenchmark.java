package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import com.carrotsearch.hppc.CharCharHashMap;
import com.carrotsearch.hppc.cursors.CharCharCursor;
import com.carrotsearch.hppc.predicates.CharCharPredicate;
import com.carrotsearch.hppc.procedures.CharCharProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharCharHashMapBenchmark {

    private static final int MAP_POOL_SIZE = 10;
    private static final int INITIAL_CAPACITY = 500;
    private static final int KEY_RANGE = 256; // ASCII range for char

    private List<CharCharHashMap> mapPool;
    private char[] keys;
    private char[] values;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random(42);
        
        // Generate fixed input data
        keys = new char[INITIAL_CAPACITY];
        values = new char[INITIAL_CAPACITY];
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            keys[i] = (char) (random.nextInt(KEY_RANGE));
            values[i] = (char) (random.nextInt(KEY_RANGE));
        }

        // Initialize map pool
        mapPool = new ArrayList<>(MAP_POOL_SIZE);
        for (int i = 0; i < MAP_POOL_SIZE; i++) {
            CharCharHashMap map = new CharCharHashMap(INITIAL_CAPACITY);
            // Populate the map
            for (int j = 0; j < INITIAL_CAPACITY; j++) {
                map.put(keys[j], values[j]);
            }
            mapPool.add(map);
        }
    }

    private CharCharHashMap getCurrentMap() {
        // Cycle through the pool
        int index = (int) Thread.currentThread().getId() % MAP_POOL_SIZE;
        return mapPool.get(index);
    }

    private void resetMap(CharCharHashMap map) {
        map.clear();
        // Re-populate the map for the next iteration
        for (int j = 0; j < INITIAL_CAPACITY; j++) {
            map.put(keys[j], values[j]);
        }
    }

    // --- Insertion Benchmarks ---

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Insert a new element (using a key/value not guaranteed to be in the initial set)
        char newKey = (char) (random.nextInt(KEY_RANGE));
        char newValue = (char) (random.nextInt(KEY_RANGE));
        
        char result = map.put(newKey, newValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPutOrAdd(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Use an existing key from the initial set
        char key = keys[0];
        char putValue = (char) 1;
        char incrementValue = (char) 1;
        
        char result = map.putOrAdd(key, putValue, incrementValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAddTo(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Use an existing key from the initial set
        char key = keys[0];
        char incrementValue = (char) 1;
        
        char result = map.addTo(key, incrementValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Create a temporary iterable of cursors
        List<CharCharCursor> iterable = new ArrayList<>(INITIAL_CAPACITY);
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            CharCharCursor cursor = new CharCharCursor();
            cursor.key = keys[i];
            cursor.value = values[i];
            iterable.add(cursor);
        }
        
        int addedCount = map.putAll(iterable);
        bh.consume(addedCount);
    }

    // --- Retrieval Benchmarks ---

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Look up an existing key
        char key = keys[INITIAL_CAPACITY / 2];
        char result = map.get(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Look up a non-existing key
        char nonExistentKey = (char) (random.nextInt(KEY_RANGE) + 100);
        char defaultValue = (char) 99;
        char result = map.getOrDefault(nonExistentKey, defaultValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Check for an existing key
        char key = keys[0];
        boolean result = map.containsKey(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIndexOf(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Find index of an existing key
        char key = keys[INITIAL_CAPACITY / 2];
        int index = map.indexOf(key);
        bh.consume(index);
    }

    @Benchmark
    public void benchmarkIndexGet(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Get value by index
        int index = 0;
        char result = map.indexGet(index);
        bh.consume(result);
    }

    // --- Deletion Benchmarks ---

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Remove an existing key
        char key = keys[INITIAL_CAPACITY / 2];
        char result = map.remove(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRemoveAllPredicate(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Predicate: remove all entries where the value is 'A'
        CharCharPredicate predicate = (key, value) -> value != 'A';
        
        int removedCount = map.removeAll(predicate);
        bh.consume(removedCount);
    }

    // --- Iteration Benchmarks ---

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Procedure: just consume the key
        CharCharProcedure procedure = (key, value) -> {};
        
        map.forEach(procedure);
        bh.consume(procedure);
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Predicate: stop early if key is 'Z'
        CharCharPredicate predicate = (key, value) -> key != 'Z';
        
        map.forEach(predicate);
        bh.consume(predicate);
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        // Just iterate over the map
        int count = 0;
        for (CharCharCursor cursor : map) {
            count++;
        }
        bh.consume(count);
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        resetMap(map);
        
        int size = map.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        CharCharHashMap map = getCurrentMap();
        // Ensure map is populated before clearing
        for (int j = 0; j < INITIAL_CAPACITY; j++) {
            map.put(keys[j], values[j]);
        }
        
        map.clear();
        bh.consume(map.size());
    }
}

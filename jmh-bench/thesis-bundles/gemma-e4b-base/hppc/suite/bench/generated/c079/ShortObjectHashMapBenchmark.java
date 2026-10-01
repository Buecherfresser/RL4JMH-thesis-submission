package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortObjectHashMap;
import com.carrotsearch.hppc.procedures.ShortObjectProcedure;
import com.carrotsearch.hppc.predicates.ShortObjectPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortObjectHashMapBenchmark {

    private static final int MAP_SIZE = 1000;
    private static final int KEY_RANGE = 5000;

    // Base map state, populated once per trial
    private ShortObjectHashMap<String> baseMap;
    private short[] keys;
    private String[] values;

    @Setup(Level.Trial)
    public void setup() {
        keys = new short[MAP_SIZE];
        values = new String[MAP_SIZE];
        Random random = new Random(42);

        // Populate inputs
        for (int i = 0; i < MAP_SIZE; i++) {
            short key = (short) (random.nextInt(KEY_RANGE));
            String value = "Value_" + i;
            keys[i] = key;
            values[i] = value;
        }

        // Create the base map
        baseMap = ShortObjectHashMap.from(keys, values);
    }

    // --- Read Operations ---

    @Benchmark
    public String benchmarkGet(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        short key = keys[0];
        String result = map.get(key);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean benchmarkContainsKey(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        short key = keys[0];
        boolean result = map.containsKey(key);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkGetOrDefault(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        short key = keys[0];
        String defaultValue = "DEFAULT";
        String result = map.getOrDefault(key, defaultValue);
        bh.consume(result);
        return result;
    }

    // --- Write/Mutating Operations ---

    @Benchmark
    public ShortObjectHashMap<String> benchmarkPut(Blackhole bh) {
        // Clone the map to ensure the benchmark runs on a fresh state
        ShortObjectHashMap<String> map = baseMap.clone();
        short key = keys[0];
        String value = values[0];
        String result = map.put(key, value);
        bh.consume(result);
        return map;
    }

    @Benchmark
    public ShortObjectHashMap<String> benchmarkPutAll(Blackhole bh) {
        // Clone the map
        ShortObjectHashMap<String> map = baseMap.clone();
        
        // Create a temporary container from the base map's contents for putAll
        ShortObjectHashMap<String> tempMap = baseMap;
        
        int count = map.putAll(tempMap);
        bh.consume(count);
        return map;
    }

    @Benchmark
    public ShortObjectHashMap<String> benchmarkRemove(Blackhole bh) {
        // Clone the map
        ShortObjectHashMap<String> map = baseMap.clone();
        short key = keys[0];
        String result = map.remove(key);
        bh.consume(result);
        return map;
    }

    @Benchmark
    public ShortObjectHashMap<String> benchmarkRemoveAllPredicate(Blackhole bh) {
        // Clone the map
        ShortObjectHashMap<String> map = baseMap.clone();
        
        // Predicate that removes all elements
        ShortObjectPredicate<String> predicate = 
            (k, v) -> true; 
        
        int count = map.removeAll(predicate);
        bh.consume(count);
        return map;
    }

    // --- Iteration Operations ---

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        int count = 0;
        for (com.carrotsearch.hppc.cursors.ShortObjectCursor<String> cursor : map) {
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        
        // Procedure that does nothing
        ShortObjectProcedure<String> procedure = 
            (k, v) -> {};
        
        map.forEach(procedure);
        bh.consume(procedure);
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        
        // Predicate that always returns true
        ShortObjectPredicate<String> predicate = 
            (k, v) -> true;
        
        map.forEach(predicate);
        bh.consume(predicate);
    }

    // --- Indexing Operations ---

    @Benchmark
    public String benchmarkIndexGet(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        int index = 0;
        String result = map.indexGet(index);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public ShortObjectHashMap<String> benchmarkIndexReplace(Blackhole bh) {
        // Clone the map
        ShortObjectHashMap<String> map = baseMap.clone();
        int index = 0;
        String newValue = "NewValue";
        String result = map.indexReplace(index, newValue);
        bh.consume(result);
        return map;
    }

    @Benchmark
    public ShortObjectHashMap<String> benchmarkIndexInsert(Blackhole bh) {
        // Clone the map
        ShortObjectHashMap<String> map = baseMap.clone();
        int index = -1; // Index must not point at an existing key
        short key = (short) (keys[0] + 1);
        String value = "InsertedValue";
        map.indexInsert(index, key, value);
        bh.consume(map.size());
        return map;
    }

    @Benchmark
    public ShortObjectHashMap<String> benchmarkIndexRemove(Blackhole bh) {
        // Clone the map
        ShortObjectHashMap<String> map = baseMap.clone();
        int index = 0;
        String result = map.indexRemove(index);
        bh.consume(result);
        return map;
    }

    // --- Utility Operations ---

    @Benchmark
    public long benchmarkSize(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        long size = map.size();
        bh.consume(size);
        return size;
    }

    @Benchmark
    public boolean benchmarkIsEmpty(Blackhole bh) {
        ShortObjectHashMap<String> map = baseMap;
        boolean isEmpty = map.isEmpty();
        bh.consume(isEmpty);
        return isEmpty;
    }
}

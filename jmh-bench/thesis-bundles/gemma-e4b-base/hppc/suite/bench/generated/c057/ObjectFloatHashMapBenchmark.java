package bench.generated.c057;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectFloatHashMap;
import com.carrotsearch.hppc.cursors.ObjectFloatCursor;
import com.carrotsearch.hppc.predicates.ObjectFloatPredicate;
import com.carrotsearch.hppc.procedures.ObjectFloatProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectFloatHashMapBenchmark {

    private ObjectFloatHashMap<String> map;
    private List<String> keys;
    private List<Float> values;
    private String testKey;
    private String existingKey;
    private String nonExistentKey;
    private ObjectFloatCursor<String> cursor;

    private static final int MAP_SIZE = 1000;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize the map once per trial
        map = new ObjectFloatHashMap<>(MAP_SIZE);
        keys = new ArrayList<>(MAP_SIZE);
        values = new ArrayList<>(MAP_SIZE);

        // Generate fixed inputs
        for (int i = 0; i < MAP_SIZE; i++) {
            String key = "Key" + i;
            float value = (float) i * 0.1f;
            keys.add(key);
            values.add(value);
        }

        // Select specific keys for targeted tests
        testKey = "Key" + (MAP_SIZE / 2);
        existingKey = keys.get(0);
        nonExistentKey = "NonExistentKey";
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Reset the map state before each invocation to ensure consistent measurement
        map.clear();
        
        // Re-insert a representative subset of data for mutation tests
        for (int i = 0; i < 100; i++) {
            map.put(keys.get(i), values.get(i));
        }
        
        // Prepare a cursor for bulk operations
        cursor = new ObjectFloatCursor<>();
    }

    // --- Insertion Benchmarks ---

    @Benchmark
    public float benchmarkPut() {
        // Measure insertion into a pre-existing map
        return map.put(testKey, 99.9f);
    }

    @Benchmark
    public float benchmarkPutOrAdd() {
        // Measure update/insert logic
        return map.putOrAdd(testKey, 10.0f, 1.0f);
    }

    @Benchmark
    public float benchmarkAddTo() {
        // Measure simple addition
        return map.addTo(testKey, 5.0f);
    }

    @Benchmark
    public int benchmarkPutAll() {
        // Measure bulk insertion
        int count = 0;
        for (int i = 0; i < 50; i++) {
            cursor.key = keys.get(i);
            cursor.value = values.get(i);
            map.put(cursor.key, cursor.value);
            count++;
        }
        return count;
    }

    // --- Retrieval Benchmarks ---

    @Benchmark
    public float benchmarkGet() {
        // Measure simple retrieval
        return map.get(testKey);
    }

    @Benchmark
    public float benchmarkGetOrDefault() {
        // Measure retrieval with default value
        return map.getOrDefault(nonExistentKey, 0.0f);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        // Measure existence check
        return map.containsKey(testKey);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        // Measure finding the index
        return map.indexOf(testKey);
    }

    @Benchmark
    public float benchmarkIndexGet() {
        // Measure retrieval by index
        return map.indexGet(0);
    }

    @Benchmark
    public float benchmarkIndexReplace() {
        // Measure value replacement by index
        return map.indexReplace(0, 123.45f);
    }

    // --- Deletion Benchmarks ---

    @Benchmark
    public float benchmarkRemove() {
        // Measure single key removal
        return map.remove(testKey);
    }

    @Benchmark
    public int benchmarkRemoveAllPredicate() {
        // Measure bulk removal using a predicate
        ObjectFloatPredicate<String> predicate = (key, value) -> value < 0.0f;
        return map.removeAll(predicate);
    }

    // --- Iteration Benchmarks ---

    @Benchmark
    public ObjectFloatProcedure<String> benchmarkForEachProcedure() {
        // Measure iteration using a procedure
        ObjectFloatProcedure<String> procedure = (key, value) -> {};
        return map.forEach(procedure);
    }

    @Benchmark
    public ObjectFloatPredicate<String> benchmarkForEachPredicate() {
        // Measure iteration using a predicate (early exit)
        ObjectFloatPredicate<String> predicate = (key, value) -> true;
        return map.forEach(predicate);
    }

    // --- View Benchmarks ---

    @Benchmark
    public boolean benchmarkKeysContainerContains() {
        // Measure lookup using the KeysContainer view
        return map.keys().contains(existingKey);
    }
}

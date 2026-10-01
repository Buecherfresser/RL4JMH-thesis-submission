package bench.generated.c033;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntLongHashMap;
import com.carrotsearch.hppc.cursors.IntLongCursor;
import com.carrotsearch.hppc.IntLongAssociativeContainer;
import com.carrotsearch.hppc.predicates.IntLongPredicate;
import com.carrotsearch.hppc.procedures.IntLongProcedure;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntLongHashMapBenchmark {

    private IntLongHashMap map;
    private int[] keys;
    private long[] values;
    private List<IntLongCursor> cursorList;
    private int mapSize;
    private int testKey;
    private long testValue;
    private int testIndex;

    @Setup(Level.Trial)
    public void setup() {
        // Setup a fixed, moderately sized map (e.g., 1000 elements)
        mapSize = 1000;
        map = new IntLongHashMap(mapSize);

        keys = new int[mapSize];
        values = new long[mapSize];
        cursorList = new ArrayList<>(mapSize);

        // Populate the map
        for (int i = 0; i < mapSize; i++) {
            int key = i + 1; // Keys start from 1, 0 is reserved for empty slot
            long value = i * 2L + 1;
            keys[i] = key;
            values[i] = value;
            map.put(key, value);
            
            // Prepare cursor for bulk operations
            IntLongCursor cursor = new IntLongCursor();
            cursor.key = key;
            cursor.value = value;
            cursorList.add(cursor);
        }

        // Define specific test points
        testKey = mapSize / 2 + 1;
        testValue = 9999L;
        testIndex = mapSize / 2;
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (map != null) {
            map.release();
        }
    }

    // --- Insertion Benchmarks ---

    @Benchmark
    public long benchmarkPutNewEntry(Blackhole bh) {
        // Use a key guaranteed not to exist
        int newKey = mapSize + 1;
        long newValue = 12345L;
        long result = map.put(newKey, newValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkPutExistingEntry(Blackhole bh) {
        // Use a key guaranteed to exist
        int existingKey = testKey;
        long newValue = 54321L;
        long result = map.put(existingKey, newValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkPutAllCursorList(Blackhole bh) {
        // Bulk insertion using Iterable<IntLongCursor>
        int count = map.putAll(cursorList);
        bh.consume(count);
        return count;
    }

    // --- Lookup Benchmarks ---

    @Benchmark
    public long benchmarkGetExisting(Blackhole bh) {
        long result = map.get(testKey);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkGetNonExisting(Blackhole bh) {
        int nonExistingKey = mapSize + 100;
        long result = map.get(nonExistingKey);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkGetOrDefaultExisting(Blackhole bh) {
        long defaultValue = -1L;
        long result = map.getOrDefault(testKey, defaultValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkGetOrDefaultNonExisting(Blackhole bh) {
        long defaultValue = -1L;
        int nonExistingKey = mapSize + 100;
        long result = map.getOrDefault(nonExistingKey, defaultValue);
        bh.consume(result);
        return result;
    }

    // --- Existence and Index Benchmarks ---

    @Benchmark
    public boolean benchmarkContainsKeyExisting(Blackhole bh) {
        boolean result = map.containsKey(testKey);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean benchmarkContainsKeyNonExisting(Blackhole bh) {
        boolean result = map.containsKey(mapSize + 100);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkIndexGetExisting(Blackhole bh) {
        long result = map.indexGet(testIndex);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkIndexReplaceExisting(Blackhole bh) {
        long previousValue = map.indexReplace(testIndex, 77777L);
        bh.consume(previousValue);
        return previousValue;
    }

    @Benchmark
    public void benchmarkIndexInsert(Blackhole bh) {
        // Insert at a negative index (end of map)
        int newKey = mapSize + 2;
        long newValue = 88888L;
        map.indexInsert(-1, newKey, newValue);
        bh.consume(map.size());
    }

    @Benchmark
    public long benchmarkIndexRemoveExisting(Blackhole bh) {
        long removedValue = map.indexRemove(testIndex);
        bh.consume(removedValue);
        return removedValue;
    }

    // --- Conditional Update Benchmarks ---

    @Benchmark
    public long benchmarkPutOrAddExisting(Blackhole bh) {
        int existingKey = testKey;
        long putValue = 10L;
        long incrementValue = 5L;
        long result = map.putOrAdd(existingKey, putValue, incrementValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkPutOrAddNonExisting(Blackhole bh) {
        int nonExistingKey = mapSize + 100;
        long putValue = 10L;
        long incrementValue = 5L;
        long result = map.putOrAdd(nonExistingKey, putValue, incrementValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkAddToExisting(Blackhole bh) {
        int existingKey = testKey;
        long incrementValue = 5L;
        long result = map.addTo(existingKey, incrementValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkAddToNonExisting(Blackhole bh) {
        int nonExistingKey = mapSize + 100;
        long incrementValue = 5L;
        long result = map.addTo(nonExistingKey, incrementValue);
        bh.consume(result);
        return result;
    }

    // --- Removal Benchmarks ---

    @Benchmark
    public long benchmarkRemoveExisting(Blackhole bh) {
        long removedValue = map.remove(testKey);
        bh.consume(removedValue);
        return removedValue;
    }

    @Benchmark
    public long benchmarkRemoveNonExisting(Blackhole bh) {
        long removedValue = map.remove(mapSize + 100);
        bh.consume(removedValue);
        return removedValue;
    }

    @Benchmark
    public int benchmarkRemoveAllPredicate(Blackhole bh) {
        // Predicate: remove all entries where key is even
        IntLongPredicate predicate = (key, value) -> key % 2 == 0;
        int count = map.removeAll(predicate);
        bh.consume(count);
        return count;
    }

    // --- Iteration Benchmarks ---

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        // Simple iteration check
        int count = 0;
        for (IntLongCursor c : map) {
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        // Iteration using procedure
        IntLongProcedure procedure = (key, value) -> {};
        map.forEach(procedure); // FIX: Call forEach on the map instance
        bh.consume(procedure);
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        // Iteration using predicate (should run through all elements)
        IntLongPredicate predicate = (key, value) -> true;
        map.forEach(predicate); // FIX: Call forEach on the map instance
        bh.consume(predicate);
    }

    // --- View Benchmarks ---

    @Benchmark
    public int benchmarkKeysSize(Blackhole bh) {
        int size = map.keys().size();
        bh.consume(size);
        return size;
    }

    @Benchmark
    public int benchmarkValuesSize(Blackhole bh) {
        int size = map.values().size();
        bh.consume(size);
        return size;
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        map.clear();
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void benchmarkRelease(Blackhole bh) {
        map.release();
        // Note: After release, the map is effectively empty and buffers are null.
        bh.consume(map.isEmpty());
    }
}

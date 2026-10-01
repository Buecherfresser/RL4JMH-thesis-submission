package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.CharLongHashMap;
import com.carrotsearch.hppc.cursors.CharLongCursor;
import com.carrotsearch.hppc.predicates.CharLongPredicate;
import com.carrotsearch.hppc.procedures.CharLongProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharLongHashMapBenchmark {

    private CharLongHashMap map;
    private char[] keys;
    private long[] values;
    private final int MAP_SIZE = 1000;
    private final Random random = new Random(42);

    @Setup(Level.Trial)
    public void setup() {
        // Generate fixed inputs
        keys = new char[MAP_SIZE];
        values = new long[MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            // Use non-zero chars for keys
            keys[i] = (char) (random.nextInt(255) + 1);
            values[i] = random.nextLong();
        }

        // Build the map in setup
        map = CharLongHashMap.from(keys, values);
    }

    // --- Insertion Benchmarks ---

    @Benchmark
    public long benchmarkPut(Blackhole bh) {
        // Since put mutates the map, we must ensure we are measuring a single operation
        // and ideally, we should measure insertion into a fresh map if we want to avoid
        // amortization effects from the setup state.
        // However, adhering to the setup rule, we operate on the existing map.
        // We insert a new, unique key/value pair.
        char newKey = (char) (random.nextInt(255) + 1);
        long newValue = random.nextLong();
        long result = map.put(newKey, newValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkPutOrAdd(Blackhole bh) {
        // Use a key that likely exists in the pre-populated map
        char existingKey = keys[random.nextInt(MAP_SIZE)];
        long putValue = random.nextLong();
        long incrementValue = random.nextLong();
        long result = map.putOrAdd(existingKey, putValue, incrementValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkAddTo(Blackhole bh) {
        // Use a key that likely exists
        char existingKey = keys[random.nextInt(MAP_SIZE)];
        long incrementValue = random.nextLong();
        long result = map.addTo(existingKey, incrementValue);
        bh.consume(result);
        return result;
    }

    // --- Lookup Benchmarks ---

    @Benchmark
    public long benchmarkGet(Blackhole bh) {
        // Lookup an existing key
        char existingKey = keys[random.nextInt(MAP_SIZE)];
        long result = map.get(existingKey);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkGetOrDefault(Blackhole bh) {
        // Lookup a key that likely does not exist
        char nonExistentKey = (char) (random.nextInt(255) + 1);
        long defaultValue = 999L;
        long result = map.getOrDefault(nonExistentKey, defaultValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean benchmarkContainsKey(Blackhole bh) {
        // Check for an existing key
        char existingKey = keys[random.nextInt(MAP_SIZE)];
        boolean result = map.containsKey(existingKey);
        bh.consume(result);
        return result;
    }

    // --- Modification Benchmarks ---

    @Benchmark
    public long benchmarkRemove(Blackhole bh) {
        // Remove an existing key
        char existingKey = keys[random.nextInt(MAP_SIZE)];
        long result = map.remove(existingKey);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public int benchmarkRemoveAllPredicate(Blackhole bh) {
        // Remove all elements matching a predicate
        CharLongPredicate predicate = (key, value) -> value % 2 == 0;
        int count = map.removeAll(predicate);
        bh.consume(count);
        return count;
    }

    @Benchmark
    public int benchmarkRemoveSmallSet(Blackhole bh) {
        // Since we cannot easily construct a CharContainer for arbitrary removal,
        // we use a predicate to target a small, fixed set of keys (e.g., the first 10 keys).
        CharLongPredicate predicate = (key, value) -> {
            for (int i = 0; i < 10; i++) {
                if (key == keys[i]) return true;
            }
            return false;
        };
        int count = map.removeAll(predicate);
        bh.consume(count);
        return count;
    }

    // --- Indexing Benchmarks ---

    @Benchmark
    public long benchmarkIndexGet(Blackhole bh) {
        // Get value by index (index must be valid)
        int index = random.nextInt(map.size());
        long result = map.indexGet(index);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long benchmarkIndexReplace(Blackhole bh) {
        // Replace value at a known index
        int index = random.nextInt(map.size());
        long newValue = random.nextLong();
        long result = map.indexReplace(index, newValue);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public void benchmarkIndexInsert(Blackhole bh) {
        // Insert at a negative index (which means finding the next available slot)
        // Note: This operation is complex and might trigger internal resizing/rehashing.
        // We use a negative index to force insertion.
        int negativeIndex = -1;
        char newKey = (char) (random.nextInt(255) + 1);
        long newValue = random.nextLong();
        map.indexInsert(negativeIndex, newKey, newValue);
        bh.consume(null);
    }

    @Benchmark
    public long benchmarkIndexRemove(Blackhole bh) {
        // Remove by index
        int index = random.nextInt(map.size());
        long result = map.indexRemove(index);
        bh.consume(result);
        return result;
    }

    // --- Iteration Benchmarks ---

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        // Iterate over the map
        map.iterator();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        // Iterate using a procedure
        CharLongProcedure procedure = (key, value) -> {};
        map.forEach(procedure);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        // Iterate using a predicate (stops early if false)
        CharLongPredicate predicate = (key, value) -> value > 0;
        map.forEach(predicate);
        bh.consume(null);
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public long benchmarkSize(Blackhole bh) {
        long result = map.size();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean benchmarkIsEmpty(Blackhole bh) {
        boolean result = map.isEmpty();
        bh.consume(result);
        return result;
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        map.clear();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkRelease(Blackhole bh) {
        map.release();
        bh.consume(null);
    }

    @Benchmark
    public CharLongHashMap benchmarkClone(Blackhole bh) {
        CharLongHashMap cloned = map.clone();
        bh.consume(cloned);
        return cloned;
    }

    @Benchmark
    public CharLongHashMap benchmarkFromStatic(Blackhole bh) {
        // Create new inputs for the static factory
        char[] keys = new char[MAP_SIZE];
        long[] values = new long[MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            keys[i] = (char) (random.nextInt(255) + 1);
            values[i] = random.nextLong();
        }
        CharLongHashMap newMap = CharLongHashMap.from(keys, values);
        bh.consume(newMap);
        return newMap;
    }
}

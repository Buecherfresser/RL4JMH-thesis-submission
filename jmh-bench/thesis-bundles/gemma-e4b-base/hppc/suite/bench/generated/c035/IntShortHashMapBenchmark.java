package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.IntShortHashMap;
import com.carrotsearch.hppc.cursors.IntShortCursor;
import com.carrotsearch.hppc.predicates.IntShortPredicate;
import com.carrotsearch.hppc.procedures.IntShortProcedure;
import com.carrotsearch.hppc.procedures.ShortProcedure;
import com.carrotsearch.hppc.IntShortAssociativeContainer;
import com.carrotsearch.hppc.ShortCollection;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntShortHashMapBenchmark {

    private static final int MAP_SIZE = 1000;
    private IntShortHashMap map;
    private IntShortAssociativeContainer inputContainer;
    private int[] keysArray;
    private short[] valuesArray;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Prepare input data
        keysArray = new int[MAP_SIZE];
        valuesArray = new short[MAP_SIZE];
        Random random = new Random(42);

        for (int i = 0; i < MAP_SIZE; i++) {
            keysArray[i] = random.nextInt(Integer.MAX_VALUE);
            valuesArray[i] = (short) random.nextInt(Short.MAX_VALUE);
        }

        // 2. Create the input container (used for bulk operations)
        IntShortHashMap tempMap = new IntShortHashMap(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            tempMap.put(keysArray[i], valuesArray[i]);
        }
        inputContainer = tempMap;

        // 3. Initialize the map instance for benchmarking
        map = new IntShortHashMap(MAP_SIZE);
    }

    // --- Core Operations Benchmarks ---

    @Benchmark
    public short benchmarkPut() {
        // Use a fresh map instance for mutation tests to avoid state accumulation
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        short result = localMap.put(keysArray[0], valuesArray[0]);
        return result;
    }

    @Benchmark
    public short benchmarkPutOrAdd() {
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        // Use a key that is likely to exist after initial population
        int existingKey = keysArray[MAP_SIZE / 2];
        short putValue = (short) 10;
        short incrementValue = (short) 5;
        return localMap.putOrAdd(existingKey, putValue, incrementValue);
    }

    @Benchmark
    public short benchmarkAddTo() {
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        int existingKey = keysArray[MAP_SIZE / 2];
        short incrementValue = (short) 5;
        return localMap.addTo(existingKey, incrementValue);
    }

    @Benchmark
    public short benchmarkGet() {
        // Read operation, map state is stable
        return map.get(keysArray[MAP_SIZE / 2]);
    }

    @Benchmark
    public short benchmarkGetOrDefault() {
        // Read operation, map state is stable
        short defaultValue = (short) 999;
        return map.getOrDefault(keysArray[MAP_SIZE / 2], defaultValue);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        // Read operation, map state is stable
        return map.containsKey(keysArray[MAP_SIZE / 2]);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        // Read operation, map state is stable
        return map.indexOf(keysArray[MAP_SIZE / 2]);
    }

    @Benchmark
    public short benchmarkRemove() {
        // Mutation test
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        short result = localMap.remove(keysArray[0]);
        return result;
    }

    // --- Bulk Operations Benchmarks ---

    @Benchmark
    public int benchmarkPutAllContainer() {
        // Mutation test
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        int result = localMap.putAll(inputContainer);
        return result;
    }

    @Benchmark
    public int benchmarkRemoveAllPredicate() {
        // Mutation test
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        // Predicate: remove all entries where the value is 0
        IntShortPredicate predicate = (key, value) -> value != (short) 0;
        int result = localMap.removeAll(predicate);
        return result;
    }

    // --- Index Operations Benchmarks ---

    @Benchmark
    public boolean benchmarkIndexExists() {
        // Read operation, map state is stable
        return map.indexExists(MAP_SIZE / 2);
    }

    @Benchmark
    public short benchmarkIndexGet() {
        // Read operation, map state is stable
        return map.indexGet(MAP_SIZE / 2);
    }

    @Benchmark
    public short benchmarkIndexReplace() {
        // Mutation test
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        short newValue = (short) 1234;
        return localMap.indexReplace(MAP_SIZE / 2, newValue);
    }

    @Benchmark
    public void benchmarkIndexInsert() {
        // Mutation test: Insert at a negative index (end)
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        int newKey = 99999;
        short newValue = (short) 500;
        localMap.indexInsert(-1, newKey, newValue);
    }

    @Benchmark
    public short benchmarkIndexRemove() {
        // Mutation test
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        short result = localMap.indexRemove(MAP_SIZE / 2);
        return result;
    }

    // --- Iteration and View Benchmarks ---

    @Benchmark
    public void benchmarkIterator() {
        // Read operation, map state is stable
        map.iterator();
    }

    @Benchmark
    public void benchmarkForEachProcedure() {
        // Read operation, map state is stable
        IntShortProcedure procedure = (key, value) -> {};
        map.forEach(procedure);
    }

    @Benchmark
    public void benchmarkForEachPredicate() {
        // Read operation, map state is stable
        IntShortPredicate predicate = (key, value) -> true;
        map.forEach(predicate);
    }

    @Benchmark
    public int benchmarkKeysContainerSize() {
        // Read operation, map state is stable
        return map.keys().size();
    }

    @Benchmark
    public void benchmarkValuesContainerForEach() {
        // Read operation, map state is stable
        // FIX: ShortCollection.forEach expects ShortProcedure, not IntShortProcedure
        ShortProcedure procedure = (value) -> {};
        map.values().forEach(procedure);
    }

    // --- Utility Benchmarks ---

    @Benchmark
    public void benchmarkClear() {
        // Mutation test
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        localMap.clear();
    }

    @Benchmark
    public void benchmarkRelease() {
        // Mutation test
        IntShortHashMap localMap = new IntShortHashMap(MAP_SIZE);
        localMap.release();
    }

    @Benchmark
    public int benchmarkSize() {
        // Read operation, map state is stable
        return map.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        // Read operation, map state is stable
        return map.isEmpty();
    }
}

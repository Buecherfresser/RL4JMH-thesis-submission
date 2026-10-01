package bench.generated.c029;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntDoubleHashMap;
import com.carrotsearch.hppc.cursors.IntDoubleCursor;
import com.carrotsearch.hppc.predicates.IntDoublePredicate;
import com.carrotsearch.hppc.IntDoubleAssociativeContainer;
import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.procedures.IntDoubleProcedure;
import java.util.Random;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntDoubleHashMapBenchmark {

    private IntDoubleHashMap map;
    private int[] keys;
    private double[] values;
    private int mapSize;
    private final Random random = new Random(42);

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize inputs once per trial
        mapSize = 1000;
        keys = new int[mapSize];
        values = new double[mapSize];
        for (int i = 0; i < mapSize; i++) {
            keys[i] = random.nextInt(Integer.MAX_VALUE);
            values[i] = random.nextDouble();
        }
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        // Initialize a fresh map instance for each iteration to ensure clean state for mutation tests
        map = new IntDoubleHashMap(mapSize);
        
        // Populate the map with the fixed inputs
        for (int i = 0; i < mapSize; i++) {
            map.put(keys[i], values[i]);
        }
    }

    // --- Read Operations ---

    @Benchmark
    public double benchmarkGet() {
        int key = keys[random.nextInt(mapSize)];
        return map.get(key);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        int key = keys[random.nextInt(mapSize)];
        return map.containsKey(key);
    }

    @Benchmark
    public double benchmarkGetOrDefault() {
        int key = keys[random.nextInt(mapSize)];
        double defaultValue = 99.99;
        return map.getOrDefault(key, defaultValue);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        int key = keys[random.nextInt(mapSize)];
        return map.indexOf(key);
    }

    @Benchmark
    public double benchmarkIndexGet() {
        int key = keys[random.nextInt(mapSize)];
        int index = map.indexOf(key);
        return map.indexGet(index);
    }

    // --- Write/Update Operations ---

    @Benchmark
    public double benchmarkPut() {
        int key = keys[random.nextInt(mapSize)];
        double value = values[random.nextInt(mapSize)];
        return map.put(key, value);
    }

    @Benchmark
    public double benchmarkPutOrAdd() {
        int key = keys[random.nextInt(mapSize)];
        double putValue = 10.0;
        double incrementValue = 1.0;
        return map.putOrAdd(key, putValue, incrementValue);
    }

    @Benchmark
    public double benchmarkAddTo() {
        int key = keys[random.nextInt(mapSize)];
        double incrementValue = 0.5;
        return map.addTo(key, incrementValue);
    }

    @Benchmark
    public double benchmarkRemove() {
        int key = keys[random.nextInt(mapSize)];
        return map.remove(key);
    }

    // --- Bulk Operations ---

    @Benchmark
    public int benchmarkPutAllIterable() {
        // Create a small iterable of cursors for bulk insertion
        IntDoubleCursor[] cursors = new IntDoubleCursor[10];
        for (int i = 0; i < 10; i++) {
            cursors[i] = new IntDoubleCursor();
            cursors[i].key = keys[random.nextInt(mapSize)];
            cursors[i].value = values[random.nextInt(mapSize)];
        }
        
        // Use a fresh map instance for this mutation test
        IntDoubleHashMap freshMap = new IntDoubleHashMap(10);
        return freshMap.putAll(Arrays.asList(cursors));
    }

    @Benchmark
    public int benchmarkRemoveAllPredicate() {
        // Predicate to remove elements where key is even
        IntDoublePredicate predicate = (key, value) -> key % 2 != 0;
        
        // Use a fresh map instance for this mutation test
        IntDoubleHashMap freshMap = new IntDoubleHashMap(mapSize);
        for (int i = 0; i < mapSize; i++) {
            freshMap.put(keys[i], values[i]);
        }
        
        return freshMap.removeAll(predicate);
    }

    // --- Iteration and View Operations ---

    @Benchmark
    public void benchmarkIterator() {
        // Consume the iterator result
        map.iterator().next();
    }

    @Benchmark
    public void benchmarkKeysContainerSize() {
        // Accessing the keys view size
        map.keys().size();
    }

    @Benchmark
    public void benchmarkValuesContainerSize() {
        // Accessing the values view size
        map.values().size();
    }

    @Benchmark
    public void benchmarkForEachProcedure() {
        // Simple procedure: do nothing
        // Explicitly cast the lambda to IntDoubleProcedure to resolve ambiguity
        map.forEach((IntDoubleProcedure) (k, v) -> {});
    }

    @Benchmark
    public void benchmarkForEachPredicate() {
        // Simple predicate: always true
        IntDoublePredicate predicate = (k, v) -> true;
        map.forEach(predicate);
    }

    // --- Maintenance Operations ---

    @Benchmark
    public void benchmarkClear() {
        map.clear();
    }

    @Benchmark
    public void benchmarkRelease() {
        map.release();
    }
}

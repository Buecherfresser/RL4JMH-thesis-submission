package bench.generated.c077;

import com.carrotsearch.hppc.ShortIntHashMap;
import com.carrotsearch.hppc.cursors.ShortIntCursor;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortIntHashMapBenchmark {

    private ShortIntHashMap map;
    private List<ShortIntCursor> testData;
    private ShortIntCursor keyToLookup;
    private ShortIntCursor keyToRemove;
    private ShortIntCursor keyToInsert;

    private static final int MAP_SIZE = 1000;
    private static final int DEFAULT_VALUE = 42;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Generate fixed test data once per trial
        testData = new ArrayList<>(MAP_SIZE);
        Random random = new Random(42);

        for (int i = 0; i < MAP_SIZE; i++) {
            // Generate short keys, ensuring key != 0
            short key = (short) (random.nextInt(32767) + 1);
            int value = random.nextInt();
            ShortIntCursor cursor = new ShortIntCursor();
            cursor.key = key;
            cursor.value = value;
            testData.add(cursor);
        }

        // Select specific cursors for targeted operations
        keyToLookup = testData.get(MAP_SIZE / 2);
        keyToRemove = testData.get(MAP_SIZE / 4);
        keyToInsert = new ShortIntCursor();
        keyToInsert.key = (short) 9999;
        keyToInsert.value = 100;
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Reset the map for mutation benchmarks to ensure consistent state
        map = new ShortIntHashMap(MAP_SIZE);
        map.clear();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Use the keyToInsert cursor for insertion
        map.put(keyToInsert.key, keyToInsert.value);
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Ensure the map contains the key before lookup
        map.put(keyToLookup.key, keyToLookup.value);
        int result = map.get(keyToLookup.key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Ensure the map contains the key before check
        map.put(keyToLookup.key, keyToLookup.value);
        boolean contains = map.containsKey(keyToLookup.key);
        bh.consume(contains);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Ensure the map contains the key before removal
        map.put(keyToRemove.key, keyToRemove.value);
        int removedCount = map.remove(keyToRemove.key);
        bh.consume(removedCount);
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        // Use the pre-generated test data
        int addedCount = map.putAll(testData);
        bh.consume(addedCount);
    }

    @Benchmark
    public void benchmarkIndexGet(Blackhole bh) {
        // Ensure map is populated enough to test index access
        map.put(keyToLookup.key, keyToLookup.value);
        int result = map.indexGet(0);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIndexReplace(Blackhole bh) {
        // Ensure map has at least one element
        map.put(keyToLookup.key, keyToLookup.value);
        int previousValue = map.indexReplace(0, 999);
        bh.consume(previousValue);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Populate map first
        map.putAll(testData);
        map.clear();
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Populate map first
        map.putAll(testData);
        int size = map.size();
        bh.consume(size);
    }
}

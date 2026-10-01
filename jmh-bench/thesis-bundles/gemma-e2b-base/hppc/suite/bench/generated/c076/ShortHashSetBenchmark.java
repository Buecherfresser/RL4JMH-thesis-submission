package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ShortHashSet;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.predicates.ShortPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortHashSetBenchmark {

    private ShortHashSet set;
    private short[] insertionElements;
    private short[] lookupElements;
    private Random random;

    // Constants for setup
    private static final int INSERT_COUNT = 10000;
    private static final int INSERT_COUNT_FOR_SETUP = 5000;
    private static final int LOOKUP_COUNT = 1000;

    @Setup
    public void setup() {
        random = new Random(42);

        // 1. Setup insertion elements (unique shorts)
        insertionElements = new short[INSERT_COUNT_FOR_SETUP];
        for (int i = 0; i < INSERT_COUNT_FOR_SETUP; i++) {
            // Generate shorts in a range to ensure some collisions but distinct values
            insertionElements[i] = (short) (random.nextInt(30000) - 15000);
        }

        // 2. Setup lookup elements (a subset of insertion elements)
        lookupElements = new short[LOOKUP_COUNT];
        for (int i = 0; i < LOOKUP_COUNT; i++) {
            lookupElements[i] = insertionElements[random.nextInt(INSERT_COUNT_FOR_SETUP)];
        }

        // 3. Initialize the set for tests that require pre-populated state
        set = ShortHashSet.from(insertionElements);
    }

    @Benchmark
    public void benchmarkAddSingle(Blackhole bh) {
        // Test adding a single element
        short key = insertionElements[random.nextInt(INSERT_COUNT_FOR_SETUP)];
        boolean result = set.add(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAddAllArray(Blackhole bh) {
        // Test adding a batch of elements from an array
        short[] batch = new short[100];
        for (int i = 0; i < 100; i++) {
            batch[i] = insertionElements[random.nextInt(INSERT_COUNT_FOR_SETUP)];
        }
        int count = set.addAll(batch);
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkContains(Blackhole bh) {
        // Test lookup for an existing element
        short key = lookupElements[random.nextInt(LOOKUP_COUNT)];
        boolean result = set.contains(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsMissing(Blackhole bh) {
        // Test lookup for a non-existing element
        short missingKey = (short) (random.nextInt(30000) - 15000);
        boolean result = set.contains(missingKey);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIndexOf(Blackhole bh) {
        // Test finding the index of an existing element
        short key = lookupElements[random.nextInt(LOOKUP_COUNT)];
        int index = set.indexOf(key);
        bh.consume(index);
    }

    @Benchmark
    public void benchmarkIndexExists(Blackhole bh) {
        // Test checking existence at a specific index
        int index = random.nextInt(set.size() + 1); // Test valid and invalid indices
        boolean result = set.indexExists(index);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRemoveSingle(Blackhole bh) {
        // Test removing a single element
        short key = lookupElements[random.nextInt(LOOKUP_COUNT)];
        boolean result = set.remove(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRemoveAllSingle(Blackhole bh) {
        // Test removing a single element using removeAll(short)
        short key = lookupElements[random.nextInt(LOOKUP_COUNT)];
        int count = set.removeAll(key);
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkRemoveAllPredicate(Blackhole bh) {
        // Test removing elements matching a predicate (e.g., removing all keys > 10000)
        ShortPredicate predicate = key -> key > 10000;
        int count = set.removeAll(predicate);
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Test clearing the entire set
        set.clear();
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size calculation
        int size = set.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkToArray(Blackhole bh) {
        // Test converting the set to an array
        short[] result = set.toArray();
        bh.consume(result);
    }
}

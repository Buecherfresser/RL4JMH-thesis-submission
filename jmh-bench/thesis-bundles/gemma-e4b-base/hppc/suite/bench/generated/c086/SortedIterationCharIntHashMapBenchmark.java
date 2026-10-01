package bench.generated.c086;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.SortedIterationCharIntHashMap;
import com.carrotsearch.hppc.CharIntHashMap;
import com.carrotsearch.hppc.CharCollection;
import com.carrotsearch.hppc.IntContainer;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharIntComparator;
import com.carrotsearch.hppc.cursors.CharIntCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.CharIntProcedure;
import com.carrotsearch.hppc.predicates.CharIntPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharIntHashMapBenchmark {

    private CharIntHashMap delegateMap;
    private SortedIterationCharIntHashMap sortedByCharComparator;
    private SortedIterationCharIntHashMap sortedByCharIntComparator;

    private char testKey;
    private int testValue;
    private int nonExistentKey;
    private int iterationSize;

    // Concrete implementations of abstract comparators to satisfy constructor requirements
    // Note: These are minimal implementations assuming natural ordering for compilation purposes.
    private static class ConcreteCharComparator implements CharComparator {
        @Override
        public int compare(char c1, char c2) {
            return Character.compare(c1, c2);
        }
    }

    private static class ConcreteCharIntComparator implements CharIntComparator {
        @Override
        public int compare(char c1, int v1, char c2, int v2) {
            int charComparison = Character.compare(c1, c2);
            if (charComparison != 0) {
                return charComparison;
            }
            return Integer.compare(v1, v2);
        }
    }

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Delegate Map
        final int mapSize = 10000;
        delegateMap = new CharIntHashMap(mapSize);

        // Populate the map
        for (int i = 0; i < mapSize; i++) {
            char key = (char) ('a' + (i % 26));
            int value = i;
            delegateMap.put(key, value);
        }

        // Setup test values
        testKey = 'a';
        testValue = 100;
        nonExistentKey = 'z' + 1;
        iterationSize = mapSize;

        // 2. Setup Comparators (using concrete implementations)
        CharComparator charComparator = new ConcreteCharComparator();
        CharIntComparator charIntComparator = new ConcreteCharIntComparator();

        // 3. Setup SUT instances (Construction/Sorting happens here)
        // Sort by Key only
        sortedByCharComparator = new SortedIterationCharIntHashMap(delegateMap, charComparator);

        // Sort by Key and Value
        sortedByCharIntComparator = new SortedIterationCharIntHashMap(delegateMap, charIntComparator);
    }

    // --- Benchmarks for Key-Sorted View (sortedByCharComparator) ---

    @Benchmark
    public void checkContainsKey_KeySorted(Blackhole bh) {
        bh.consume(sortedByCharComparator.containsKey(testKey));
    }

    @Benchmark
    public void get_KeySorted(Blackhole bh) {
        bh.consume(sortedByCharComparator.get(testKey));
    }

    @Benchmark
    public void indexOf_KeySorted(Blackhole bh) {
        bh.consume(sortedByCharComparator.indexOf(testKey));
    }

    @Benchmark
    public void size_KeySorted(Blackhole bh) {
        bh.consume(sortedByCharComparator.size());
    }

    @Benchmark
    public void isEmpty_KeySorted(Blackhole bh) {
        bh.consume(sortedByCharComparator.isEmpty());
    }

    @Benchmark
    public void forEach_KeySorted(Blackhole bh) {
        CharIntProcedure procedure = (k, v) -> {};
        bh.consume(sortedByCharComparator.forEach(procedure));
    }

    @Benchmark
    public void keysContainer_Size_KeySorted(Blackhole bh) {
        bh.consume(sortedByCharComparator.keys().size());
    }

    @Benchmark
    public void keysContainer_Iteration_KeySorted(Blackhole bh) {
        CharCursor cursor = new CharCursor();
        sortedByCharComparator.keys().iterator().next(); // Just consume the first element
        bh.consume(cursor);
    }

    @Benchmark
    public void valuesContainer_Size_KeySorted(Blackhole bh) {
        bh.consume(sortedByCharComparator.values().size());
    }

    @Benchmark
    public void valuesContainer_Iteration_KeySorted(Blackhole bh) {
        IntCursor cursor = new IntCursor();
        sortedByCharComparator.values().iterator().next(); // Just consume the first element
        bh.consume(cursor);
    }

    // --- Benchmarks for Key+Value Sorted View (sortedByCharIntComparator) ---

    @Benchmark
    public void checkContainsKey_KeyValueSorted(Blackhole bh) {
        bh.consume(sortedByCharIntComparator.containsKey(testKey));
    }

    @Benchmark
    public void get_KeyValueSorted(Blackhole bh) {
        bh.consume(sortedByCharIntComparator.get(testKey));
    }

    @Benchmark
    public void indexOf_KeyValueSorted(Blackhole bh) {
        bh.consume(sortedByCharIntComparator.indexOf(testKey));
    }

    @Benchmark
    public void size_KeyValueSorted(Blackhole bh) {
        bh.consume(sortedByCharIntComparator.size());
    }

    @Benchmark
    public void isEmpty_KeyValueSorted(Blackhole bh) {
        bh.consume(sortedByCharIntComparator.isEmpty());
    }

    @Benchmark
    public void forEach_KeyValueSorted(Blackhole bh) {
        CharIntProcedure procedure = (k, v) -> {};
        bh.consume(sortedByCharIntComparator.forEach(procedure));
    }

    @Benchmark
    public void keysContainer_Size_KeyValueSorted(Blackhole bh) {
        bh.consume(sortedByCharIntComparator.keys().size());
    }

    @Benchmark
    public void keysContainer_Iteration_KeyValueSorted(Blackhole bh) {
        CharCursor cursor = new CharCursor();
        sortedByCharIntComparator.keys().iterator().next();
        bh.consume(cursor);
    }

    @Benchmark
    public void valuesContainer_Size_KeyValueSorted(Blackhole bh) {
        bh.consume(sortedByCharIntComparator.values().size());
    }

    @Benchmark
    public void valuesContainer_Iteration_KeyValueSorted(Blackhole bh) {
        IntCursor cursor = new IntCursor();
        sortedByCharIntComparator.values().iterator().next();
        bh.consume(cursor);
    }
}

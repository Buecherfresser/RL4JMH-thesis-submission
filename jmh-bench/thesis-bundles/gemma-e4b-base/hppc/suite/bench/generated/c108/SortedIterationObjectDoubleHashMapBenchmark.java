package bench.generated.c108;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationObjectDoubleHashMap;
import com.carrotsearch.hppc.ObjectDoubleHashMap;
import com.carrotsearch.hppc.comparators.ObjectDoubleComparator;
import com.carrotsearch.hppc.cursors.ObjectDoubleCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.ObjectCollection;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.DoubleContainer;
import com.carrotsearch.hppc.procedures.ObjectDoubleProcedure;

import java.util.Comparator;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectDoubleHashMapBenchmark {

    private static final int MAP_SIZE = 10000;
    private ObjectDoubleHashMap<String> delegateMap;
    private SortedIterationObjectDoubleHashMap<String> sortedMap;
    private Comparator<String> stringComparator;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Delegate Map
        delegateMap = new ObjectDoubleHashMap<>();
        Random random = new Random(42);

        for (int i = 0; i < MAP_SIZE; i++) {
            String key = "Key_" + random.nextInt(MAP_SIZE);
            double value = random.nextDouble() * 100.0;
            delegateMap.put(key, value);
        }

        // 2. Setup Comparator
        stringComparator = Comparator.naturalOrder();

        // 3. Setup SUT (This measures the O(N log N) construction cost once for subsequent read tests)
        sortedMap = new SortedIterationObjectDoubleHashMap<>(delegateMap, stringComparator);
    }

    /**
     * Measures the time taken to construct the SortedIterationObjectDoubleHashMap,
     * which involves sorting the iteration order array (O(N log N)).
     */
    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        // Re-create the map in each invocation to measure construction time accurately
        ObjectDoubleHashMap<String> delegate = new ObjectDoubleHashMap<>();
        Random random = new Random(42);
        for (int i = 0; i < MAP_SIZE; i++) {
            String key = "Key_" + random.nextInt(MAP_SIZE);
            double value = random.nextDouble() * 100.0;
            delegate.put(key, value);
        }
        SortedIterationObjectDoubleHashMap<String> map = new SortedIterationObjectDoubleHashMap<>(delegate, stringComparator);
        bh.consume(map);
    }

    /**
     * Measures the time taken for a key lookup (containsKey).
     */
    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        String key = "Key_" + (MAP_SIZE / 2);
        bh.consume(sortedMap.containsKey(key));
    }

    /**
     * Measures the time taken for a value lookup (get).
     */
    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        String key = "Key_" + (MAP_SIZE / 2);
        bh.consume(sortedMap.get(key));
    }

    /**
     * Measures the time taken to check the size of the map.
     */
    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        bh.consume(sortedMap.size());
    }

    /**
     * Measures the time taken to iterate over all keys using the KeysContainer.
     */
    @Benchmark
    public void benchmarkIterationKeys(Blackhole bh) {
        ObjectCollection<String> keysContainer = sortedMap.keys();
        int count = 0;
        for (ObjectCursor<String> cursor : keysContainer) {
            count++;
        }
        bh.consume(count);
    }

    /**
     * Measures the time taken to iterate over all values using the ValuesContainer.
     */
    @Benchmark
    public void benchmarkIterationValues(Blackhole bh) {
        DoubleContainer valuesContainer = sortedMap.values();
        int count = 0;
        for (DoubleCursor cursor : valuesContainer) {
            count++;
        }
        bh.consume(count);
    }

    /**
     * Measures the time taken to execute a procedure (side effect) over all entries.
     */
    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        // Define a simple procedure
        ObjectDoubleProcedure<String> procedure = (key, value) -> {
            // Simulate work
        };
        sortedMap.forEach(procedure);
        bh.consume(procedure);
    }

    /**
     * Measures the time taken to find the index of a key.
     */
    @Benchmark
    public void benchmarkIndexOf(Blackhole bh) {
        String key = "Key_" + (MAP_SIZE / 2);
        bh.consume(sortedMap.indexOf(key));
    }

    /**
     * Measures the time taken to check if an index exists.
     */
    @Benchmark
    public void benchmarkIndexExists(Blackhole bh) {
        int index = MAP_SIZE / 2;
        bh.consume(sortedMap.indexExists(index));
    }

    /**
     * Measures the time taken to retrieve a value by index.
     */
    @Benchmark
    public void benchmarkIndexGet(Blackhole bh) {
        int index = MAP_SIZE / 2;
        bh.consume(sortedMap.indexGet(index));
    }
}

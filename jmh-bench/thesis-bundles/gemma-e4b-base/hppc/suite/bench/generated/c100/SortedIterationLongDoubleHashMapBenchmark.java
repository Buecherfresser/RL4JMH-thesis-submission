package bench.generated.c100;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationLongDoubleHashMap;
import com.carrotsearch.hppc.LongDoubleHashMap;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongDoubleComparator;
import com.carrotsearch.hppc.cursors.LongDoubleCursor;
import com.carrotsearch.hppc.procedures.LongDoubleProcedure;
import com.carrotsearch.hppc.predicates.LongDoublePredicate;
import com.carrotsearch.hppc.LongContainer;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.procedures.DoubleProcedure;
import com.carrotsearch.hppc.predicates.DoublePredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongDoubleHashMapBenchmark {

    private LongDoubleHashMap delegateMap;
    private SortedIterationLongDoubleHashMap sortedByKey;
    private SortedIterationLongDoubleHashMap sortedByKeyAndValue;

    private long testKey;
    private int testIndex;
    private double testDefaultValue;

    // Procedures and Predicates for iteration tests
    private LongDoubleProcedure procedure;
    private LongDoublePredicate predicate;

    private static final int MAP_SIZE = 10000;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Delegate Map
        delegateMap = new LongDoubleHashMap(MAP_SIZE);
        Random random = new Random(42);

        for (int i = 0; i < MAP_SIZE; i++) {
            long key = random.nextLong();
            double value = random.nextDouble();
            delegateMap.put(key, value);
        }

        // 2. Setup SUT instances
        // Comparator for Key only sorting (using lambda to implement LongComparator)
        LongComparator keyComparator = (a, b) -> Long.compare(a, b);
        sortedByKey = new SortedIterationLongDoubleHashMap(delegateMap, keyComparator);

        // Comparator for Key and Value sorting (using lambda to implement LongDoubleComparator)
        LongDoubleComparator kvComparator = (a, b, c, d) -> {
            if (a != c) return Long.compare(a, c);
            return Double.compare(b, d);
        };
        sortedByKeyAndValue = new SortedIterationLongDoubleHashMap(delegateMap, kvComparator);

        // 3. Setup test inputs
        testKey = delegateMap.keys[MAP_SIZE / 2];
        testIndex = MAP_SIZE / 2;
        testDefaultValue = 99.99;

        // 4. Setup iteration logic
        procedure = (k, v) -> {};
        predicate = (k, v) -> true;
    }

    @Benchmark
    public void benchmark_containsKey(Blackhole bh) {
        bh.consume(sortedByKey.containsKey(testKey));
    }

    @Benchmark
    public void benchmark_size(Blackhole bh) {
        bh.consume(sortedByKey.size());
    }

    @Benchmark
    public void benchmark_isEmpty(Blackhole bh) {
        bh.consume(sortedByKey.isEmpty());
    }

    @Benchmark
    public void benchmark_get(Blackhole bh) {
        bh.consume(sortedByKey.get(testKey));
    }

    @Benchmark
    public void benchmark_getOrDefault(Blackhole bh) {
        bh.consume(sortedByKey.getOrDefault(testKey, testDefaultValue));
    }

    @Benchmark
    public void benchmark_indexOf(Blackhole bh) {
        bh.consume(sortedByKey.indexOf(testKey));
    }

    @Benchmark
    public void benchmark_indexExists(Blackhole bh) {
        bh.consume(sortedByKey.indexExists(testIndex));
    }

    @Benchmark
    public void benchmark_indexGet(Blackhole bh) {
        bh.consume(sortedByKey.indexGet(testIndex));
    }

    @Benchmark
    public void benchmark_iterator_full_iteration(Blackhole bh) {
        // Test iteration over the sorted view
        sortedByKey.iterator().next(); // Ensure iterator is initialized
        
        // Consume the entire iteration sequence
        int count = 0;
        for (LongDoubleCursor cursor : sortedByKey) {
            bh.consume(cursor.key);
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmark_forEach_procedure(Blackhole bh) {
        // Test iteration using a procedure
        sortedByKey.forEach(procedure);
        bh.consume(procedure);
    }

    @Benchmark
    public void benchmark_forEach_predicate(Blackhole bh) {
        // Test iteration using a predicate
        sortedByKey.forEach(predicate);
        bh.consume(predicate);
    }

    @Benchmark
    public void benchmark_keys_container_iterator(Blackhole bh) {
        // Test iteration over the KeysContainer view
        int count = 0;
        for (LongCursor cursor : sortedByKey.keys()) {
            bh.consume(cursor.value);
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmark_values_container_iterator(Blackhole bh) {
        // Test iteration over the ValuesContainer view
        int count = 0;
        for (DoubleCursor cursor : sortedByKey.values()) {
            bh.consume(cursor.value);
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmark_visualizeKeyDistribution(Blackhole bh) {
        // Test visualization (requires iteration over keys)
        String result = sortedByKey.visualizeKeyDistribution(10);
        bh.consume(result);
    }
}

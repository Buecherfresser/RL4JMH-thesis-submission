package bench.generated.c104;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationLongObjectHashMap;
import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.comparators.LongObjectComparator;
import com.carrotsearch.hppc.procedures.LongObjectProcedure;
import com.carrotsearch.hppc.predicates.LongObjectPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongObjectHashMapBenchmark {

    private static final int MAP_SIZE = 10000;
    private static final String DEFAULT_VALUE = "TestValue";

    // Subject Under Test (SUT)
    private SortedIterationLongObjectHashMap<String> sut;

    // Delegate map used for setup
    private LongObjectHashMap<String> delegateMap;

    // Input data
    private long[] keys;
    private String[] values;

    // Test inputs
    private long existingKey;
    private long nonExistingKey;

    // Iteration helpers
    private LongObjectProcedure<String> procedure;
    private LongObjectPredicate<String> predicate;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Initialize data
        keys = new long[MAP_SIZE];
        values = new String[MAP_SIZE];
        Random random = new Random(42);

        for (int i = 0; i < MAP_SIZE; i++) {
            keys[i] = random.nextLong();
            values[i] = "Value_" + i;
        }

        // Select specific keys for testing
        existingKey = keys[MAP_SIZE / 2];
        nonExistingKey = keys[MAP_SIZE] + 1;

        // 2. Initialize delegate map
        delegateMap = new LongObjectHashMap<>(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            delegateMap.put(keys[i], values[i]);
        }

        // 3. Initialize SUT (SortedIterationLongObjectHashMap)
        // Use LongObjectComparator to achieve key-based sorting, as LongComparator is abstract.
        LongObjectComparator<String> keyComparator = new LongObjectComparator<String>() {
            @Override
            public int compare(long k1, String v1, long k2, String v2) {
                // Only compare keys
                return Long.compare(k1, k2);
            }
        };
        sut = new SortedIterationLongObjectHashMap<>(delegateMap, keyComparator);

        // 4. Initialize iteration helpers
        procedure = (key, value) -> {}; // No-op procedure
        predicate = (key, value) -> true; // Always true predicate
    }

    // --- Operational Benchmarks (O(1) lookups) ---

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        String result = sut.get(existingKey);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        boolean result = sut.containsKey(existingKey);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        String result = sut.getOrDefault(nonExistingKey, "Default");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIndexOf(Blackhole bh) {
        int index = sut.indexOf(existingKey);
        bh.consume(index);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        int size = sut.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkIsEmpty(Blackhole bh) {
        boolean isEmpty = sut.isEmpty();
        bh.consume(isEmpty);
    }

    // --- Iteration Benchmarks (O(N)) ---

    @Benchmark
    public void benchmarkIterationKeys(Blackhole bh) {
        int count = 0;
        for (com.carrotsearch.hppc.cursors.LongCursor cursor : sut.keys()) {
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkIterationValues(Blackhole bh) {
        int count = 0;
        for (com.carrotsearch.hppc.cursors.ObjectCursor<String> cursor : sut.values()) {
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkForEachKVProcedure(Blackhole bh) {
        sut.forEach(procedure);
        bh.consume(procedure);
    }

    @Benchmark
    public void benchmarkForEachKVPredicate(Blackhole bh) {
        sut.forEach(predicate);
        bh.consume(predicate);
    }
}

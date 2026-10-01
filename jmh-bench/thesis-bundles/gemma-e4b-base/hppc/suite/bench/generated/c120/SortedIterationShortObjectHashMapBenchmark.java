package bench.generated.c120;

import com.carrotsearch.hppc.ShortObjectHashMap;
import com.carrotsearch.hppc.SortedIterationShortObjectHashMap;
import com.carrotsearch.hppc.comparators.ShortObjectComparator;
import com.carrotsearch.hppc.procedures.ShortObjectProcedure;
import com.carrotsearch.hppc.predicates.ShortObjectPredicate;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationShortObjectHashMapBenchmark {

    private static final int MAP_SIZE = 1000;
    private static final int KEY_RANGE = 10000;

    // Subject under test (SUT)
    private SortedIterationShortObjectHashMap<String> sortedMap;

    // Delegate map used for setup
    private ShortObjectHashMap<String> delegateMap;

    // Test inputs
    private short testKey;
    private short nonExistentKey;
    private String defaultValue;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup Delegate Map
        delegateMap = new ShortObjectHashMap<>(MAP_SIZE);
        Random random = new Random(42);

        // Populate the delegate map
        for (int i = 0; i < MAP_SIZE; i++) {
            short key = (short) (random.nextInt(KEY_RANGE));
            String value = "Value_" + i;
            delegateMap.put(key, value);
        }

        // 2. Setup SUT
        // Fix: ShortComparator is abstract. We use ShortObjectComparator and implement
        // a key-only comparison logic to achieve the same sorting behavior.
        ShortObjectComparator<String> keyOnlyComparator = new ShortObjectComparator<String>() {
            @Override
            public int compare(short k1, String v1, short k2, String v2) {
                return Short.compare(k1, k2);
            }
        };
        sortedMap = new SortedIterationShortObjectHashMap<>(delegateMap, keyOnlyComparator);

        // 3. Setup Test Inputs
        // Key that exists
        testKey = (short) (random.nextInt(KEY_RANGE));
        // Key that does not exist
        nonExistentKey = (short) (KEY_RANGE + 1);
        // Default value for getOrDefault
        defaultValue = "DEFAULT";
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Test key existence lookup
        boolean contains = sortedMap.containsKey(testKey);
        bh.consume(contains);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test value retrieval
        String value = sortedMap.get(testKey);
        bh.consume(value);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Test retrieval with default value
        String value = sortedMap.getOrDefault(nonExistentKey, defaultValue);
        bh.consume(value);
    }

    @Benchmark
    public void benchmarkIterationForEachProcedure(Blackhole bh) {
        // Test iteration using a procedure (side effect simulation)
        ShortObjectProcedure<String> procedure = (k, v) -> {};
        // Consume the return value (the procedure itself)
        ShortObjectProcedure<String> result = sortedMap.forEach(procedure);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIterationForEachPredicate(Blackhole bh) {
        // Test iteration using a predicate (early exit simulation)
        ShortObjectPredicate<String> predicate = (k, v) -> true;
        // Consume the return value (the predicate itself)
        ShortObjectPredicate<String> result = sortedMap.forEach(predicate);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size retrieval
        int size = sortedMap.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkIsEmpty(Blackhole bh) {
        // Test emptiness check
        boolean isEmpty = sortedMap.isEmpty();
        bh.consume(isEmpty);
    }
}

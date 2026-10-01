package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.LongIntHashMap;
import com.carrotsearch.hppc.predicates.LongIntPredicate;
import com.carrotsearch.hppc.procedures.LongIntProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongIntHashMapBenchmark {

    private LongIntHashMap map;
    private long[] keys;
    private int[] values;
    private Random random;
    private static final int MAP_SIZE = 1000;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random(42);
        // Initialize map instance (will be populated in setupIteration)
        map = new LongIntHashMap(MAP_SIZE);
        keys = new long[MAP_SIZE];
        values = new int[MAP_SIZE];
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        // Generate fresh data for this iteration
        for (int i = 0; i < MAP_SIZE; i++) {
            long key = random.nextLong();
            int value = random.nextInt();
            keys[i] = key;
            values[i] = value;
        }
        // Rebuild map state using the static factory method
        map = LongIntHashMap.from(keys, values);
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test insertion/update
        long key = keys[0];
        int value = values[0];
        int result = map.put(key, value);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test lookup
        long key = keys[0];
        int result = map.get(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Test existence check
        long key = keys[0];
        boolean result = map.containsKey(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Test deletion
        long key = keys[0];
        int result = map.remove(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        // Test bulk insertion by creating a temporary map from the input arrays
        LongIntHashMap tempMap = LongIntHashMap.from(keys, values);
        int count = tempMap.size();
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Test lookup with default
        long key = keys[0];
        int defaultValue = 999;
        int result = map.getOrDefault(key, defaultValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIndexOf(Blackhole bh) {
        // Test finding index
        long key = keys[0];
        int index = map.indexOf(key);
        bh.consume(index);
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        // Test iteration using a predicate (early exit possible)
        LongIntPredicate predicate = (key, value) -> key % 2 == 0;
        map.forEach(predicate);
        bh.consume(predicate);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        // Test iteration using a procedure (side effect)
        LongIntProcedure procedure = (key, value) -> {};
        map.forEach(procedure);
        bh.consume(procedure);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Test size retrieval
        int size = map.size();
        bh.consume(size);
    }
}

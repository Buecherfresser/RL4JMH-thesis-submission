package bench.generated.c063;

import com.carrotsearch.hppc.ObjectLongHashMap;
import com.carrotsearch.hppc.cursors.ObjectLongCursor;
import com.carrotsearch.hppc.predicates.ObjectLongPredicate;
import com.carrotsearch.hppc.procedures.ObjectLongProcedure;
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
public class ObjectLongHashMapBenchmark {

    private ObjectLongHashMap<String> map;
    private List<String> keys;
    private List<Long> values;
    private List<ObjectLongCursor<String>> cursorIterable;
    private ObjectLongPredicate<String> removalPredicate;
    private ObjectLongProcedure<String> procedure;
    private final int MAP_SIZE = 1000;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Generate fixed inputs once per trial
        keys = new ArrayList<>(MAP_SIZE);
        values = new ArrayList<>(MAP_SIZE);
        Random random = new Random(42);

        for (int i = 0; i < MAP_SIZE; i++) {
            keys.add("Key_" + i);
            values.add((long) i * 10);
        }

        // Prepare cursor iterable for bulk operations
        cursorIterable = new ArrayList<>(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            ObjectLongCursor<String> cursor = new ObjectLongCursor<>();
            cursor.key = keys.get(i);
            cursor.value = values.get(i);
            cursorIterable.add(cursor);
        }

        // Prepare predicates/procedures
        // Predicate: Remove keys whose value is even
        removalPredicate = (key, value) -> value % 2 == 0;
        // Procedure: Increment value by 1
        procedure = (key, value) -> {}; // Dummy procedure, actual work is ignored by JMH
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Rebuild the map for every invocation to ensure consistent state for mutation tests
        map = ObjectLongHashMap.from(keys.toArray(new String[0]), values.stream().mapToLong(Long::longValue).toArray());
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Test single insertion/update
        String key = "Key_999";
        long value = 9999L;
        long result = map.put(key, value);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Test single lookup
        String key = "Key_500";
        long result = map.get(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        // Test lookup with default value
        String key = "Key_9999"; // Key not present
        long defaultValue = 12345L;
        long result = map.getOrDefault(key, defaultValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        // Test existence check
        String key = "Key_500";
        boolean result = map.containsKey(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        // Test single deletion
        String key = "Key_500";
        long result = map.remove(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPutOrAdd(Blackhole bh) {
        // Test update or insert logic
        String key = "Key_500";
        long putValue = 100L;
        long incrementValue = 5L;
        long result = map.putOrAdd(key, putValue, incrementValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAddTo(Blackhole bh) {
        // Test increment logic
        String key = "Key_500";
        long incrementValue = 10L;
        long result = map.addTo(key, incrementValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        // Test bulk insertion
        // cursorIterable is List<ObjectLongCursor<String>>, which implements Iterable<ObjectLongCursor<? extends String>>
        int count = map.putAll(cursorIterable);
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkRemoveAllPredicate(Blackhole bh) {
        // Test bulk deletion using predicate
        int count = map.removeAll(removalPredicate);
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkIteratorFetch(Blackhole bh) {
        // Test iterator fetching the first element
        map.iterator().next();
        bh.consume(true);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        // Test iteration using procedure
        map.forEach(procedure);
        bh.consume(true);
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        // Test iteration using predicate
        map.forEach(removalPredicate);
        bh.consume(true);
    }
}

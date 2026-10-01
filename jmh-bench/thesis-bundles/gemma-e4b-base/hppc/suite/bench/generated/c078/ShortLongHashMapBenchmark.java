package bench.generated.c078;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortLongHashMap;
import com.carrotsearch.hppc.cursors.ShortLongCursor;
import com.carrotsearch.hppc.predicates.ShortLongPredicate;
import com.carrotsearch.hppc.procedures.ShortLongProcedure;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortLongHashMapBenchmark {

    private ShortLongHashMap map;
    private List<ShortLongCursor> cursorsToInsert;
    private final int MAP_SIZE = 1000;
    private final int INPUT_COUNT = 100;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize the map instance
        map = new ShortLongHashMap(MAP_SIZE);

        // Prepare input data for bulk insertion operations
        cursorsToInsert = new ArrayList<>(INPUT_COUNT);
        Random random = new Random(42);

        for (int i = 0; i < INPUT_COUNT; i++) {
            ShortLongCursor cursor = new ShortLongCursor();
            // Generate unique keys and values
            short key = (short) random.nextInt(MAP_SIZE / 2);
            long value = random.nextLong();
            
            cursor.key = key;
            cursor.value = value;
            cursorsToInsert.add(cursor);
        }
    }

    @Setup(Level.Iteration)
    public void setupPut() {
        // Reset the map for insertion benchmarks
        map.clear();
    }

    /**
     * Benchmarks standard insertion (put).
     */
    @Benchmark
    public long benchmarkPut(Blackhole bh) {
        // Use the first prepared cursor for a single put operation
        ShortLongCursor cursor = cursorsToInsert.get(0);
        long result = map.put(cursor.key, cursor.value);
        bh.consume(result);
        return result;
    }

    @Setup(Level.Iteration)
    public void setupLookup() {
        // Populate the map with a fixed set of data for lookup tests
        for (int i = 0; i < MAP_SIZE / 2; i++) {
            short key = (short) i;
            long value = i * 100L;
            map.put(key, value);
        }
    }

    @Benchmark
    public long benchmarkGet(Blackhole bh) {
        // Use a key known to exist in the pre-populated map
        short key = (short) (MAP_SIZE / 4);
        long result = map.get(key);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public boolean benchmarkContainsKey(Blackhole bh) {
        // Use a key known to exist
        short key = (short) (MAP_SIZE / 4);
        boolean result = map.containsKey(key);
        bh.consume(result);
        return result;
    }

    @Setup(Level.Iteration)
    public void setupRemoval() {
        // Populate the map
        for (int i = 0; i < MAP_SIZE / 2; i++) {
            short key = (short) i;
            long value = i * 100L;
            map.put(key, value);
        }
    }

    @Benchmark
    public long benchmarkRemove(Blackhole bh) {
        // Use a key known to exist
        short key = (short) (MAP_SIZE / 4);
        long result = map.remove(key);
        bh.consume(result);
        return result;
    }

    /**
     * Benchmarks bulk insertion (putAll).
     */
    @Benchmark
    public int benchmarkPutAll(Blackhole bh) {
        // Use the pre-prepared list of key/value pairs
        int count = map.putAll(cursorsToInsert);
        bh.consume(count);
        return count;
    }

    @Setup(Level.Iteration)
    public void setupPutOrAdd() {
        // Populate the map
        for (int i = 0; i < MAP_SIZE / 2; i++) {
            short key = (short) i;
            long value = i * 100L;
            map.put(key, value);
        }
    }

    @Benchmark
    public long benchmarkPutOrAdd(Blackhole bh) {
        // Use a key known to exist (update case)
        short key = (short) (MAP_SIZE / 4);
        long result = map.putOrAdd(key, 1L, 1L);
        bh.consume(result);
        return result;
    }

    @Setup(Level.Iteration)
    public void setupIterationProcedure() {
        // Populate the map
        for (int i = 0; i < MAP_SIZE / 2; i++) {
            short key = (short) i;
            long value = i * 100L;
            map.put(key, value);
        }
    }

    @Benchmark
    public ShortLongProcedure benchmarkIterationProcedure(Blackhole bh) {
        // Define a simple procedure to consume the data
        ShortLongProcedure procedure = (k, v) -> {};
        ShortLongProcedure result = map.forEach(procedure);
        bh.consume(result);
        return result;
    }

    @Setup(Level.Iteration)
    public void setupIterationIterator() {
        // Populate the map
        for (int i = 0; i < MAP_SIZE / 2; i++) {
            short key = (short) i;
            long value = i * 100L;
            map.put(key, value);
        }
    }

    @Benchmark
    public void benchmarkIterationIterator(Blackhole bh) {
        int count = 0;
        // Iterating over the map using the iterator implementation
        for (ShortLongCursor cursor : map) {
            count++;
        }
        bh.consume(count);
    }

    @Setup(Level.Iteration)
    public void setupClear() {
        // Populate the map
        for (int i = 0; i < MAP_SIZE / 2; i++) {
            short key = (short) i;
            long value = i * 100L;
            map.put(key, value);
        }
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        map.clear();
        bh.consume(map.size());
    }
}

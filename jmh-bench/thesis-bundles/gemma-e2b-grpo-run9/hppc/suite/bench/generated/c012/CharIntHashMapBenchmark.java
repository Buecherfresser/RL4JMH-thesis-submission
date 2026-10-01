package bench.generated.c012;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.CharIntHashMap;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.procedures.CharIntProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharIntHashMapBenchmark {

    private CharIntHashMap map;

    @Setup
    public void setup() {
        // Initialize a fresh map for each benchmark run.
        // Using the default constructor which initializes with DEFAULT_EXPECTED_ELEMENTS.
        this.map = new CharIntHashMap();
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test insertion into an empty map (or a map that is expected to be small)
        map.put('a', 10);
        bh.consume(map.size());
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Ensure 'a' is present from the previous benchmark or setup
        int result = map.get('a');
        bh.consume(result);
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        // Test getting a non-existent key
        int result = map.getOrDefault('z', 999);
        bh.consume(result);
    }

    @Benchmark
    public void testPutOrAdd(Blackhole bh) {
        // Test putting a new key
        map.putOrAdd('b', 20, 5);
        bh.consume(map.size());
    }

    @Benchmark
    public void testPutOrAddExisting(Blackhole bh) {
        // Test incrementing an existing key ('a' was put in testPut)
        map.putOrAdd('a', 5, 10); // Should result in 10 + 10 = 20
        bh.consume(map.get('a'));
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        // Remove an existing key
        map.remove('a');
        bh.consume(map.size());
    }

    @Benchmark
    public void testRemoveNonExistent(Blackhole bh) {
        // Attempt to remove a key that doesn't exist
        int removedCount = map.remove('z');
        bh.consume(removedCount);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        bh.consume(map.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void testStaticFactory(Blackhole bh) {
        // Test the static factory method. This doesn't rely on instance state.
        try {
            CharIntHashMap.from(new char[]{'x', 'y'}, new int[]{1, 2});
        } catch (IllegalArgumentException e) {
            // Ignore expected exceptions if input validation fails for specific test cases
        }
        bh.consume(null);
    }

    @Benchmark
    public void testForEachProcedure(Blackhole bh) {
        // Test iteration using a procedure (read-only operation)
        map.put('c', 30);
        map.put('d', 40);

        // The procedure consumes the result, ensuring the loop runs.
        map.forEach((CharIntProcedure) (key, value) -> {
            // Do nothing, just execute the procedure
        });
        bh.consume(null);
    }
}

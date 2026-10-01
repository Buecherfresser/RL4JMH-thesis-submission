package bench.generated.c045;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongLongHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class LongLongHashMapBenchmark {

    // State field for the map instance. Since LongLongHashMap is mutable,
    // we will create a new instance inside the benchmark method for mutation tests
    // to ensure isolation, or rely on the fact that JMH isolates threads.
    private LongLongHashMap map;

    @Setup
    public void setup() {
        // Initialize a map instance. This runs once per benchmark class instance.
        this.map = new LongLongHashMap();
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        // Test insertion. We create a new map instance here to avoid state pollution
        // between benchmark iterations, which is safer for mutable structures.
        LongLongHashMap localMap = new LongLongHashMap();
        localMap.put(100L, 100L);
        bh.consume(localMap);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        // Test retrieval on an empty map (should return 0L)
        LongLongHashMap localMap = new LongLongHashMap();
        long result = localMap.get(100L);
        bh.consume(result);
    }

    @Benchmark
    public void testGetExisting(Blackhole bh) {
        // Test retrieval after insertion
        LongLongHashMap localMap = new LongLongHashMap();
        localMap.put(100L, 100L);
        long result = localMap.get(100L);
        bh.consume(result);
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        // Test getOrDefault on a non-existent key
        LongLongHashMap localMap = new LongLongHashMap();
        long result = localMap.getOrDefault(999L, 500L);
        bh.consume(result);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size on an empty map
        LongLongHashMap localMap = new LongLongHashMap();
        int size = localMap.size();
        bh.consume(size);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation
        LongLongHashMap localMap = new LongLongHashMap();
        localMap.put(1L, 1L);
        localMap.clear();
        bh.consume(localMap);
    }

    @Benchmark
    public void testPutAll(Blackhole bh) {
        // Test putAll with an empty iterable (should return 0)
        LongLongHashMap localMap = new LongLongHashMap();
        int result = localMap.putAll(java.util.Collections.emptyList());
        bh.consume(result);
    }
}

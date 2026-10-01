package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntIntHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntIntHashMapBenchmark {

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Create a fresh map for each invocation to test insertion cost
        IntIntHashMap map = new IntIntHashMap();
        int key = 12345;
        int value = 98765;
        map.put(key, value);
        bh.consume(map);
    }

    @Benchmark
    public void benchmarkGetExisting(Blackhole bh) {
        IntIntHashMap map = new IntIntHashMap();
        // Pre-populate to ensure a hit, though for a fresh map, this is a miss.
        map.put(1, 10);
        int result = map.get(1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetMissing(Blackhole bh) {
        IntIntHashMap map = new IntIntHashMap();
        // Test lookup on an empty map (miss case)
        int result = map.get(99999);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        IntIntHashMap map = new IntIntHashMap();
        // Test size on an empty map
        int size = map.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        IntIntHashMap map = new IntIntHashMap();
        map.put(100, 1);
        boolean contains = map.containsKey(100);
        bh.consume(contains);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        IntIntHashMap map = new IntIntHashMap();
        map.put(1, 10);
        int removedCount = map.remove(1);
        bh.consume(removedCount);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        IntIntHashMap map = new IntIntHashMap();
        map.put(1, 10);
        map.clear();
        bh.consume(map);
    }
}

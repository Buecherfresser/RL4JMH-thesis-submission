package bench.generated.c066;

import com.carrotsearch.hppc.ObjectObjectIdentityHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectObjectIdentityHashMapBenchmark {

    private ObjectObjectIdentityHashMap<Integer, Integer> map;
    private final int INITIAL_SIZE = 10000;

    @Setup
    public void setup() {
        // Initialize the map with a fixed set of data
        map = new ObjectObjectIdentityHashMap<>();
        for (int i = 0; i < INITIAL_SIZE; i++) {
            map.put(i, i * 2);
        }
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        int key = (int) (Math.random() * INITIAL_SIZE);
        int value = key * 2 + 1;
        map.put(key, value);
        bh.consume(null);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        int key = (int) (Math.random() * INITIAL_SIZE);
        Integer result = map.get(key);
        bh.consume(result);
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        int key = (int) (Math.random() * INITIAL_SIZE);
        Integer result = map.getOrDefault(key, 0);
        bh.consume(result);
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        int key = (int) (Math.random() * INITIAL_SIZE);
        boolean result = map.containsKey(key);
        bh.consume(result);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        int key = (int) (Math.random() * INITIAL_SIZE);
        map.remove(key);
        bh.consume(null);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        int size = map.size();
        bh.consume(size);
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        boolean empty = map.isEmpty();
        bh.consume(empty);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        map.clear();
        bh.consume(null);
    }

    @Benchmark
    public void testRelease(Blackhole bh) {
        map.release();
        bh.consume(null);
    }

    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Create a small array of keys and values for the static factory method
        Integer[] keys = new Integer[100];
        Integer[] values = new Integer[100];
        for (int i = 0; i < 100; i++) {
            keys[i] = i;
            values[i] = i * 3;
        }
        ObjectObjectIdentityHashMap<Integer, Integer> newMap = ObjectObjectIdentityHashMap.from(keys, values);
        bh.consume(newMap.size());
    }
}

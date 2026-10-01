package bench.generated.c056;

import com.carrotsearch.hppc.ObjectDoubleIdentityHashMap;
import java.util.Random;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectDoubleIdentityHashMapBenchmark {

    private ObjectDoubleIdentityHashMap<Integer> map;
    private Integer[] keys;
    private double[] values;
    private final Random random = new Random(42);

    @Setup
    public void setup() {
        // Setup a moderately sized map for testing operations
        int maxElements = 50000;

        keys = new Integer[maxElements];
        values = new double[maxElements];

        // Populate keys (Integer) and values (double)
        for (int i = 0; i < maxElements; i++) {
            keys[i] = random.nextInt(1000000);
            values[i] = random.nextDouble() * 1000000.0;
        }

        // Create the map using the static factory method for a realistic setup
        map = ObjectDoubleIdentityHashMap.from(keys, values);
    }

    @Benchmark
    public void testPut(Blackhole bh) {
        Integer key = random.nextInt(1000000);
        double value = random.nextDouble() * 1000000.0;
        double result = map.put(key, value);
        bh.consume(result);
    }

    @Benchmark
    public void testGet(Blackhole bh) {
        Integer key = random.nextInt(1000000);
        Double result = map.get(key);
        bh.consume(result);
    }

    @Benchmark
    public void testGetOrDefault(Blackhole bh) {
        Integer key = random.nextInt(1000000);
        double defaultValue = 0.0;
        double result = map.getOrDefault(key, defaultValue);
        bh.consume(result);
    }

    @Benchmark
    public void testContainsKey(Blackhole bh) {
        Integer key = random.nextInt(1000000);
        boolean result = map.containsKey(key);
        bh.consume(result);
    }

    @Benchmark
    public void testRemove(Blackhole bh) {
        Integer key = random.nextInt(1000000);
        Double result = map.remove(key);
        bh.consume(result);
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        int result = map.size();
        bh.consume(result);
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        boolean result = map.isEmpty();
        bh.consume(result);
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        map.clear();
        // Consume something to ensure the operation isn't optimized away
        bh.consume(map.size());
    }

    @Benchmark
    public void testFromStaticFactory(Blackhole bh) {
        // Create a small, specific set of inputs for the static method test
        Integer[] keysForFrom = {1, 2, 3, 4, 5};
        double[] valuesForFrom = {10.0, 20.0, 30.0, 40.0, 50.0};

        ObjectDoubleIdentityHashMap<Integer> newMap = ObjectDoubleIdentityHashMap.from(keysForFrom, valuesForFrom);
        bh.consume(newMap.size());
    }
}

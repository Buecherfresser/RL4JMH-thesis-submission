package bench.generated.c068;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectShortIdentityHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectShortIdentityHashMapBenchmark {

    private ObjectShortIdentityHashMap<Object> map;
    private Object[] keys;
    private short[] values;
    private int mapSize;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Setup large inputs once per trial
        mapSize = 1000;
        keys = new Object[mapSize];
        values = new short[mapSize];

        // Create distinct objects for identity hashing
        for (int i = 0; i < mapSize; i++) {
            keys[i] = new Object(); // Distinct object instances
            values[i] = (short) i;
        }
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        // Reset map state for consistent iteration timing
        map = new ObjectShortIdentityHashMap<>(mapSize);
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        // Put a single element
        Object key = keys[0];
        short value = values[0];
        int result = map.put(key, value);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        // Get a single element
        Object key = keys[0];
        short result = map.get(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        // Check size
        int size = map.size();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        // Fill map first
        for (int i = 0; i < mapSize; i++) {
            map.put(keys[i], values[i]);
        }
        // Clear map
        map.clear();
        bh.consume(map.isEmpty());
    }

    @Benchmark
    public void benchmarkRelease(Blackhole bh) {
        // Fill map first
        for (int i = 0; i < mapSize; i++) {
            map.put(keys[i], values[i]);
        }
        // Release map
        map.release();
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Test static factory method
        ObjectShortIdentityHashMap<Object> newMap = ObjectShortIdentityHashMap.from(keys, values);
        bh.consume(newMap.size());
    }
}

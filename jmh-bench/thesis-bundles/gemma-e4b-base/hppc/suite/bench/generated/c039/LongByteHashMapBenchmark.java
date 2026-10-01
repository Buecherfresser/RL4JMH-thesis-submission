package bench.generated.c039;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import com.carrotsearch.hppc.LongByteHashMap;
import com.carrotsearch.hppc.cursors.LongByteCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongByteHashMapBenchmark {

    private static final int MAP_POOL_SIZE = 10;
    private static final int INITIAL_CAPACITY = 1000;
    private static final int ELEMENTS_PER_MAP = 500;

    private List<LongByteHashMap> mapPool;
    private int mapIndex;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        mapPool = new ArrayList<>(MAP_POOL_SIZE);
        random = new Random(42);

        for (int i = 0; i < MAP_POOL_SIZE; i++) {
            LongByteHashMap map = new LongByteHashMap(INITIAL_CAPACITY);
            
            // Populate the map with fixed data
            for (int j = 0; j < ELEMENTS_PER_MAP; j++) {
                long key = random.nextLong();
                byte value = (byte) random.nextInt(256);
                map.put(key, value);
            }
            mapPool.add(map);
        }
        mapIndex = 0;
    }

    private LongByteHashMap getCurrentMap() {
        LongByteHashMap map = mapPool.get(mapIndex);
        mapIndex = (mapIndex + 1) % MAP_POOL_SIZE;
        return map;
    }

    private void resetMap(LongByteHashMap map) {
        map.clear();
    }

    @Benchmark
    public void benchmarkPut(Blackhole bh) {
        LongByteHashMap map = getCurrentMap();
        resetMap(map);
        
        long key = random.nextLong();
        byte value = (byte) random.nextInt(256);
        
        byte result = map.put(key, value);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        LongByteHashMap map = getCurrentMap();
        
        // Use a key that is likely present in the pre-populated map
        long key = random.nextLong();
        
        byte result = map.get(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        LongByteHashMap map = getCurrentMap();
        
        long key = random.nextLong();
        
        boolean result = map.containsKey(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkRemove(Blackhole bh) {
        LongByteHashMap map = getCurrentMap();
        
        // Use a key that is likely present
        long key = random.nextLong();
        
        byte result = map.remove(key);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPutAll(Blackhole bh) {
        LongByteHashMap map = getCurrentMap();
        resetMap(map);

        // Create a temporary container using LongByteHashMap as the concrete implementation
        LongByteHashMap container = new LongByteHashMap(ELEMENTS_PER_MAP / 2);
        
        // Populate the container
        for (int i = 0; i < ELEMENTS_PER_MAP / 2; i++) {
            long key = random.nextLong();
            byte value = (byte) random.nextInt(256);
            container.put(key, value);
        }

        int addedCount = map.putAll(container);
        bh.consume(addedCount);
    }

    @Benchmark
    public void benchmarkIteration(Blackhole bh) {
        LongByteHashMap map = getCurrentMap();
        
        // Iterate over all entries
        int count = 0;
        for (LongByteCursor cursor : map) {
            count++;
        }
        bh.consume(count);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        LongByteHashMap map = getCurrentMap();
        byte defaultValue = (byte) 0xFF;
        
        long key = random.nextLong();
        
        byte result = map.getOrDefault(key, defaultValue);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkIndexGet(Blackhole bh) {
        LongByteHashMap map = getCurrentMap();
        
        // Use a random index within the map's capacity
        int index = random.nextInt(map.size());
        
        byte result = map.indexGet(index);
        bh.consume(result);
    }
}

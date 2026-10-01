package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.CharByteHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharByteHashMapBenchmark {

    private static final int SIZE = 1024;
    private static final int POOL_SIZE = 4;

    // Read‑only map.
    private CharByteHashMap filledMap;

    // Keys/values used in benchmarks.
    private char existingKey;
    private byte existingValue;
    private char missingKey = (char) 0xFFFF;

    // Pools for mutating benchmarks.
    private CharByteHashMap[] putPool = new CharByteHashMap[POOL_SIZE];
    private int putPoolIdx = 0;

    private CharByteHashMap[] removePool = new CharByteHashMap[POOL_SIZE];
    private int removePoolIdx = 0;

    // Container for putAll benchmark.
    private CharByteHashMap containerForPutAll;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(0x1234ABCDL);
        filledMap = new CharByteHashMap(SIZE);
        for (int i = 0; i < SIZE; i++) {
            char key;
            do {
                key = (char) (rnd.nextInt(Character.MAX_VALUE) + 1);
            } while (filledMap.containsKey(key));
            byte value = (byte) rnd.nextInt(256);
            filledMap.put(key, value);
            if (i == 0) {
                existingKey = key;
                existingValue = value;
            }
        }

        for (int i = 0; i < POOL_SIZE; i++) {
            putPool[i] = new CharByteHashMap();
            removePool[i] = new CharByteHashMap();
            removePool[i].put(existingKey, existingValue);
        }

        containerForPutAll = new CharByteHashMap(SIZE / 2);
        for (int i = 0; i < SIZE / 2; i++) {
            char key = (char) (i + 1);
            containerForPutAll.put(key, (byte) (i & 0xFF));
        }
    }

    // -------------------------------------------------------------------------
    // Mutating operations.
    // -------------------------------------------------------------------------

    @Benchmark
    public byte benchmarkPut() {
        CharByteHashMap map = putPool[putPoolIdx];
        putPoolIdx = (putPoolIdx + 1) & (POOL_SIZE - 1);
        return map.put(missingKey, (byte) 42);
    }

    @Benchmark
    public byte benchmarkPutOrAdd() {
        CharByteHashMap map = putPool[putPoolIdx];
        putPoolIdx = (putPoolIdx + 1) & (POOL_SIZE - 1);
        return map.putOrAdd(missingKey, (byte) 5, (byte) 3);
    }

    @Benchmark
    public byte benchmarkAddTo() {
        CharByteHashMap map = putPool[putPoolIdx];
        putPoolIdx = (putPoolIdx + 1) & (POOL_SIZE - 1);
        return map.addTo(missingKey, (byte) 7);
    }

    @Benchmark
    public byte benchmarkRemove() {
        CharByteHashMap map = removePool[removePoolIdx];
        removePoolIdx = (removePoolIdx + 1) & (POOL_SIZE - 1);
        return map.remove(existingKey);
    }

    @Benchmark
    public int benchmarkPutAllContainer() {
        CharByteHashMap target = new CharByteHashMap();
        return target.putAll(containerForPutAll);
    }

    @Benchmark
    public int benchmarkIndexInsert() {
        CharByteHashMap map = new CharByteHashMap();
        int idx = map.indexOf(missingKey);
        map.indexInsert(idx, missingKey, (byte) 9);
        return map.get(missingKey);
    }

    @Benchmark
    public byte benchmarkIndexReplace() {
        int idx = filledMap.indexOf(existingKey);
        return filledMap.indexReplace(idx, (byte) 99);
    }

    @Benchmark
    public byte benchmarkIndexRemove() {
        int idx = filledMap.indexOf(existingKey);
        return filledMap.indexRemove(idx);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        CharByteHashMap map = new CharByteHashMap();
        map.putAll(filledMap);
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkEnsureCapacity(Blackhole bh) {
        CharByteHashMap map = new CharByteHashMap();
        map.ensureCapacity(SIZE * 2);
        bh.consume(map.size());
    }

    // -------------------------------------------------------------------------
    // Read‑only operations.
    // -------------------------------------------------------------------------

    @Benchmark
    public byte benchmarkGet() {
        return filledMap.get(existingKey);
    }

    @Benchmark
    public byte benchmarkGetOrDefault() {
        return filledMap.getOrDefault(missingKey, (byte) -1);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return filledMap.containsKey(existingKey);
    }

    @Benchmark
    public int benchmarkSize() {
        return filledMap.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return filledMap.isEmpty();
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return filledMap.indexOf(existingKey);
    }

    @Benchmark
    public byte benchmarkIndexGet() {
        int idx = filledMap.indexOf(existingKey);
        return filledMap.indexGet(idx);
    }

    @Benchmark
    public int benchmarkHashCode() {
        return filledMap.hashCode();
    }

    @Benchmark
    public boolean benchmarkEquals() {
        return filledMap.equals(filledMap.clone());
    }

    @Benchmark
    public CharByteHashMap benchmarkClone() {
        return filledMap.clone();
    }
}

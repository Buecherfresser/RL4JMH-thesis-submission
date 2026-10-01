package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.ObjectObjectIdentityHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectObjectIdentityHashMapBenchmark {
    private static final int MAP_SIZE = 1024;

    private Object[] keys;
    private Object[] values;

    private ObjectObjectIdentityHashMap<Object, Object> filledMap;
    private ObjectObjectIdentityHashMap<Object, Object> emptyMap;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345);
        keys = new Object[MAP_SIZE];
        values = new Object[MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            keys[i] = new Object();
            values[i] = new Object();
        }
        filledMap = new ObjectObjectIdentityHashMap<>(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            filledMap.put(keys[i], values[i]);
        }
        emptyMap = new ObjectObjectIdentityHashMap<>(MAP_SIZE);
    }

    @Benchmark
    public Object getExisting() {
        return filledMap.get(keys[MAP_SIZE / 2]);
    }

    @Benchmark
    public boolean containsKey() {
        return filledMap.containsKey(keys[MAP_SIZE / 3]);
    }

    @Benchmark
    public Object putNew(Blackhole bh) {
        Object newKey = new Object();
        Object newValue = new Object();
        Object prev = emptyMap.put(newKey, newValue);
        bh.consume(prev);
        emptyMap.remove(newKey);
        return prev;
    }

    @Benchmark
    public Object removeExisting(Blackhole bh) {
        Object key = keys[0];
        Object removed = filledMap.remove(key);
        bh.consume(removed);
        filledMap.put(key, values[0]);
        return removed;
    }

    @Benchmark
    public void clearMap(Blackhole bh) {
        ObjectObjectIdentityHashMap<Object, Object> tmp = new ObjectObjectIdentityHashMap<>(filledMap);
        tmp.clear();
        bh.consume(tmp);
    }

    @Benchmark
    public int size() {
        return filledMap.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return filledMap.isEmpty();
    }

    @Benchmark
    public long ramBytesUsed() {
        return filledMap.ramBytesUsed();
    }
}

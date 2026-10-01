package bench.generated.c056;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectDoubleIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectDoubleCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectDoubleIdentityHashMapBenchmark {
    private int size = 1024;
    private int mapPoolSize = 4;

    private ObjectDoubleIdentityHashMap<Object> readOnlyMap;
    private ObjectDoubleIdentityHashMap<Object>[] mutableMaps;
    private Object[] keys;
    private double[] values;
    private Object extraKey;
    private double extraValue;
    private int mutableIndex;

    @Setup
    public void init() {
        Random rnd = new Random(12345L);
        keys = new Object[size];
        values = new double[size];
        for (int i = 0; i < size; i++) {
            keys[i] = new Object();
            values[i] = rnd.nextDouble();
        }
        extraKey = new Object();
        extraValue = rnd.nextDouble();

        // Read‑only map (filled once)
        readOnlyMap = new ObjectDoubleIdentityHashMap<>(size);
        for (int i = 0; i < size; i++) {
            readOnlyMap.put(keys[i], values[i]);
        }

        // Pool of mutable maps (each pre‑filled)
        @SuppressWarnings("unchecked")
        ObjectDoubleIdentityHashMap<Object>[] pool = new ObjectDoubleIdentityHashMap[mapPoolSize];
        for (int p = 0; p < mapPoolSize; p++) {
            ObjectDoubleIdentityHashMap<Object> m = new ObjectDoubleIdentityHashMap<>(size);
            for (int i = 0; i < size; i++) {
                m.put(keys[i], values[i]);
            }
            pool[p] = m;
        }
        mutableMaps = pool;
        mutableIndex = 0;
    }

    private ObjectDoubleIdentityHashMap<Object> nextMutableMap() {
        ObjectDoubleIdentityHashMap<Object> m = mutableMaps[mutableIndex];
        mutableIndex = (mutableIndex + 1) & (mapPoolSize - 1);
        return m;
    }

    @Benchmark
    public double benchmarkGet() {
        return readOnlyMap.get(keys[0]);
    }

    @Benchmark
    public double benchmarkGetOrDefault() {
        return readOnlyMap.getOrDefault(keys[0], -1.0);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return readOnlyMap.containsKey(keys[0]);
    }

    @Benchmark
    public double benchmarkPut(Blackhole bh) {
        ObjectDoubleIdentityHashMap<Object> map = nextMutableMap();
        map.put(extraKey, extraValue);
        double v = map.get(extraKey);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public double benchmarkRemove(Blackhole bh) {
        ObjectDoubleIdentityHashMap<Object> map = nextMutableMap();
        double removed = map.remove(keys[0]);
        bh.consume(removed);
        return removed;
    }

    @Benchmark
    public double benchmarkPutOrAdd(Blackhole bh) {
        ObjectDoubleIdentityHashMap<Object> map = nextMutableMap();
        map.putOrAdd(extraKey, extraValue, extraValue);
        double v = map.get(extraKey);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public double benchmarkAddTo(Blackhole bh) {
        ObjectDoubleIdentityHashMap<Object> map = nextMutableMap();
        map.addTo(extraKey, extraValue);
        double v = map.get(extraKey);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public double benchmarkIteration() {
        double sum = 0.0;
        for (ObjectDoubleCursor<Object> c : readOnlyMap) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public int benchmarkSize() {
        return readOnlyMap.size();
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        ObjectDoubleIdentityHashMap<Object> map = nextMutableMap();
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void benchmarkEnsureCapacity(Blackhole bh) {
        ObjectDoubleIdentityHashMap<Object> map = nextMutableMap();
        map.ensureCapacity(size * 2);
        bh.consume(map.size());
    }
}

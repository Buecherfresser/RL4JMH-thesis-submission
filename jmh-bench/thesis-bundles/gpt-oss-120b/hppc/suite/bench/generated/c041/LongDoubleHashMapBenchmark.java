package bench.generated.c041;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.LongDoubleHashMap;
import com.carrotsearch.hppc.cursors.LongDoubleCursor;
import com.carrotsearch.hppc.procedures.LongDoubleProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongDoubleHashMapBenchmark {
    private static final int POOL_SIZE = 8; // power‑of‑two for fast wrap

    private int size = 1024;
    private long[] keys;
    private double[] values;
    private LongDoubleHashMap map;               // read‑only benchmark map
    private LongDoubleHashMap[] putPool;          // for mutating put‑like ops
    private LongDoubleHashMap[] removePool;       // for mutating remove‑like ops
    private LongDoubleHashMap[] clearPool;        // for clear benchmark
    private int putIdx;
    private int removeIdx;
    private int clearIdx;
    private Random rand;

    @Setup(Level.Trial)
    public void setup() {
        rand = new Random(0);
        keys = new long[size];
        values = new double[size];
        for (int i = 0; i < size; i++) {
            keys[i] = i + 1;               // avoid the special zero key
            values[i] = i * 1.0;
        }

        // Base map used for read‑only operations
        map = new LongDoubleHashMap(size);
        for (int i = 0; i < size; i++) {
            map.put(keys[i], values[i]);
        }

        // Pools for mutating benchmarks
        putPool = new LongDoubleHashMap[POOL_SIZE];
        removePool = new LongDoubleHashMap[POOL_SIZE];
        clearPool = new LongDoubleHashMap[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            putPool[i] = map.clone();
            removePool[i] = map.clone();
            clearPool[i] = map.clone();
        }
        putIdx = 0;
        removeIdx = 0;
        clearIdx = 0;
    }

    // -------------------------------------------------------------------------
    // Read‑only operations
    // -------------------------------------------------------------------------

    @Benchmark
    public double benchGet(Blackhole bh) {
        long key = keys[rand.nextInt(size)];
        double v = map.get(key);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public boolean benchContainsKey(Blackhole bh) {
        long key = keys[rand.nextInt(size)];
        boolean b = map.containsKey(key);
        bh.consume(b);
        return b;
    }

    @Benchmark
    public int benchSize() {
        return map.size();
    }

    @Benchmark
    public int benchIndexOf() {
        long key = keys[rand.nextInt(size)];
        return map.indexOf(key);
    }

    @Benchmark
    public double benchIndexGet() {
        int idx = map.indexOf(keys[rand.nextInt(size)]);
        return idx >= 0 ? map.indexGet(idx) : Double.NaN;
    }

    @Benchmark
    public void benchIterateForEach(Blackhole bh) {
        map.forEach((LongDoubleProcedure) (k, v) -> bh.consume(v));
    }

    @Benchmark
    public void benchIterateIterator(Blackhole bh) {
        for (LongDoubleCursor c : map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    // -------------------------------------------------------------------------
    // Mutating operations – each uses a fresh map from a small pool
    // -------------------------------------------------------------------------

    @Benchmark
    public double benchPut() {
        LongDoubleHashMap m = putPool[putIdx];
        putIdx = (putIdx + 1) & (POOL_SIZE - 1);
        long newKey = size + putIdx + 1; // guaranteed absent
        double newVal = rand.nextDouble();
        return m.put(newKey, newVal);
    }

    @Benchmark
    public double benchPutOrAdd() {
        LongDoubleHashMap m = putPool[putIdx];
        putIdx = (putIdx + 1) & (POOL_SIZE - 1);
        long key = keys[rand.nextInt(size)];
        double putVal = 1.0;
        double incVal = 2.0;
        return m.putOrAdd(key, putVal, incVal);
    }

    @Benchmark
    public double benchAddTo() {
        LongDoubleHashMap m = putPool[putIdx];
        putIdx = (putIdx + 1) & (POOL_SIZE - 1);
        long key = keys[rand.nextInt(size)];
        double inc = 1.5;
        return m.addTo(key, inc);
    }

    @Benchmark
    public double benchRemove() {
        LongDoubleHashMap m = removePool[removeIdx];
        removeIdx = (removeIdx + 1) & (POOL_SIZE - 1);
        long key = keys[removeIdx]; // existing key
        return m.remove(key);
    }

    @Benchmark
    public double benchIndexReplace() {
        LongDoubleHashMap m = putPool[putIdx];
        putIdx = (putIdx + 1) & (POOL_SIZE - 1);
        long key = keys[rand.nextInt(size)];
        int idx = m.indexOf(key);
        return idx >= 0 ? m.indexReplace(idx, 42.0) : Double.NaN;
    }

    @Benchmark
    public double benchIndexRemove() {
        LongDoubleHashMap m = removePool[removeIdx];
        removeIdx = (removeIdx + 1) & (POOL_SIZE - 1);
        long key = keys[rand.nextInt(size)];
        int idx = m.indexOf(key);
        return idx >= 0 ? m.indexRemove(idx) : Double.NaN;
    }

    @Benchmark
    public void benchClear(Blackhole bh) {
        LongDoubleHashMap m = clearPool[clearIdx];
        clearIdx = (clearIdx + 1) & (POOL_SIZE - 1);
        m.clear();
        bh.consume(m.size()); // always 0 after clear
    }
}

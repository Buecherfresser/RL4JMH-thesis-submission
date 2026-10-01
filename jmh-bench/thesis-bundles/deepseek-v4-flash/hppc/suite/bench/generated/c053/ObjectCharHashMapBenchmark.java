package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.ObjectCharHashMap;
import com.carrotsearch.hppc.cursors.ObjectCharCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.procedures.ObjectCharProcedure;
import com.carrotsearch.hppc.predicates.ObjectCharPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
@Threads(1)
public class ObjectCharHashMapBenchmark {

    private ObjectCharHashMap<String> map;
    private String[] keys;
    private char[] values;
    private ObjectCharHashMap<String> otherMap;

    private ObjectCharHashMap<String>[] poolMaps;
    private String[] poolKeys;
    private int poolIndex;

    @Setup(Level.Trial)
    public void setup() {
        int size = 1000;
        map = new ObjectCharHashMap<>(size);
        keys = new String[size];
        values = new char[size];
        Random rand = new Random(12345);
        for (int i = 0; i < size; i++) {
            keys[i] = "key" + i;
            values[i] = (char) rand.nextInt(Character.MAX_VALUE);
            map.put(keys[i], values[i]);
        }

        otherMap = new ObjectCharHashMap<>(size);
        for (int i = 0; i < size; i++) {
            otherMap.put(keys[i], values[i]);
        }

        int poolSize = 1000;
        poolMaps = new ObjectCharHashMap[poolSize];
        poolKeys = new String[poolSize];
        for (int i = 0; i < poolSize; i++) {
            poolKeys[i] = "poolKey" + i;
            poolMaps[i] = new ObjectCharHashMap<>(1);
            poolMaps[i].put(poolKeys[i], (char) rand.nextInt(Character.MAX_VALUE));
        }
        poolIndex = 0;
    }

    private int nextPoolIndex() {
        int idx = poolIndex;
        poolIndex = (poolIndex + 1) % poolMaps.length;
        return idx;
    }

    // ---------- Basic operations ----------

    @Benchmark
    public char putOverwrite() {
        return map.put(keys[0], (char) (values[0] + 1));
    }

    @Benchmark
    public char putNew() {
        int idx = nextPoolIndex();
        ObjectCharHashMap<String> m = poolMaps[idx];
        return m.put("newKey", (char) 1);
    }

    @Benchmark
    public char get() {
        return map.get(keys[0]);
    }

    @Benchmark
    public char getOrDefault() {
        return map.getOrDefault(keys[0], (char) 0);
    }

    @Benchmark
    public boolean containsKey() {
        return map.containsKey(keys[0]);
    }

    @Benchmark
    public char remove() {
        int idx = nextPoolIndex();
        ObjectCharHashMap<String> m = poolMaps[idx];
        String k = poolKeys[idx];
        return m.remove(k);
    }

    @Benchmark
    public char putOrAdd() {
        return map.putOrAdd(keys[0], (char) 1, (char) 1);
    }

    @Benchmark
    public char addTo() {
        return map.addTo(keys[0], (char) 1);
    }

    // ---------- Index-based operations ----------

    @Benchmark
    public int indexOf() {
        return map.indexOf(keys[0]);
    }

    @Benchmark
    public boolean indexExists() {
        int idx = map.indexOf(keys[0]);
        return map.indexExists(idx);
    }

    @Benchmark
    public char indexGet() {
        int idx = map.indexOf(keys[0]);
        return map.indexGet(idx);
    }

    @Benchmark
    public char indexReplace() {
        int idx = map.indexOf(keys[0]);
        return map.indexReplace(idx, (char) (values[0] + 1));
    }

    @Benchmark
    public char indexInsert() {
        int idx = nextPoolIndex();
        ObjectCharHashMap<String> m = poolMaps[idx];
        int insertIdx = m.indexOf("missingKey");
        m.indexInsert(insertIdx, "missingKey", (char) 1);
        return m.get("missingKey");
    }

    @Benchmark
    public char indexRemove() {
        int idx = nextPoolIndex();
        ObjectCharHashMap<String> m = poolMaps[idx];
        String k = poolKeys[idx];
        int removeIdx = m.indexOf(k);
        return m.indexRemove(removeIdx);
    }

    // ---------- Bulk / lifecycle ----------

    @Benchmark
    public void clear(Blackhole bh) {
        int idx = nextPoolIndex();
        ObjectCharHashMap<String> m = poolMaps[idx];
        m.clear();
        bh.consume(m.size());
    }

    @Benchmark
    public void release(Blackhole bh) {
        int idx = nextPoolIndex();
        ObjectCharHashMap<String> m = poolMaps[idx];
        m.release();
        bh.consume(m.size());
    }

    @Benchmark
    public int size() {
        return map.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Benchmark
    public void ensureCapacity(Blackhole bh) {
        map.ensureCapacity(2000);
        bh.consume(map.size());
    }

    @Benchmark
    public long ramBytesAllocated() {
        return map.ramBytesAllocated();
    }

    @Benchmark
    public long ramBytesUsed() {
        return map.ramBytesUsed();
    }

    // ---------- Iteration and views ----------

    @Benchmark
    public int iterate() {
        int sum = 0;
        for (ObjectCharCursor<String> c : map) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public int forEachProcedure() {
        int[] sum = {0};
        map.forEach((ObjectCharProcedure<String>) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public int forEachPredicate() {
        int[] sum = {0};
        map.forEach((ObjectCharPredicate<String>) (k, v) -> {
            sum[0] += v;
            return true;
        });
        return sum[0];
    }

    @Benchmark
    public int keysView() {
        int count = 0;
        for (ObjectCursor<String> c : map.keys()) {
            count++;
        }
        return count;
    }

    @Benchmark
    public int valuesView() {
        int sum = 0;
        for (CharCursor c : map.values()) {
            sum += c.value;
        }
        return sum;
    }

    // ---------- Object methods ----------

    @Benchmark
    public ObjectCharHashMap<String> clone() {
        return map.clone();
    }

    @Benchmark
    public String toString() {
        return map.toString();
    }

    @Benchmark
    public int hashCode() {
        return map.hashCode();
    }

    @Benchmark
    public boolean equals() {
        return map.equals(otherMap);
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return map.visualizeKeyDistribution(10);
    }

    @Benchmark
    public ObjectCharHashMap<String> from() {
        return ObjectCharHashMap.from(keys, values);
    }
}

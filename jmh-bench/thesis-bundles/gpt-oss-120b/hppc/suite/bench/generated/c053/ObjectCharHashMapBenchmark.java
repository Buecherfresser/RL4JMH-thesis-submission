package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectCharHashMap;
import com.carrotsearch.hppc.cursors.ObjectCharCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.procedures.ObjectCharProcedure;
import com.carrotsearch.hppc.predicates.ObjectCharPredicate;
import java.util.Iterator;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectCharHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final int MAP_SIZE = 1024;
        final int POOL_SIZE = 16;
        ObjectCharHashMap<String> map;
        String[] keys;
        char[] values;
        ObjectCharHashMap<String>[] mapPool;
        ObjectCharHashMap<String>[] emptyMapPool;
        int poolIndex;
        Random random;

        @Setup(Level.Trial)
        public void setup() {
            random = new Random(12345);
            keys = new String[MAP_SIZE];
            values = new char[MAP_SIZE];
            for (int i = 0; i < MAP_SIZE; i++) {
                keys[i] = "key" + i;
                values[i] = (char) (i % 65536);
            }
            map = new ObjectCharHashMap<>(MAP_SIZE);
            for (int i = 0; i < MAP_SIZE; i++) {
                map.put(keys[i], values[i]);
            }
            @SuppressWarnings("unchecked")
            ObjectCharHashMap<String>[] tmpPool = (ObjectCharHashMap<String>[]) new ObjectCharHashMap[POOL_SIZE];
            @SuppressWarnings("unchecked")
            ObjectCharHashMap<String>[] tmpEmpty = (ObjectCharHashMap<String>[]) new ObjectCharHashMap[POOL_SIZE];
            mapPool = tmpPool;
            emptyMapPool = tmpEmpty;
            for (int i = 0; i < POOL_SIZE; i++) {
                ObjectCharHashMap<String> m = new ObjectCharHashMap<>(MAP_SIZE);
                m.put(keys[i], values[i]);
                mapPool[i] = m;
                emptyMapPool[i] = new ObjectCharHashMap<>(MAP_SIZE);
            }
            poolIndex = 0;
        }

        ObjectCharHashMap<String> nextMapFromPool() {
            int idx = poolIndex;
            poolIndex = (poolIndex + 1) & (POOL_SIZE - 1);
            return mapPool[idx];
        }

        ObjectCharHashMap<String> nextEmptyMapFromPool() {
            int idx = poolIndex;
            poolIndex = (poolIndex + 1) & (POOL_SIZE - 1);
            return emptyMapPool[idx];
        }
    }

    @Benchmark
    public char putUpdate(BenchmarkState s) {
        return s.map.put(s.keys[0], (char) (s.values[0] + 1));
    }

    @Benchmark
    public char putNewKey(BenchmarkState s) {
        ObjectCharHashMap<String> m = s.nextEmptyMapFromPool();
        return m.put("newKey", (char) 42);
    }

    @Benchmark
    public char getExisting(BenchmarkState s) {
        return s.map.get(s.keys[0]);
    }

    @Benchmark
    public char getOrDefaultMissing(BenchmarkState s) {
        return s.map.getOrDefault("missing", (char) 7);
    }

    @Benchmark
    public boolean containsKeyExisting(BenchmarkState s) {
        return s.map.containsKey(s.keys[0]);
    }

    @Benchmark
    public char removeExisting(BenchmarkState s) {
        ObjectCharHashMap<String> m = s.nextMapFromPool();
        return m.remove(s.keys[0]);
    }

    @Benchmark
    public char putOrAddExisting(BenchmarkState s) {
        return s.map.putOrAdd(s.keys[0], (char) 1, (char) 2);
    }

    @Benchmark
    public char addToExisting(BenchmarkState s) {
        return s.map.addTo(s.keys[0], (char) 3);
    }

    @Benchmark
    public int indexOfExisting(BenchmarkState s) {
        return s.map.indexOf(s.keys[0]);
    }

    @Benchmark
    public char indexGetExisting(BenchmarkState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexGet(idx);
    }

    @Benchmark
    public char indexReplaceExisting(BenchmarkState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexReplace(idx, (char) 99);
    }

    @Benchmark
    public void indexInsertNew(BenchmarkState s) {
        ObjectCharHashMap<String> m = s.nextEmptyMapFromPool();
        int idx = m.indexOf("newKey");
        m.indexInsert(idx, "newKey", (char) 55);
    }

    @Benchmark
    public char indexRemoveExisting(BenchmarkState s) {
        ObjectCharHashMap<String> m = s.nextMapFromPool();
        int idx = m.indexOf(s.keys[0]);
        return m.indexRemove(idx);
    }

    @Benchmark
    public void clearMap(BenchmarkState s) {
        ObjectCharHashMap<String> m = s.nextMapFromPool();
        m.clear();
    }

    @Benchmark
    public int size(BenchmarkState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean isEmpty(BenchmarkState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public void forEachProcedure(BenchmarkState s, Blackhole bh) {
        s.map.forEach((ObjectCharProcedure<String>) (k, v) -> bh.consume(v));
    }

    @Benchmark
    public void forEachPredicate(BenchmarkState s, Blackhole bh) {
        s.map.forEach((ObjectCharPredicate<String>) (k, v) -> {
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void iterateEntries(BenchmarkState s, Blackhole bh) {
        for (ObjectCharCursor<String> c : s.map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateKeys(BenchmarkState s, Blackhole bh) {
        Iterator<ObjectCursor<String>> it = s.map.keys().iterator();
        while (it.hasNext()) {
            bh.consume(it.next().value);
        }
    }

    @Benchmark
    public void iterateValues(BenchmarkState s, Blackhole bh) {
        Iterator<CharCursor> it = s.map.values().iterator();
        while (it.hasNext()) {
            bh.consume(it.next().value);
        }
    }

    @Benchmark
    public ObjectCharHashMap<String> cloneMap(BenchmarkState s) {
        return s.map.clone();
    }

    @Benchmark
    public int hashCode(BenchmarkState s) {
        return s.map.hashCode();
    }

    @Benchmark
    public boolean equalsClone(BenchmarkState s) {
        ObjectCharHashMap<String> copy = s.map.clone();
        return s.map.equals(copy);
    }

    @Benchmark
    public String toString(BenchmarkState s) {
        return s.map.toString();
    }

    @Benchmark
    public long ramBytesUsed(BenchmarkState s) {
        return s.map.ramBytesUsed();
    }

    @Benchmark
    public String visualizeKeyDistribution(BenchmarkState s) {
        return s.map.visualizeKeyDistribution(10);
    }
}

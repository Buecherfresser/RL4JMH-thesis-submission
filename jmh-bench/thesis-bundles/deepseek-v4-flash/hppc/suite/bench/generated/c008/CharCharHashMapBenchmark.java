package bench.generated.c008;

import com.carrotsearch.hppc.CharCharHashMap;
import com.carrotsearch.hppc.CharHashSet;
import com.carrotsearch.hppc.cursors.CharCharCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.predicates.CharCharPredicate;
import com.carrotsearch.hppc.predicates.CharPredicate;
import com.carrotsearch.hppc.procedures.CharCharProcedure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharCharHashMapBenchmark {

    // ---------------------------------------------------------------------
    // State for read-only operations (single map, reused)
    // ---------------------------------------------------------------------
    @State(Scope.Benchmark)
    public static class ReadOnlyState {
        CharCharHashMap map;
        char[] keys;
        char[] values;
        int size = 1024; // number of entries

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(0x12345678);
            map = new CharCharHashMap(size);
            keys = new char[size];
            values = new char[size];
            // Generate unique keys (avoid 0 because it's the empty marker)
            for (int i = 0; i < size; i++) {
                char k;
                do {
                    k = (char) (rnd.nextInt(Character.MAX_VALUE - 1) + 1);
                } while (map.containsKey(k));
                char v = (char) rnd.nextInt();
                keys[i] = k;
                values[i] = v;
                map.put(k, v);
            }
        }
    }

    // ---------------------------------------------------------------------
    // State for mutating operations that change size (pool of maps)
    // ---------------------------------------------------------------------
    @State(Scope.Benchmark)
    public static class PoolState {
        static final int POOL_SIZE = 256;
        CharCharHashMap[] maps;
        char[] insertKeys;   // key not present in corresponding map
        char[] removeKeys;   // key present in corresponding map
        int index = 0;

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(0xDEADBEEF);
            maps = new CharCharHashMap[POOL_SIZE];
            insertKeys = new char[POOL_SIZE];
            removeKeys = new char[POOL_SIZE];
            int entriesPerMap = 512;
            for (int i = 0; i < POOL_SIZE; i++) {
                CharCharHashMap m = new CharCharHashMap(entriesPerMap);
                // Generate unique keys for this map
                char[] keys = new char[entriesPerMap];
                for (int j = 0; j < entriesPerMap; j++) {
                    char k;
                    do {
                        k = (char) (rnd.nextInt(Character.MAX_VALUE - 1) + 1);
                    } while (m.containsKey(k));
                    keys[j] = k;
                    m.put(k, (char) rnd.nextInt());
                }
                // Pick a key to remove (present)
                removeKeys[i] = keys[rnd.nextInt(entriesPerMap)];
                // Pick a key to insert (not present)
                char insertKey;
                do {
                    insertKey = (char) (rnd.nextInt(Character.MAX_VALUE - 1) + 1);
                } while (m.containsKey(insertKey));
                insertKeys[i] = insertKey;
                maps[i] = m;
            }
        }

        // Helper to get next map and its associated keys
        public CharCharHashMap nextMap() {
            int i = (index++) & (POOL_SIZE - 1);
            return maps[i];
        }
    }

    // ---------------------------------------------------------------------
    // Read-only benchmarks
    // ---------------------------------------------------------------------

    @Benchmark
    public char get(ReadOnlyState s, Blackhole bh) {
        char sum = 0;
        for (int i = 0; i < s.size; i++) {
            sum += s.map.get(s.keys[i]);
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public char getOrDefault(ReadOnlyState s, Blackhole bh) {
        char sum = 0;
        for (int i = 0; i < s.size; i++) {
            sum += s.map.getOrDefault(s.keys[i], (char) 0);
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public boolean containsKey(ReadOnlyState s, Blackhole bh) {
        boolean all = true;
        for (int i = 0; i < s.size; i++) {
            all &= s.map.containsKey(s.keys[i]);
        }
        bh.consume(all);
        return all;
    }

    @Benchmark
    public int indexOf(ReadOnlyState s, Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < s.size; i++) {
            sum += s.map.indexOf(s.keys[i]);
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public boolean indexExists(ReadOnlyState s, Blackhole bh) {
        boolean all = true;
        for (int i = 0; i < s.size; i++) {
            int idx = s.map.indexOf(s.keys[i]);
            all &= s.map.indexExists(idx);
        }
        bh.consume(all);
        return all;
    }

    @Benchmark
    public char indexGet(ReadOnlyState s, Blackhole bh) {
        char sum = 0;
        for (int i = 0; i < s.size; i++) {
            int idx = s.map.indexOf(s.keys[i]);
            sum += s.map.indexGet(idx);
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public int size(ReadOnlyState s, Blackhole bh) {
        int sz = s.map.size();
        bh.consume(sz);
        return sz;
    }

    @Benchmark
    public boolean isEmpty(ReadOnlyState s, Blackhole bh) {
        boolean empty = s.map.isEmpty();
        bh.consume(empty);
        return empty;
    }

    @Benchmark
    public int hashCode(ReadOnlyState s, Blackhole bh) {
        int h = s.map.hashCode();
        bh.consume(h);
        return h;
    }

    @Benchmark
    public boolean equals(ReadOnlyState s, Blackhole bh) {
        // Compare with a clone to avoid modifying the original
        CharCharHashMap other = s.map.clone();
        boolean eq = s.map.equals(other);
        bh.consume(eq);
        return eq;
    }

    @Benchmark
    public long ramBytesAllocated(ReadOnlyState s, Blackhole bh) {
        long bytes = s.map.ramBytesAllocated();
        bh.consume(bytes);
        return bytes;
    }

    @Benchmark
    public long ramBytesUsed(ReadOnlyState s, Blackhole bh) {
        long bytes = s.map.ramBytesUsed();
        bh.consume(bytes);
        return bytes;
    }

    @Benchmark
    public String toString(ReadOnlyState s, Blackhole bh) {
        String str = s.map.toString();
        bh.consume(str);
        return str;
    }

    @Benchmark
    public String visualizeKeyDistribution(ReadOnlyState s, Blackhole bh) {
        String viz = s.map.visualizeKeyDistribution(80);
        bh.consume(viz);
        return viz;
    }

    @Benchmark
    public CharCharHashMap clone(ReadOnlyState s, Blackhole bh) {
        CharCharHashMap copy = s.map.clone();
        bh.consume(copy);
        return copy;
    }

    // ---------------------------------------------------------------------
    // Iteration benchmarks (read-only)
    // ---------------------------------------------------------------------

    @Benchmark
    public long iterate(ReadOnlyState s, Blackhole bh) {
        long sum = 0;
        for (CharCharCursor c : s.map) {
            sum += c.key + c.value;
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public long forEachProcedure(ReadOnlyState s, Blackhole bh) {
        final long[] sum = {0};
        s.map.forEach((CharCharProcedure) (k, v) -> sum[0] += k + v);
        bh.consume(sum[0]);
        return sum[0];
    }

    @Benchmark
    public long forEachPredicate(ReadOnlyState s, Blackhole bh) {
        final long[] sum = {0};
        s.map.forEach((CharCharPredicate) (k, v) -> {
            sum[0] += k + v;
            return true;
        });
        bh.consume(sum[0]);
        return sum[0];
    }

    @Benchmark
    public long iterateKeys(ReadOnlyState s, Blackhole bh) {
        long sum = 0;
        for (CharCursor c : s.map.keys()) {
            sum += c.value;
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public long iterateValues(ReadOnlyState s, Blackhole bh) {
        long sum = 0;
        for (CharCursor c : s.map.values()) {
            sum += c.value;
        }
        bh.consume(sum);
        return sum;
    }

    // ---------------------------------------------------------------------
    // Mutating benchmarks (update operations, no size change)
    // ---------------------------------------------------------------------

    @Benchmark
    public char putExisting(ReadOnlyState s, Blackhole bh) {
        char last = 0;
        for (int i = 0; i < s.size; i++) {
            last = s.map.put(s.keys[i], (char) (s.values[i] + 1));
        }
        bh.consume(last);
        return last;
    }

    @Benchmark
    public char putOrAddExisting(ReadOnlyState s, Blackhole bh) {
        char last = 0;
        for (int i = 0; i < s.size; i++) {
            last = s.map.putOrAdd(s.keys[i], (char) 1, (char) 1);
        }
        bh.consume(last);
        return last;
    }

    @Benchmark
    public char addToExisting(ReadOnlyState s, Blackhole bh) {
        char last = 0;
        for (int i = 0; i < s.size; i++) {
            last = s.map.addTo(s.keys[i], (char) 1);
        }
        bh.consume(last);
        return last;
    }

    @Benchmark
    public char indexReplace(ReadOnlyState s, Blackhole bh) {
        char last = 0;
        for (int i = 0; i < s.size; i++) {
            int idx = s.map.indexOf(s.keys[i]);
            last = s.map.indexReplace(idx, (char) (s.values[i] + 1));
        }
        bh.consume(last);
        return last;
    }

    // ---------------------------------------------------------------------
    // Mutating benchmarks (size-changing, using pool)
    // ---------------------------------------------------------------------

    @Benchmark
    public char putNew(PoolState s, Blackhole bh) {
        CharCharHashMap map = s.nextMap();
        char key = s.insertKeys[(s.index - 1) & (PoolState.POOL_SIZE - 1)];
        char val = (char) 42;
        char prev = map.put(key, val);
        bh.consume(prev);
        return prev;
    }

    @Benchmark
    public char remove(PoolState s, Blackhole bh) {
        CharCharHashMap map = s.nextMap();
        char key = s.removeKeys[(s.index - 1) & (PoolState.POOL_SIZE - 1)];
        char prev = map.remove(key);
        bh.consume(prev);
        return prev;
    }

    @Benchmark
    public void clear(PoolState s, Blackhole bh) {
        CharCharHashMap map = s.nextMap();
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void release(PoolState s, Blackhole bh) {
        CharCharHashMap map = s.nextMap();
        map.release();
        bh.consume(map.size());
    }

    @Benchmark
    public void ensureCapacity(PoolState s, Blackhole bh) {
        CharCharHashMap map = s.nextMap();
        map.ensureCapacity(map.size() * 2);
        bh.consume(map.size());
    }

    @Benchmark
    public char indexInsert(PoolState s, Blackhole bh) {
        CharCharHashMap map = s.nextMap();
        char key = s.insertKeys[(s.index - 1) & (PoolState.POOL_SIZE - 1)];
        int idx = map.indexOf(key);
        map.indexInsert(idx, key, (char) 7);
        bh.consume(map.get(key));
        return map.get(key);
    }

    @Benchmark
    public char indexRemove(PoolState s, Blackhole bh) {
        CharCharHashMap map = s.nextMap();
        char key = s.removeKeys[(s.index - 1) & (PoolState.POOL_SIZE - 1)];
        int idx = map.indexOf(key);
        char prev = map.indexRemove(idx);
        bh.consume(prev);
        return prev;
    }

    // ---------------------------------------------------------------------
    // Bulk operations
    // ---------------------------------------------------------------------

    @Benchmark
    public int putAll(ReadOnlyState s, Blackhole bh) {
        CharCharHashMap other = new CharCharHashMap(s.size);
        for (int i = 0; i < s.size; i++) {
            other.put(s.keys[i], s.values[i]);
        }
        int added = s.map.putAll(other);
        bh.consume(added);
        return added;
    }

    @Benchmark
    public int removeAllContainer(ReadOnlyState s, Blackhole bh) {
        // Remove all keys that are present (i.e., all keys)
        CharHashSet container = new CharHashSet();
        for (char k : s.keys) {
            container.add(k);
        }
        int removed = s.map.removeAll(container);
        bh.consume(removed);
        return removed;
    }

    @Benchmark
    public int removeAllPredicate(ReadOnlyState s, Blackhole bh) {
        int removed = s.map.removeAll((CharCharPredicate) (k, v) -> true);
        bh.consume(removed);
        return removed;
    }

    @Benchmark
    public int removeAllKeyPredicate(ReadOnlyState s, Blackhole bh) {
        int removed = s.map.removeAll((CharPredicate) k -> true);
        bh.consume(removed);
        return removed;
    }

    // ---------------------------------------------------------------------
    // Static factory
    // ---------------------------------------------------------------------

    @Benchmark
    public CharCharHashMap from(ReadOnlyState s, Blackhole bh) {
        CharCharHashMap map = CharCharHashMap.from(s.keys, s.values);
        bh.consume(map);
        return map;
    }
}

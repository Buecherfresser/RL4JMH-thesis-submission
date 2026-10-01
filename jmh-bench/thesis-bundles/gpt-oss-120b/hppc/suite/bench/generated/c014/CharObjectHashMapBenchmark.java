package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.CharObjectHashMap;
import com.carrotsearch.hppc.cursors.CharObjectCursor;
import com.carrotsearch.hppc.procedures.CharObjectProcedure;
import com.carrotsearch.hppc.predicates.CharObjectPredicate;
import com.carrotsearch.hppc.procedures.CharProcedure;
import com.carrotsearch.hppc.predicates.CharPredicate;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.predicates.ObjectPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharObjectHashMapBenchmark {

    private int elementCount = 1024;

    private CharObjectHashMap<String> map;
    private char[] keys;
    private String[] values;
    private Random random;

    @Setup(Level.Trial)
    public void setUp() {
        map = new CharObjectHashMap<>(elementCount);
        keys = new char[elementCount];
        values = new String[elementCount];
        random = new Random(12345L);

        for (int i = 0; i < elementCount; i++) {
            char k;
            // avoid the special zero key
            do {
                k = (char) (random.nextInt(Character.MAX_VALUE) + 1);
            } while (containsKey(keys, k, i));
            keys[i] = k;
            String v = "val" + i;
            values[i] = v;
            map.put(k, v);
        }
    }

    private boolean containsKey(char[] arr, char key, int limit) {
        for (int i = 0; i < limit; i++) {
            if (arr[i] == key) {
                return true;
            }
        }
        return false;
    }

    private char randomKey() {
        return keys[random.nextInt(elementCount)];
    }

    private String randomValue() {
        return values[random.nextInt(elementCount)];
    }

    @Benchmark
    public String get() {
        return map.get(randomKey());
    }

    @Benchmark
    public String getOrDefault() {
        return map.getOrDefault(randomKey(), "default");
    }

    @Benchmark
    public boolean containsKey() {
        return map.containsKey(randomKey());
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
    public int hashCodeBenchmark() {
        return map.hashCode();
    }

    @Benchmark
    public boolean equalsBenchmark() {
        CharObjectHashMap<String> clone = map.clone();
        return map.equals(clone);
    }

    @Benchmark
    public CharObjectHashMap<String> cloneBenchmark() {
        return map.clone();
    }

    @Benchmark
    public void iterate(Blackhole bh) {
        for (CharObjectCursor<String> c : map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        map.forEach((CharObjectProcedure<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        map.forEach((CharObjectPredicate<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public boolean keysContains() {
        return map.keys().contains(randomKey());
    }

    @Benchmark
    public void keysForEach(Blackhole bh) {
        map.keys().forEach((CharProcedure) k -> bh.consume(k));
    }

    @Benchmark
    public boolean valuesContains() {
        return map.values().contains(randomValue());
    }

    @Benchmark
    public void valuesForEach(Blackhole bh) {
        map.values().forEach((ObjectProcedure<String>) v -> bh.consume(v));
    }

    @Benchmark
    public int indexOf() {
        return map.indexOf(randomKey());
    }

    @Benchmark
    public String indexGet() {
        int idx = map.indexOf(randomKey());
        if (idx >= 0) {
            return map.indexGet(idx);
        }
        return null;
    }

    @Benchmark
    public String indexReplace() {
        int idx = map.indexOf(randomKey());
        if (idx >= 0) {
            return map.indexReplace(idx, "replaced");
        }
        return null;
    }

    @Benchmark
    public void indexInsert(Blackhole bh) {
        char newKey;
        do {
            newKey = (char) (random.nextInt(Character.MAX_VALUE) + 1);
        } while (map.containsKey(newKey));
        int idx = map.indexOf(newKey); // will be negative
        CharObjectHashMap<String> clone = map.clone();
        clone.indexInsert(idx, newKey, "inserted");
        bh.consume(clone);
    }

    @Benchmark
    public String indexRemove() {
        int idx = map.indexOf(randomKey());
        if (idx >= 0) {
            return map.indexRemove(idx);
        }
        return null;
    }

    @Benchmark
    public void clear(Blackhole bh) {
        CharObjectHashMap<String> clone = map.clone();
        clone.clear();
        bh.consume(clone);
    }

    @Benchmark
    public long ramBytesUsed() {
        return map.ramBytesUsed();
    }

    @Benchmark
    public long ramBytesAllocated() {
        return map.ramBytesAllocated();
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return map.visualizeKeyDistribution(64);
    }

    @Benchmark
    public CharObjectHashMap<String> put() {
        CharObjectHashMap<String> clone = map.clone();
        char newKey;
        do {
            newKey = (char) (random.nextInt(Character.MAX_VALUE) + 1);
        } while (clone.containsKey(newKey));
        clone.put(newKey, "newValue");
        return clone;
    }

    @Benchmark
    public String remove() {
        CharObjectHashMap<String> clone = map.clone();
        char k = randomKey();
        return clone.remove(k);
    }
}

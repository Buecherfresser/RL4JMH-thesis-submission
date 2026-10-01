package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.CharObjectHashMap;
import com.carrotsearch.hppc.cursors.CharObjectCursor;
import com.carrotsearch.hppc.procedures.CharObjectProcedure;
import com.carrotsearch.hppc.predicates.CharObjectPredicate;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharObjectHashMapBenchmark {

    private static final int MAP_SIZE = 1000;
    private static final int SEED = 12345;

    private CharObjectHashMap<String> map;
    private CharObjectHashMap<String> otherMap;
    private char[] keys;
    private String[] values;
    private char existingKey;
    private char missingKey;
    private int existingIndex;
    private String newValue;
    private CharObjectHashMap<String> cloneMap;
    private CharObjectHashMap<String> equalMap;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(SEED);
        keys = new char[MAP_SIZE];
        values = new String[MAP_SIZE];
        map = new CharObjectHashMap<>(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            char key;
            do {
                key = (char) (random.nextInt(Character.MAX_VALUE) + 1); // avoid 0
            } while (map.containsKey(key));
            keys[i] = key;
            values[i] = "value" + i;
            map.put(key, values[i]);
        }
        existingKey = keys[0];
        missingKey = (char) (existingKey + 1);
        while (map.containsKey(missingKey)) {
            missingKey++;
        }
        existingIndex = map.indexOf(existingKey);
        newValue = "newValue";
        otherMap = new CharObjectHashMap<>(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            otherMap.put(keys[i], values[i]);
        }
        cloneMap = map.clone();
        equalMap = map.clone();
    }

    @Benchmark
    public String putOverwrite() {
        return map.put(existingKey, newValue);
    }

    @Benchmark
    public String get() {
        return map.get(existingKey);
    }

    @Benchmark
    public String getOrDefault() {
        return map.getOrDefault(existingKey, "default");
    }

    @Benchmark
    public boolean containsKey() {
        return map.containsKey(existingKey);
    }

    @Benchmark
    public int indexOf() {
        return map.indexOf(existingKey);
    }

    @Benchmark
    public String indexGet() {
        return map.indexGet(existingIndex);
    }

    @Benchmark
    public String indexReplace() {
        return map.indexReplace(existingIndex, newValue);
    }

    @Benchmark
    public int forEachProcedure(Blackhole bh) {
        final int[] sum = {0};
        map.forEach((CharObjectProcedure<String>) (key, value) -> sum[0] += value.length());
        bh.consume(sum[0]);
        return sum[0];
    }

    @Benchmark
    public boolean forEachPredicate(Blackhole bh) {
        final boolean[] found = {false};
        map.forEach((CharObjectPredicate<String>) (key, value) -> {
            if (value.startsWith("value")) {
                found[0] = true;
                return false; // stop early
            }
            return true;
        });
        bh.consume(found[0]);
        return found[0];
    }

    @Benchmark
    public int iterator(Blackhole bh) {
        int sum = 0;
        for (CharObjectCursor<String> cursor : map) {
            sum += cursor.value.length();
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public int keysView(Blackhole bh) {
        int count = 0;
        for (CharCursor cursor : map.keys()) {
            count += cursor.value;
        }
        bh.consume(count);
        return count;
    }

    @Benchmark
    public int valuesView(Blackhole bh) {
        int sum = 0;
        for (ObjectCursor<String> cursor : map.values()) {
            sum += cursor.value.length();
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public CharObjectHashMap<String> clone() {
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
        return map.equals(equalMap);
    }

    @Benchmark
    public void ensureCapacity(Blackhole bh) {
        map.ensureCapacity(MAP_SIZE + 10);
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

    @Benchmark
    public int putAll() {
        return map.putAll(otherMap);
    }

    @Benchmark
    public CharObjectHashMap<String> from() {
        return CharObjectHashMap.from(keys, values);
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return map.visualizeKeyDistribution(10);
    }

    @Benchmark
    public int size() {
        return map.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return map.isEmpty();
    }
}

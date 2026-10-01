package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.ShortShortHashMap;
import com.carrotsearch.hppc.cursors.ShortShortCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.procedures.ShortShortProcedure;
import com.carrotsearch.hppc.predicates.ShortShortPredicate;
import com.carrotsearch.hppc.predicates.ShortPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortShortHashMapBenchmark {

    private ShortShortHashMap map;
    private short existingKey;
    private short existingValue;
    private short nonExistingKey;
    private int existingIndex;
    private int ensureCapacitySize;
    private ShortShortHashMap equalMap;

    @Setup(Level.Trial)
    public void setup() {
        int size = 1024;
        map = new ShortShortHashMap(size);
        Random rnd = new Random(0);
        for (int i = 0; i < size; i++) {
            short k = (short) (i + 1); // avoid zero (empty slot marker)
            short v = (short) rnd.nextInt(Short.MAX_VALUE);
            map.put(k, v);
        }
        existingKey = 1;
        existingValue = map.get(existingKey);
        nonExistingKey = (short) (size + 1000); // guaranteed absent and non‑zero
        existingIndex = map.indexOf(existingKey);
        ensureCapacitySize = size * 2;
        equalMap = map.clone();
    }

    @Benchmark
    public short getExisting() {
        return map.get(existingKey);
    }

    @Benchmark
    public short getNonExisting() {
        return map.get(nonExistingKey);
    }

    @Benchmark
    public short getOrDefaultExisting() {
        return map.getOrDefault(existingKey, (short) -1);
    }

    @Benchmark
    public short getOrDefaultNonExisting() {
        return map.getOrDefault(nonExistingKey, (short) -1);
    }

    @Benchmark
    public boolean containsExisting() {
        return map.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsNonExisting() {
        return map.containsKey(nonExistingKey);
    }

    @Benchmark
    public short putUpdateExisting() {
        ShortShortHashMap m = map.clone();
        return m.put(existingKey, (short) (existingValue + 1));
    }

    @Benchmark
    public short putOrAddExisting() {
        ShortShortHashMap m = map.clone();
        return m.putOrAdd(existingKey, (short) 0, (short) 1);
    }

    @Benchmark
    public short addToExisting() {
        ShortShortHashMap m = map.clone();
        return m.addTo(existingKey, (short) 1);
    }

    @Benchmark
    public short removeNonExisting() {
        ShortShortHashMap m = map.clone();
        return m.remove(nonExistingKey);
    }

    @Benchmark
    public int indexOfExisting() {
        return map.indexOf(existingKey);
    }

    @Benchmark
    public short indexGetExisting() {
        return map.indexGet(existingIndex);
    }

    @Benchmark
    public short indexReplaceExisting() {
        ShortShortHashMap m = map.clone();
        return m.indexReplace(existingIndex, (short) (existingValue + 2));
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        map.forEach((ShortShortProcedure) (k, v) -> bh.consume(k + v));
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        map.forEach((ShortShortPredicate) (k, v) -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public short iterateLastValue() {
        short last = 0;
        for (ShortShortCursor c : map) {
            last = c.value;
        }
        return last;
    }

    @Benchmark
    public int keysSize() {
        return map.keys().size();
    }

    @Benchmark
    public boolean keysContainsExisting() {
        return map.keys().contains(existingKey);
    }

    @Benchmark
    public short iterateKeysLast() {
        short last = 0;
        for (ShortCursor c : map.keys()) {
            last = c.value;
        }
        return last;
    }

    @Benchmark
    public int valuesSize() {
        return map.values().size();
    }

    @Benchmark
    public boolean valuesContainsExisting() {
        return map.values().contains(existingValue);
    }

    @Benchmark
    public short iterateValuesLast() {
        short last = 0;
        for (ShortCursor c : map.values()) {
            last = c.value;
        }
        return last;
    }

    @Benchmark
    public ShortShortHashMap cloneMap() {
        return map.clone();
    }

    @Benchmark
    public String toStringMap() {
        return map.toString();
    }

    @Benchmark
    public int hashCodeMap() {
        return map.hashCode();
    }

    @Benchmark
    public boolean equalsSelf() {
        return map.equals(equalMap);
    }

    @Benchmark
    public void ensureCapacity() {
        ShortShortHashMap m = map.clone();
        m.ensureCapacity(ensureCapacitySize);
    }

    @Benchmark
    public long ramBytesUsed() {
        return map.ramBytesUsed();
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

package bench.generated.c008;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.CharCharHashMap;
import com.carrotsearch.hppc.cursors.CharCharCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.CharArrayList;
import com.carrotsearch.hppc.CharContainer;
import com.carrotsearch.hppc.procedures.CharCharProcedure;
import com.carrotsearch.hppc.predicates.CharCharPredicate;
import com.carrotsearch.hppc.predicates.CharPredicate;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharCharHashMapBenchmark {

    private int prepopulateSize = 256;

    private CharCharHashMap baseMap;
    private char existingKey;
    private char missingKey;
    private char newKey;
    private int existingIndex;
    private CharCharProcedure noopProcedure;
    private CharCharPredicate alwaysTruePredicate;
    private CharCharPredicate alwaysFalsePredicate;
    private CharPredicate alwaysTrueCharPredicate;
    private CharPredicate alwaysFalseCharPredicate;
    private CharContainer charContainer;

    @Setup
    public void setup() {
        Random rnd = new Random(0x1234ABCD);
        baseMap = new CharCharHashMap(prepopulateSize);
        for (int i = 0; i < prepopulateSize; i++) {
            char k = (char) (rnd.nextInt(0xFFFF) + 1); // avoid zero key
            char v = (char) (rnd.nextInt(0xFFFF));
            baseMap.put(k, v);
            if (i == 0) {
                existingKey = k;
            }
        }
        // Ensure missingKey is not present
        char candidate;
        do {
            candidate = (char) (rnd.nextInt(0xFFFF) + 1);
        } while (baseMap.containsKey(candidate));
        missingKey = candidate;

        // Ensure newKey is also absent
        do {
            candidate = (char) (rnd.nextInt(0xFFFF) + 1);
        } while (baseMap.containsKey(candidate) || candidate == missingKey);
        newKey = candidate;

        existingIndex = baseMap.indexOf(existingKey);
        noopProcedure = (k, v) -> {
            // no-op
        };
        alwaysTruePredicate = (k, v) -> true;
        alwaysFalsePredicate = (k, v) -> false;
        alwaysTrueCharPredicate = (k) -> true;
        alwaysFalseCharPredicate = (k) -> false;

        CharArrayList list = new CharArrayList();
        list.add(existingKey);
        charContainer = list;
    }

    @Benchmark
    public char putExisting() {
        CharCharHashMap m = baseMap.clone();
        return m.put(existingKey, (char) (m.get(existingKey) + 1));
    }

    @Benchmark
    public char putNew() {
        CharCharHashMap m = baseMap.clone();
        return m.put(newKey, (char) 42);
    }

    @Benchmark
    public char getExisting() {
        return baseMap.get(existingKey);
    }

    @Benchmark
    public char getMissing() {
        return baseMap.get(missingKey);
    }

    @Benchmark
    public char getOrDefaultExisting() {
        return baseMap.getOrDefault(existingKey, (char) 0xFF);
    }

    @Benchmark
    public char getOrDefaultMissing() {
        return baseMap.getOrDefault(missingKey, (char) 0xFF);
    }

    @Benchmark
    public boolean containsKeyExisting() {
        return baseMap.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsKeyMissing() {
        return baseMap.containsKey(missingKey);
    }

    @Benchmark
    public char removeExisting() {
        CharCharHashMap m = baseMap.clone();
        return m.remove(existingKey);
    }

    @Benchmark
    public char removeMissing() {
        CharCharHashMap m = baseMap.clone();
        return m.remove(missingKey);
    }

    @Benchmark
    public char putOrAddExisting() {
        CharCharHashMap m = baseMap.clone();
        return m.putOrAdd(existingKey, (char) 1, (char) 2);
    }

    @Benchmark
    public char putOrAddMissing() {
        CharCharHashMap m = baseMap.clone();
        return m.putOrAdd(missingKey, (char) 1, (char) 2);
    }

    @Benchmark
    public char addToExisting() {
        CharCharHashMap m = baseMap.clone();
        return m.addTo(existingKey, (char) 3);
    }

    @Benchmark
    public int indexOfExisting() {
        return baseMap.indexOf(existingKey);
    }

    @Benchmark
    public int indexOfMissing() {
        return baseMap.indexOf(missingKey);
    }

    @Benchmark
    public char indexGet() {
        return baseMap.indexGet(existingIndex);
    }

    @Benchmark
    public char indexReplace() {
        CharCharHashMap m = baseMap.clone();
        int idx = m.indexOf(existingKey);
        return m.indexReplace(idx, (char) 99);
    }

    @Benchmark
    public void indexInsert(Blackhole bh) {
        CharCharHashMap m = baseMap.clone();
        int idx = m.indexOf(missingKey); // negative
        m.indexInsert(idx, missingKey, (char) 77);
        bh.consume(m);
    }

    @Benchmark
    public char indexRemove() {
        CharCharHashMap m = baseMap.clone();
        int idx = m.indexOf(existingKey);
        return m.indexRemove(idx);
    }

    @Benchmark
    public void clear(Blackhole bh) {
        CharCharHashMap m = baseMap.clone();
        m.clear();
        bh.consume(m.size());
    }

    @Benchmark
    public int size() {
        return baseMap.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return baseMap.isEmpty();
    }

    @Benchmark
    public CharCharProcedure forEachProcedure() {
        return baseMap.forEach(noopProcedure);
    }

    @Benchmark
    public CharCharPredicate forEachPredicate() {
        return baseMap.forEach(alwaysTruePredicate);
    }

    @Benchmark
    public void iterateEntries(Blackhole bh) {
        for (CharCharCursor c : baseMap) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateKeys(Blackhole bh) {
        for (CharCursor c : baseMap.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateValues(Blackhole bh) {
        for (CharCursor c : baseMap.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int removeAllCharCharPredicate() {
        CharCharHashMap m = baseMap.clone();
        return m.removeAll(alwaysFalsePredicate);
    }

    @Benchmark
    public int removeAllCharPredicate() {
        CharCharHashMap m = baseMap.clone();
        return m.removeAll(alwaysFalseCharPredicate);
    }

    @Benchmark
    public int removeAllCharContainer() {
        CharCharHashMap m = baseMap.clone();
        return m.removeAll(charContainer);
    }
}

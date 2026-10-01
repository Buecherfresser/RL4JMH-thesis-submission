package bench.generated.c040;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.LongCharHashMap;
import com.carrotsearch.hppc.cursors.LongCharCursor;
import com.carrotsearch.hppc.procedures.LongCharProcedure;
import com.carrotsearch.hppc.predicates.LongCharPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.procedures.CharProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongCharHashMapBenchmark {

    private LongCharHashMap map;
    private long existingKey;
    private char existingValue;
    private int existingIndex;
    private long nonExistingKey;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random(12345L);
        int size = 1024;
        map = new LongCharHashMap(size);
        for (int i = 0; i < size; i++) {
            long k = random.nextLong();
            char v = (char) (i & 0xFFFF);
            map.put(k, v);
            if (i == 0) {
                existingKey = k;
                existingValue = v;
            }
        }
        long candidate;
        do {
            candidate = random.nextLong();
        } while (map.containsKey(candidate));
        nonExistingKey = candidate;
        existingIndex = map.indexOf(existingKey);
    }

    @Benchmark
    public char benchPutExisting() {
        return map.put(existingKey, (char) (existingValue + 1));
    }

    @Benchmark
    public char benchGetExisting() {
        return map.get(existingKey);
    }

    @Benchmark
    public char benchGetMissing() {
        return map.get(nonExistingKey);
    }

    @Benchmark
    public char benchGetOrDefaultExisting() {
        return map.getOrDefault(existingKey, (char) 0);
    }

    @Benchmark
    public char benchGetOrDefaultMissing() {
        return map.getOrDefault(nonExistingKey, (char) 42);
    }

    @Benchmark
    public boolean benchContainsKeyExisting() {
        return map.containsKey(existingKey);
    }

    @Benchmark
    public boolean benchContainsKeyMissing() {
        return map.containsKey(nonExistingKey);
    }

    @Benchmark
    public char benchRemoveMissing() {
        return map.remove(nonExistingKey);
    }

    @Benchmark
    public int benchSize() {
        return map.size();
    }

    @Benchmark
    public boolean benchIsEmpty() {
        return map.isEmpty();
    }

    @Benchmark
    public void benchClear(Blackhole bh) {
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public void benchForEachProcedure(Blackhole bh) {
        map.forEach((LongCharProcedure) (k, v) -> bh.consume(k ^ v));
    }

    @Benchmark
    public void benchForEachPredicate(Blackhole bh) {
        map.forEach((LongCharPredicate) (k, v) -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public void benchIterate(Blackhole bh) {
        for (LongCharCursor c : map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public boolean benchKeysContains() {
        return map.keys().contains(existingKey);
    }

    @Benchmark
    public void benchKeysForEach(Blackhole bh) {
        map.keys().forEach((LongProcedure) k -> bh.consume(k));
    }

    @Benchmark
    public boolean benchValuesContains() {
        return map.values().contains(existingValue);
    }

    @Benchmark
    public void benchValuesForEach(Blackhole bh) {
        map.values().forEach((CharProcedure) v -> bh.consume(v));
    }

    @Benchmark
    public int benchIndexOfExisting() {
        return map.indexOf(existingKey);
    }

    @Benchmark
    public char benchIndexGet() {
        return map.indexGet(existingIndex);
    }

    @Benchmark
    public char benchIndexReplace() {
        char current = map.indexGet(existingIndex);
        return map.indexReplace(existingIndex, (char) (current + 1));
    }

    @Benchmark
    public void benchEnsureCapacity() {
        map.ensureCapacity(map.size() + 1000);
    }
}

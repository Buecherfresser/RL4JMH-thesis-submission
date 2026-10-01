package bench.generated.c033;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.IntLongHashMap;
import com.carrotsearch.hppc.cursors.IntLongCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.procedures.IntLongProcedure;
import com.carrotsearch.hppc.predicates.IntLongPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntLongHashMapBenchmark {
    private IntLongHashMap map;
    private int[] keys;
    private long[] values;
    private int existingKey;
    private long existingValue;

    @Setup(Level.Trial)
    public void setUp() {
        int size = 1024;
        map = new IntLongHashMap(size);
        keys = new int[size];
        values = new long[size];
        Random rnd = new Random(12345L);
        for (int i = 0; i < size; i++) {
            int k = rnd.nextInt();
            long v = rnd.nextLong();
            keys[i] = k;
            values[i] = v;
            map.put(k, v);
        }
        existingKey = keys[0];
        existingValue = values[0];
    }

    @Benchmark
    public long get() {
        return map.get(existingKey);
    }

    @Benchmark
    public long getOrDefault() {
        return map.getOrDefault(existingKey, -1L);
    }

    @Benchmark
    public boolean containsKey() {
        return map.containsKey(existingKey);
    }

    @Benchmark
    public long putExisting() {
        return map.put(existingKey, existingValue + 1);
    }

    @Benchmark
    public long addToExisting() {
        return map.addTo(existingKey, 1L);
    }

    @Benchmark
    public long putOrAddExisting() {
        return map.putOrAdd(existingKey, 0L, 1L);
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
    public void iterate(Blackhole bh) {
        for (IntLongCursor c : map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        map.forEach((IntLongProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        map.forEach((IntLongPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void keysIteration(Blackhole bh) {
        for (IntCursor ic : map.keys()) {
            bh.consume(ic.value);
        }
    }

    @Benchmark
    public void valuesIteration(Blackhole bh) {
        for (LongCursor lc : map.values()) {
            bh.consume(lc.value);
        }
    }

    @Benchmark
    public int indexOfExisting() {
        return map.indexOf(existingKey);
    }

    @Benchmark
    public long indexGetExisting() {
        int idx = map.indexOf(existingKey);
        return map.indexGet(idx);
    }

    @Benchmark
    public long indexReplaceExisting() {
        int idx = map.indexOf(existingKey);
        return map.indexReplace(idx, existingValue + 2);
    }

    @Benchmark
    public void indexInsertNew(Blackhole bh) {
        IntLongHashMap clone = map.clone();
        int newKey = Integer.MAX_VALUE - clone.size();
        int idx = clone.indexOf(newKey);
        clone.indexInsert(idx, newKey, 123L);
        bh.consume(clone.size());
    }

    @Benchmark
    public void indexRemoveExisting(Blackhole bh) {
        IntLongHashMap clone = map.clone();
        int idx = clone.indexOf(existingKey);
        long removed = clone.indexRemove(idx);
        bh.consume(removed);
    }

    @Benchmark
    public void clearClone(Blackhole bh) {
        IntLongHashMap clone = map.clone();
        clone.clear();
        bh.consume(clone.size());
    }

    @Benchmark
    public void ensureCapacity(Blackhole bh) {
        IntLongHashMap clone = map.clone();
        clone.ensureCapacity(clone.size() * 2);
        bh.consume(clone.size());
    }
}

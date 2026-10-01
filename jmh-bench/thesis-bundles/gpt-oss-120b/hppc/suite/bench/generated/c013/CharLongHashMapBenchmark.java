package bench.generated.c013;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharLongHashMap;
import com.carrotsearch.hppc.CharArrayList;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharLongHashMapBenchmark {

    private static final int DEFAULT_SIZE = 1024;

    private CharLongHashMap baseMap;
    private CharLongHashMap map; // mutable copy for mutating benchmarks
    private char[] keys;
    private long[] values;
    private CharArrayList removalKeys;
    private final Random rand = new Random(0);
    private int idx = 0;

    @Setup(Level.Trial)
    public void setUpTrial() {
        keys = new char[DEFAULT_SIZE];
        values = new long[DEFAULT_SIZE];
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            char k;
            do {
                k = (char) rand.nextInt(Character.MAX_VALUE + 1);
            } while (k == 0);
            keys[i] = k;
            values[i] = rand.nextLong();
        }

        baseMap = new CharLongHashMap(DEFAULT_SIZE);
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            baseMap.put(keys[i], values[i]);
        }

        removalKeys = new CharArrayList();
        for (int i = 0; i < DEFAULT_SIZE / 4; i++) {
            removalKeys.add(keys[i]);
        }
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
        map = baseMap.clone();
    }

    @Benchmark
    public long get() {
        char key = keys[idx];
        idx = (idx + 1) & (DEFAULT_SIZE - 1);
        return baseMap.get(key);
    }

    @Benchmark
    public long getOrDefault() {
        char key = keys[idx];
        idx = (idx + 1) & (DEFAULT_SIZE - 1);
        return baseMap.getOrDefault(key, -1L);
    }

    @Benchmark
    public boolean containsKey() {
        char key = keys[idx];
        idx = (idx + 1) & (DEFAULT_SIZE - 1);
        return baseMap.containsKey(key);
    }

    @Benchmark
    public long put() {
        char newKey = (char) (Character.MAX_VALUE - idx);
        long newVal = idx;
        idx = (idx + 1) & (DEFAULT_SIZE - 1);
        return map.put(newKey, newVal);
    }

    @Benchmark
    public long putOrAdd() {
        char newKey = (char) (Character.MAX_VALUE - idx);
        long putVal = idx;
        long incVal = 1L;
        idx = (idx + 1) & (DEFAULT_SIZE - 1);
        return map.putOrAdd(newKey, putVal, incVal);
    }

    @Benchmark
    public long addTo() {
        char newKey = (char) (Character.MAX_VALUE - idx);
        long inc = 1L;
        idx = (idx + 1) & (DEFAULT_SIZE - 1);
        return map.addTo(newKey, inc);
    }

    @Benchmark
    public long remove() {
        char key = keys[idx];
        idx = (idx + 1) & (DEFAULT_SIZE - 1);
        return map.remove(key);
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
    public void clear(Blackhole bh) {
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public long iterate() {
        long sum = 0L;
        for (com.carrotsearch.hppc.cursors.CharLongCursor c : baseMap) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long forEachProcedure() {
        final long[] sum = new long[1];
        com.carrotsearch.hppc.procedures.CharLongProcedure proc = (k, v) -> sum[0] += v;
        baseMap.forEach(proc);
        return sum[0];
    }

    @Benchmark
    public long forEachPredicate() {
        final long[] sum = new long[1];
        com.carrotsearch.hppc.predicates.CharLongPredicate pred = (k, v) -> {
            sum[0] += v;
            return true;
        };
        baseMap.forEach(pred);
        return sum[0];
    }

    @Benchmark
    public long keysIteration() {
        long sum = 0L;
        for (com.carrotsearch.hppc.cursors.CharCursor c : baseMap.keys()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long valuesIteration() {
        long sum = 0L;
        for (com.carrotsearch.hppc.cursors.LongCursor c : baseMap.values()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public CharLongHashMap cloneMap() {
        return baseMap.clone();
    }

    @Benchmark
    public boolean equalsClone() {
        CharLongHashMap other = baseMap.clone();
        return baseMap.equals(other);
    }

    @Benchmark
    public int hashCodeMap() {
        return baseMap.hashCode();
    }

    @Benchmark
    public int removeAllPredicate() {
        com.carrotsearch.hppc.predicates.CharLongPredicate pred = (k, v) -> (v & 1L) == 0L;
        return map.removeAll(pred);
    }

    @Benchmark
    public int removeAllContainer() {
        return map.removeAll(removalKeys);
    }
}

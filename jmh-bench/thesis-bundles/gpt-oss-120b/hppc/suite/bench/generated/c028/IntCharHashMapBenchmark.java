package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.IntCharHashMap;
import com.carrotsearch.hppc.cursors.IntCharCursor;
import com.carrotsearch.hppc.procedures.IntCharProcedure;
import com.carrotsearch.hppc.predicates.IntCharPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntCharHashMapBenchmark {

    private static final int INITIAL_SIZE = 1024;

    private IntCharHashMap map;
    private int[] keys;
    private char[] values;
    private int nextKey;
    private int queryIndex;
    private Random random;

    @Setup(Level.Trial)
    public void setUp() {
        map = new IntCharHashMap(INITIAL_SIZE);
        keys = new int[INITIAL_SIZE];
        values = new char[INITIAL_SIZE];
        random = new Random(0x1234ABCDL);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            int k = random.nextInt(Integer.MAX_VALUE - 1) + 1; // avoid zero key
            char v = (char) (i & 0xFFFF);
            map.put(k, v);
            keys[i] = k;
            values[i] = v;
        }
        nextKey = Integer.MAX_VALUE - 1000; // start from a high range to avoid collisions
        queryIndex = 0;
    }

    @Benchmark
    public char benchmarkPut() {
        int k = nextKey++;
        char v = (char) (k & 0xFFFF);
        return map.put(k, v);
    }

    @Benchmark
    public char benchmarkGetExisting() {
        int idx = (queryIndex++) % INITIAL_SIZE;
        int k = keys[idx];
        return map.get(k);
    }

    @Benchmark
    public char benchmarkGetMissing() {
        // use a key that is guaranteed not to be present (zero is special, avoid it)
        return map.get(-1);
    }

    @Benchmark
    public boolean benchmarkContainsKeyExisting() {
        int idx = (queryIndex++) % INITIAL_SIZE;
        int k = keys[idx];
        return map.containsKey(k);
    }

    @Benchmark
    public boolean benchmarkContainsKeyMissing() {
        return map.containsKey(-1);
    }

    @Benchmark
    public char benchmarkRemoveExisting() {
        int idx = (queryIndex++) % INITIAL_SIZE;
        int k = keys[idx];
        return map.remove(k);
    }

    @Benchmark
    public void benchmarkClear(Blackhole bh) {
        map.clear();
        bh.consume(map.size());
    }

    @Benchmark
    public int benchmarkSize() {
        return map.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return map.isEmpty();
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        for (IntCharCursor c : map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        map.forEach((IntCharProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        map.forEach((IntCharPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true; // continue iteration
        });
    }

    @Benchmark
    public int benchmarkKeysViewSize() {
        return map.keys().size();
    }

    @Benchmark
    public boolean benchmarkValuesViewContains() {
        // check for a value that is known to exist
        char v = values[0];
        return map.values().contains(v);
    }

    @Benchmark
    public char benchmarkPutOrAddExisting() {
        int idx = (queryIndex++) % INITIAL_SIZE;
        int k = keys[idx];
        char putVal = (char) (k & 0xFF);
        char incVal = (char) 1;
        return map.putOrAdd(k, putVal, incVal);
    }

    @Benchmark
    public char benchmarkAddToExisting() {
        int idx = (queryIndex++) % INITIAL_SIZE;
        int k = keys[idx];
        char inc = (char) 1;
        return map.addTo(k, inc);
    }

    @Benchmark
    public int benchmarkPutAllFromArrays() {
        int[] ks = new int[128];
        char[] vs = new char[128];
        for (int i = 0; i < ks.length; i++) {
            ks[i] = nextKey++;
            vs[i] = (char) (ks[i] & 0xFFFF);
        }
        IntCharHashMap other = IntCharHashMap.from(ks, vs);
        map.putAll(other);
        return map.size();
    }

    @Benchmark
    public int benchmarkCloneSize() {
        IntCharHashMap cloned = map.clone();
        return cloned.size();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return map.hashCode();
    }

    @Benchmark
    public boolean benchmarkEqualsClone() {
        IntCharHashMap cloned = map.clone();
        return map.equals(cloned);
    }
}

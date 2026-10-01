package bench.generated.c077;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortIntHashMap;
import com.carrotsearch.hppc.ShortArrayList;
import com.carrotsearch.hppc.predicates.ShortIntPredicate;
import com.carrotsearch.hppc.procedures.ShortIntProcedure;
import com.carrotsearch.hppc.predicates.ShortPredicate;
import com.carrotsearch.hppc.cursors.ShortIntCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.cursors.IntCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortIntHashMapBenchmark {

    private static int ENTRY_COUNT = 1024;

    @State(Scope.Benchmark)
    public static class ReadState {
        ShortIntHashMap map;
        short[] keys;
        int[] values;

        @Setup(Level.Trial)
        public void setUp() {
            keys = new short[ENTRY_COUNT];
            values = new int[ENTRY_COUNT];
            map = new ShortIntHashMap(ENTRY_COUNT);
            for (int i = 0; i < ENTRY_COUNT; i++) {
                short k = (short) i;
                int v = i * 10;
                keys[i] = k;
                values[i] = v;
                map.put(k, v);
            }
        }
    }

    @State(Scope.Benchmark)
    public static class WriteState {
        ShortIntHashMap map;
        short[] keys;
        int[] values;

        @Setup(Level.Trial)
        public void trialSetup() {
            keys = new short[ENTRY_COUNT];
            values = new int[ENTRY_COUNT];
            for (int i = 0; i < ENTRY_COUNT; i++) {
                keys[i] = (short) i;
                values[i] = i * 10;
            }
            map = new ShortIntHashMap(ENTRY_COUNT);
        }

        @Setup(Level.Invocation)
        public void invocationSetup() {
            map.clear();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], values[i]);
            }
        }
    }

    @Benchmark
    public int benchGet(ReadState s) {
        return s.map.get(s.keys[0]);
    }

    @Benchmark
    public int benchGetOrDefault(ReadState s) {
        short missing = (short) (s.keys.length + 1);
        return s.map.getOrDefault(missing, -1);
    }

    @Benchmark
    public int benchContainsKey(ReadState s) {
        return s.map.containsKey(s.keys[0]) ? 1 : 0;
    }

    @Benchmark
    public int benchSize(ReadState s) {
        return s.map.size();
    }

    @Benchmark
    public int benchIsEmpty(ReadState s) {
        return s.map.isEmpty() ? 1 : 0;
    }

    @Benchmark
    public int benchHashCode(ReadState s) {
        return s.map.hashCode();
    }

    @Benchmark
    public int benchEquals(ReadState s) {
        ShortIntHashMap other = s.map.clone();
        return s.map.equals(other) ? 1 : 0;
    }

    @Benchmark
    public int benchIndexOf(ReadState s) {
        return s.map.indexOf(s.keys[0]);
    }

    @Benchmark
    public int benchIndexExists(ReadState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexExists(idx) ? 1 : 0;
    }

    @Benchmark
    public int benchIndexGet(ReadState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexGet(idx);
    }

    @Benchmark
    public int benchIndexReplace(ReadState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexReplace(idx, 777);
    }

    @Benchmark
    public void benchForEachProcedure(ReadState s, Blackhole bh) {
        s.map.forEach((ShortIntProcedure) (k, v) -> bh.consume(k + v));
    }

    @Benchmark
    public void benchForEachPredicate(ReadState s, Blackhole bh) {
        s.map.forEach((ShortIntPredicate) (k, v) -> {
            if ((v & 1) == 0) {
                bh.consume(k);
            }
            return true;
        });
    }

    @Benchmark
    public void benchIterate(ReadState s, Blackhole bh) {
        for (ShortIntCursor c : s.map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchKeysIterate(ReadState s, Blackhole bh) {
        for (ShortCursor c : s.map.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchValuesIterate(ReadState s, Blackhole bh) {
        for (IntCursor c : s.map.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchKeysContains(ReadState s) {
        return s.map.keys().contains(s.keys[0]) ? 1 : 0;
    }

    @Benchmark
    public int benchValuesContains(ReadState s) {
        return s.map.values().contains(s.values[0]) ? 1 : 0;
    }

    @Benchmark
    public int benchPut(WriteState s) {
        short newKey = (short) (s.keys.length + 1);
        return s.map.put(newKey, 12345);
    }

    @Benchmark
    public int benchPutOrAddExisting(WriteState s) {
        short key = s.keys[0];
        return s.map.putOrAdd(key, 5, 3);
    }

    @Benchmark
    public int benchPutOrAddMissing(WriteState s) {
        short missing = (short) (s.keys.length + 2);
        return s.map.putOrAdd(missing, 5, 3);
    }

    @Benchmark
    public int benchAddTo(WriteState s) {
        short key = s.keys[0];
        return s.map.addTo(key, 2);
    }

    @Benchmark
    public int benchRemove(WriteState s) {
        short key = s.keys[0];
        return s.map.remove(key);
    }

    @Benchmark
    public int benchRemoveAllByContainer(WriteState s) {
        ShortArrayList container = new ShortArrayList();
        container.add(s.keys[0]);
        container.add(s.keys[1]);
        return s.map.removeAll(container);
    }

    @Benchmark
    public int benchRemoveAllByShortIntPredicate(WriteState s) {
        ShortIntPredicate pred = (k, v) -> (v & 1) == 0;
        return s.map.removeAll(pred);
    }

    @Benchmark
    public int benchRemoveAllByShortPredicate(WriteState s) {
        ShortPredicate pred = k -> (k & 1) == 0;
        return s.map.removeAll(pred);
    }

    @Benchmark
    public int benchIndexInsert(WriteState s) {
        short missing = (short) (s.keys.length + 3);
        int idx = s.map.indexOf(missing);
        s.map.indexInsert(idx, missing, 999);
        return s.map.get(missing);
    }

    @Benchmark
    public int benchIndexRemove(WriteState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexRemove(idx);
    }

    @Benchmark
    public int benchClear(WriteState s) {
        s.map.clear();
        return s.map.size();
    }

    @Benchmark
    public int benchRelease(WriteState s) {
        s.map.release();
        return s.map.size();
    }

    @Benchmark
    public int benchKeysRemoveAllPredicate(WriteState s) {
        return s.map.keys().removeAll((ShortPredicate) k -> (k & 1) == 0);
    }

    @Benchmark
    public int benchValuesRemoveAllPredicate(WriteState s) {
        return s.map.values().removeAll(v -> (v & 1) == 0);
    }

    @Benchmark
    public int benchValuesRemoveAllInt(WriteState s) {
        return s.map.values().removeAll(s.values[0]);
    }
}

package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortObjectHashMap;
import com.carrotsearch.hppc.cursors.ShortObjectCursor;
import com.carrotsearch.hppc.procedures.ShortObjectProcedure;
import com.carrotsearch.hppc.procedures.ShortProcedure;
import com.carrotsearch.hppc.procedures.ObjectProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortObjectHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ShortObjectHashMap<String> map;
        ShortObjectHashMap<String> mapForClear;
        short[] keys;
        String[] values;
        final int size = 1024;
        int idx = 0;

        @Setup(Level.Trial)
        public void setUp() {
            map = new ShortObjectHashMap<>(size);
            mapForClear = new ShortObjectHashMap<>(size);
            keys = new short[size];
            values = new String[size];
            for (int i = 0; i < size; i++) {
                short k = (short) (i + 1);
                String v = "val" + i;
                keys[i] = k;
                values[i] = v;
                map.put(k, v);
                mapForClear.put(k, v);
            }
        }

        int nextIndex() {
            int i = idx;
            idx = (idx + 1) % size;
            return i;
        }
    }

    @Benchmark
    public String benchGet(BenchmarkState s, Blackhole bh) {
        int i = s.nextIndex();
        short key = s.keys[i];
        String v = s.map.get(key);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public String benchPutReplace(BenchmarkState s, Blackhole bh) {
        int i = s.nextIndex();
        short key = s.keys[i];
        String newVal = "new" + i;
        String old = s.map.put(key, newVal);
        bh.consume(old);
        return old;
    }

    @Benchmark
    public boolean benchContainsKey(BenchmarkState s, Blackhole bh) {
        int i = s.nextIndex();
        short key = s.keys[i];
        boolean b = s.map.containsKey(key);
        bh.consume(b);
        return b;
    }

    @Benchmark
    public String benchRemoveAndReinsert(BenchmarkState s, Blackhole bh) {
        int i = s.nextIndex();
        short key = s.keys[i];
        String removed = s.map.remove(key);
        s.map.put(key, s.values[i]);
        bh.consume(removed);
        return removed;
    }

    @Benchmark
    public void benchClear(BenchmarkState s, Blackhole bh) {
        s.mapForClear.clear();
        bh.consume(s.mapForClear.size());
    }

    @Benchmark
    public ShortObjectHashMap<String> benchClone(BenchmarkState s, Blackhole bh) {
        ShortObjectHashMap<String> cloned = s.map.clone();
        bh.consume(cloned.size());
        return cloned;
    }

    @Benchmark
    public int benchForEach(BenchmarkState s, Blackhole bh) {
        s.map.forEach((ShortObjectProcedure<String>) (k, v) -> {
            // no‑op
        });
        int sz = s.map.size();
        bh.consume(sz);
        return sz;
    }

    @Benchmark
    public int benchIteratorNext(BenchmarkState s, Blackhole bh) {
        ShortObjectCursor<String> cursor = s.map.iterator().next();
        int key = cursor.key;
        bh.consume(key);
        return key;
    }

    @Benchmark
    public int benchValuesForEach(BenchmarkState s, Blackhole bh) {
        s.map.values().forEach((ObjectProcedure<String>) v -> {
            // no‑op
        });
        int sz = s.map.size();
        bh.consume(sz);
        return sz;
    }

    @Benchmark
    public int benchKeysForEach(BenchmarkState s, Blackhole bh) {
        s.map.keys().forEach((ShortProcedure) k -> {
            // no‑op
        });
        int sz = s.map.size();
        bh.consume(sz);
        return sz;
    }
}

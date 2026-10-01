package bench.generated.c046;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.cursors.LongObjectCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.procedures.LongObjectProcedure;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongObjectHashMapBenchmark {

    private int size = 1024;

    /* ---------------------------------------------------------------------- */
    /* State for read‑only operations (get, containsKey, iteration, etc.)      */
    /* ---------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class ReadOnlyState {
        LongObjectHashMap<Object> map;
        long[] keys;
        int index;
        Random rnd = new Random(12345);

        @Setup(Level.Trial)
        public void setUp() {
            map = new LongObjectHashMap<>(1024);
            keys = new long[1024];
            for (int i = 0; i < 1024; i++) {
                long k = rnd.nextLong();
                keys[i] = k;
                map.put(k, new Object());
            }
            index = 0;
        }

        long nextKey() {
            long k = keys[index];
            index = (index + 1) & (1024 - 1);
            return k;
        }
    }

    /* ---------------------------------------------------------------------- */
    /* State for mutating put operation (creates a fresh map per invocation)   */
    /* ---------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class PutState {
        LongObjectHashMap<Object> map;
        long key;
        Object value;
        Random rnd = new Random(54321);

        @Setup(Level.Invocation)
        public void setUp() {
            map = new LongObjectHashMap<>(1);
            key = rnd.nextLong();
            value = new Object();
        }
    }

    /* ---------------------------------------------------------------------- */
    /* State for mutating remove operation (creates a fresh map per invocation)*/
    /* ---------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class RemoveState {
        LongObjectHashMap<Object> map;
        long[] keys;
        int index;

        @Setup(Level.Invocation)
        public void setUp() {
            map = new LongObjectHashMap<>(1024);
            keys = new long[1024];
            for (int i = 0; i < 1024; i++) {
                long k = i;
                keys[i] = k;
                map.put(k, new Object());
            }
            index = 0;
        }

        long nextKey() {
            long k = keys[index];
            index = (index + 1) & (1024 - 1);
            return k;
        }
    }

    /* ---------------------------------------------------------------------- */
    /* State for clone / toString / hashCode / equals benchmarks               */
    /* ---------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class CloneState {
        LongObjectHashMap<Object> map;
        LongObjectHashMap<Object> copy;

        @Setup(Level.Trial)
        public void setUp() {
            map = new LongObjectHashMap<>(1024);
            for (int i = 0; i < 1024; i++) {
                map.put(i, new Object());
            }
            copy = map.clone();
        }
    }

    /* ---------------------------------------------------------------------- */
    /* Benchmarks                                                            */
    /* ---------------------------------------------------------------------- */

    @Benchmark
    public Object benchmarkGet(ReadOnlyState s) {
        return s.map.get(s.nextKey());
    }

    @Benchmark
    public Object benchmarkGetOrDefault(ReadOnlyState s) {
        return s.map.getOrDefault(s.nextKey(), new Object());
    }

    @Benchmark
    public boolean benchmarkContainsKey(ReadOnlyState s) {
        return s.map.containsKey(s.nextKey());
    }

    @Benchmark
    public int benchmarkSize(ReadOnlyState s) {
        return s.map.size();
    }

    @Benchmark
    public Object benchmarkPut(PutState s) {
        return s.map.put(s.key, s.value);
    }

    @Benchmark
    public Object benchmarkRemove(RemoveState s) {
        return s.map.remove(s.nextKey());
    }

    @Benchmark
    public void benchmarkIterateForEach(ReadOnlyState s, Blackhole bh) {
        s.map.forEach((LongObjectProcedure<Object>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkIterateIterator(ReadOnlyState s, Blackhole bh) {
        for (LongObjectCursor<Object> c : s.map) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkIterateKeys(ReadOnlyState s, Blackhole bh) {
        Iterator<LongCursor> it = s.map.keys().iterator();
        while (it.hasNext()) {
            LongCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkIterateValues(ReadOnlyState s, Blackhole bh) {
        Iterator<ObjectCursor<Object>> it = s.map.values().iterator();
        while (it.hasNext()) {
            bh.consume(it.next().value);
        }
    }

    @Benchmark
    public LongObjectHashMap<Object> benchmarkClone(CloneState s) {
        return s.map.clone();
    }

    @Benchmark
    public String benchmarkToString(CloneState s) {
        return s.map.toString();
    }

    @Benchmark
    public int benchmarkHashCode(CloneState s) {
        return s.map.hashCode();
    }

    @Benchmark
    public boolean benchmarkEquals(CloneState s) {
        return s.map.equals(s.copy);
    }

    @Benchmark
    public void benchmarkClear(ReadOnlyState s) {
        s.map.clear();
    }

    @Benchmark
    public void benchmarkEnsureCapacity(ReadOnlyState s) {
        s.map.ensureCapacity(size * 2);
    }
}

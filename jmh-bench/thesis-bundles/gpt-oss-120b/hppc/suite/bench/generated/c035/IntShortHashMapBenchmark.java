package bench.generated.c035;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntShortHashMap;
import com.carrotsearch.hppc.cursors.IntShortCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.procedures.IntShortProcedure;
import com.carrotsearch.hppc.predicates.IntShortPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntShortHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class SharedState {
        IntShortHashMap map;
        int[] keys;
        short[] values;
        int existingKey;
        int nonExistingKey;

        @Setup(Level.Trial)
        public void setUp() {
            int size = 1024;
            map = new IntShortHashMap(size);
            keys = new int[size];
            values = new short[size];
            Random rand = new Random(12345L);
            for (int i = 0; i < size; i++) {
                int k = rand.nextInt(Integer.MAX_VALUE - 1) + 1; // avoid zero (special key)
                short v = (short) rand.nextInt(Short.MAX_VALUE + 1);
                keys[i] = k;
                values[i] = v;
                map.put(k, v);
            }
            existingKey = keys[0];
            nonExistingKey = 0;
            if (map.containsKey(0)) {
                nonExistingKey = keys[1];
            }
        }
    }

    @State(Scope.Benchmark)
    public static class ClearState {
        IntShortHashMap map;

        @Setup(Level.Invocation)
        public void setUp() {
            int size = 1024;
            map = new IntShortHashMap(size);
            Random rand = new Random(12345L);
            for (int i = 0; i < size; i++) {
                int k = rand.nextInt(Integer.MAX_VALUE - 1) + 1;
                short v = (short) rand.nextInt(Short.MAX_VALUE + 1);
                map.put(k, v);
            }
        }
    }

    @State(Scope.Benchmark)
    public static class RemoveAllState {
        IntShortHashMap map;

        @Setup(Level.Invocation)
        public void setUp() {
            int size = 1024;
            map = new IntShortHashMap(size);
            Random rand = new Random(12345L);
            for (int i = 0; i < size; i++) {
                int k = rand.nextInt(Integer.MAX_VALUE - 1) + 1;
                short v = (short) rand.nextInt(Short.MAX_VALUE + 1);
                map.put(k, v);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Read‑only operations
    // -------------------------------------------------------------------------

    @Benchmark
    public short get(SharedState s) {
        return s.map.get(s.existingKey);
    }

    @Benchmark
    public short getOrDefault(SharedState s) {
        return s.map.getOrDefault(s.nonExistingKey, (short) -1);
    }

    @Benchmark
    public boolean containsKey(SharedState s) {
        return s.map.containsKey(s.existingKey);
    }

    @Benchmark
    public int size(SharedState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean isEmpty(SharedState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public long iterateSum(SharedState s) {
        long sum = 0;
        for (IntShortCursor c : s.map) {
            sum += c.key;
        }
        return sum;
    }

    @Benchmark
    public long keysIterateSum(SharedState s) {
        long sum = 0;
        for (IntCursor c : s.map.keys()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long valuesIterateSum(SharedState s) {
        long sum = 0;
        for (ShortCursor c : s.map.values()) {
            sum += c.value;
        }
        return sum;
    }

    // -------------------------------------------------------------------------
    // Procedure / Predicate based iteration
    // -------------------------------------------------------------------------

    public static class SumProcedure implements IntShortProcedure {
        long sum = 0;

        @Override
        public void apply(int key, short value) {
            sum += key;
        }
    }

    @Benchmark
    public long forEachProcedure(SharedState s) {
        SumProcedure proc = new SumProcedure();
        s.map.forEach(proc);
        return proc.sum;
    }

    public static class CountPredicate implements IntShortPredicate {
        int count = 0;

        @Override
        public boolean apply(int key, short value) {
            count++;
            return true;
        }
    }

    @Benchmark
    public int forEachPredicate(SharedState s) {
        CountPredicate pred = new CountPredicate();
        s.map.forEach(pred);
        return pred.count;
    }

    // -------------------------------------------------------------------------
    // Mutating operations (use existing keys to avoid unbounded growth)
    // -------------------------------------------------------------------------

    @Benchmark
    public short putExisting(SharedState s) {
        return s.map.put(s.existingKey, (short) 123);
    }

    @Benchmark
    public short putOrAddExisting(SharedState s) {
        return s.map.putOrAdd(s.existingKey, (short) 5, (short) 2);
    }

    @Benchmark
    public short addToExisting(SharedState s) {
        return s.map.addTo(s.existingKey, (short) 1);
    }

    @Benchmark
    public short removeNonExisting(SharedState s) {
        return s.map.remove(s.nonExistingKey);
    }

    // -------------------------------------------------------------------------
    // Operations that require a fresh container per invocation
    // -------------------------------------------------------------------------

    @Benchmark
    public int clear(ClearState cs) {
        cs.map.clear();
        return cs.map.size();
    }

    @Benchmark
    public int removeAllPredicate(RemoveAllState rs) {
        int removed = rs.map.removeAll((key, value) -> false);
        return removed;
    }
}

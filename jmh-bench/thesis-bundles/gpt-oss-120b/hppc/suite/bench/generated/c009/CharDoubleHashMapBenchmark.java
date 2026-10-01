package bench.generated.c009;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.CharDoubleHashMap;
import com.carrotsearch.hppc.cursors.CharDoubleCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.CharDoubleProcedure;
import com.carrotsearch.hppc.predicates.CharDoublePredicate;
import com.carrotsearch.hppc.predicates.CharPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharDoubleHashMapBenchmark {

    /** Shared state for read‑only operations. */
    @State(Scope.Benchmark)
    public static class ReadState {
        CharDoubleHashMap map;
        char[] keys;
        double[] values;
        int index;
        int SIZE = 1024;

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(0x1234abcd);
            keys = new char[SIZE];
            values = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                // avoid the special zero key
                keys[i] = (char) (rnd.nextInt(Character.MAX_VALUE - 1) + 1);
                values[i] = rnd.nextDouble();
            }
            map = new CharDoubleHashMap(SIZE);
            for (int i = 0; i < SIZE; i++) {
                map.put(keys[i], values[i]);
            }
            index = 0;
        }

        /** Returns the next key in a round‑robin fashion. */
        char nextKey() {
            char k = keys[index];
            index = (index + 1) & (SIZE - 1);
            return k;
        }

        double nextValue() {
            double v = values[index];
            return v;
        }
    }

    /** State for mutating put operations. */
    @State(Scope.Benchmark)
    public static class PutState {
        CharDoubleHashMap map;
        char[] keys;
        double[] values;
        int index;
        int SIZE = 1024;

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(0xdeadbeef);
            keys = new char[SIZE];
            values = new double[SIZE];
            for (int i = 0; i < SIZE; i++) {
                keys[i] = (char) (rnd.nextInt(Character.MAX_VALUE - 1) + 1);
                values[i] = rnd.nextDouble();
            }
            map = new CharDoubleHashMap(SIZE);
            // pre‑populate so subsequent puts replace existing entries
            for (int i = 0; i < SIZE; i++) {
                map.put(keys[i], values[i]);
            }
            index = 0;
        }

        char nextKey() {
            char k = keys[index];
            index = (index + 1) & (SIZE - 1);
            return k;
        }

        double nextValue() {
            double v = values[index];
            return v;
        }
    }

    /** State for remove benchmark (single entry map). */
    @State(Scope.Benchmark)
    public static class RemoveState {
        CharDoubleHashMap map;
        char key = 'A';
        double value = 1.0;

        @Setup(Level.Trial)
        public void setup() {
            map = new CharDoubleHashMap(4);
            map.put(key, value);
        }
    }

    /** State for clear benchmark. */
    @State(Scope.Benchmark)
    public static class ClearState {
        CharDoubleHashMap map;
        int SIZE = 1024;

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(0xcafebabe);
            map = new CharDoubleHashMap(SIZE);
            for (int i = 0; i < SIZE; i++) {
                char k = (char) (rnd.nextInt(Character.MAX_VALUE - 1) + 1);
                double v = rnd.nextDouble();
                map.put(k, v);
            }
        }
    }

    @Benchmark
    public double benchPut(PutState s) {
        char k = s.nextKey();
        double v = s.nextValue();
        return s.map.put(k, v);
    }

    @Benchmark
    public double benchGet(ReadState s) {
        char k = s.nextKey();
        return s.map.get(k);
    }

    @Benchmark
    public double benchGetOrDefault(ReadState s) {
        char k = s.nextKey();
        return s.map.getOrDefault(k, -1.0);
    }

    @Benchmark
    public boolean benchContainsKey(ReadState s) {
        char k = s.nextKey();
        return s.map.containsKey(k);
    }

    @Benchmark
    public double benchRemove(RemoveState s) {
        return s.map.remove(s.key);
    }

    @Benchmark
    public double benchPutOrAdd(PutState s) {
        char k = s.nextKey();
        double v = s.nextValue();
        return s.map.putOrAdd(k, v, v);
    }

    @Benchmark
    public double benchAddTo(PutState s) {
        char k = s.nextKey();
        double v = s.nextValue();
        return s.map.addTo(k, v);
    }

    @Benchmark
    public int benchSize(ReadState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean benchIsEmpty(ReadState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public void benchClear(ClearState s, Blackhole bh) {
        s.map.clear();
        bh.consume(0);
    }

    @Benchmark
    public double benchIteratorSum(ReadState s) {
        double sum = 0.0;
        for (CharDoubleCursor c : s.map) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public double benchForEachProcedure(ReadState s) {
        final double[] holder = new double[1];
        CharDoubleProcedure proc = (k, v) -> holder[0] += v;
        s.map.forEach(proc);
        return holder[0];
    }

    @Benchmark
    public double benchForEachPredicate(ReadState s) {
        final double[] holder = new double[1];
        CharDoublePredicate pred = (k, v) -> {
            holder[0] += v;
            return true;
        };
        s.map.forEach(pred);
        return holder[0];
    }

    @Benchmark
    public int benchKeysSize(ReadState s) {
        return s.map.keys().size();
    }

    @Benchmark
    public boolean benchKeysContains(ReadState s) {
        char k = s.nextKey();
        return s.map.keys().contains(k);
    }

    @Benchmark
    public int benchKeysIteratorSum(ReadState s) {
        int sum = 0;
        Iterator<CharCursor> it = s.map.keys().iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public int benchValuesSize(ReadState s) {
        return s.map.values().size();
    }

    @Benchmark
    public boolean benchValuesContains(ReadState s) {
        double v = s.nextValue();
        return s.map.values().contains(v);
    }

    @Benchmark
    public double benchValuesIteratorSum(ReadState s) {
        double sum = 0.0;
        Iterator<DoubleCursor> it = s.map.values().iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public int benchClone(ReadState s) {
        CharDoubleHashMap cloned = s.map.clone();
        return cloned.size();
    }

    @Benchmark
    public int benchHashCode(ReadState s) {
        return s.map.hashCode();
    }

    @Benchmark
    public boolean benchEquals(ReadState s) {
        CharDoubleHashMap other = s.map.clone();
        return s.map.equals(other);
    }

    @Benchmark
    public int benchFromFactory(ReadState s) {
        CharDoubleHashMap m = CharDoubleHashMap.from(s.keys, s.values);
        return m.size();
    }
}

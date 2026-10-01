package bench.generated.c039;

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
import java.util.Random;
import com.carrotsearch.hppc.LongByteHashMap;
import com.carrotsearch.hppc.procedures.LongByteProcedure;
import com.carrotsearch.hppc.predicates.LongBytePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongByteHashMapBenchmark {

    /* ---------------------------------------------------------------------- */
    /* Read‑only state – map filled once and reused for read‑only ops.       */
    /* ---------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class ReadOnlyState {
        LongByteHashMap map;
        long[] keys;
        byte[] values;
        int size = 1024;
        int index = 0;

        @Setup(Level.Trial)
        public void setUp() {
            map = new LongByteHashMap(size);
            keys = new long[size];
            values = new byte[size];
            Random rnd = new Random(0);
            for (int i = 0; i < size; i++) {
                long k = rnd.nextLong();
                byte v = (byte) rnd.nextInt(256);
                keys[i] = k;
                values[i] = v;
                map.put(k, v);
            }
        }

        long nextKey() {
            int i = index;
            index = (index + 1) & (size - 1);
            return keys[i];
        }

        byte nextValue() {
            int i = (index - 1 + size) & (size - 1);
            return values[i];
        }

        long missingKey() {
            return Long.MAX_VALUE;
        }
    }

    /* ---------------------------------------------------------------------- */
    /* Mutable state – map cleared each invocation for mutating benchmarks. */
    /* ---------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class MutableState {
        LongByteHashMap map;
        long[] keys;
        byte[] values;
        int size = 1024;
        int index = 0;

        @Setup(Level.Trial)
        public void setUp() {
            map = new LongByteHashMap();
            keys = new long[size];
            values = new byte[size];
            Random rnd = new Random(1);
            for (int i = 0; i < size; i++) {
                long k = rnd.nextLong();
                byte v = (byte) rnd.nextInt(256);
                keys[i] = k;
                values[i] = v;
            }
        }

        @Setup(Level.Invocation)
        public void clearMap() {
            map.clear();
        }

        long nextKey() {
            int i = index;
            index = (index + 1) % size;
            return keys[i];
        }

        byte nextValue() {
            int i = (index - 1 + size) % size;
            return values[i];
        }
    }

    /* ---------------------------------------------------------------------- */
    /* Removal state – map filled each invocation, then a single remove.    */
    /* ---------------------------------------------------------------------- */
    @State(Scope.Benchmark)
    public static class RemovalState {
        LongByteHashMap map;
        long[] keys;
        int size = 1024;
        int index = 0;

        @Setup(Level.Trial)
        public void setUp() {
            keys = new long[size];
            Random rnd = new Random(2);
            for (int i = 0; i < size; i++) {
                keys[i] = rnd.nextLong();
            }
        }

        @Setup(Level.Invocation)
        public void fill() {
            map = new LongByteHashMap();
            for (int i = 0; i < size; i++) {
                map.put(keys[i], (byte) i);
            }
        }

        long nextKey() {
            int i = index;
            index = (index + 1) % size;
            return keys[i];
        }
    }

    /* ---------------------------------------------------------------------- */
    /* Dummy procedure and predicate used by forEach benchmarks.           */
    /* ---------------------------------------------------------------------- */
    public static class NoOpProcedure implements LongByteProcedure {
        @Override
        public void apply(long key, byte value) {
            // no‑op
        }
    }

    public static class TruePredicate implements LongBytePredicate {
        @Override
        public boolean apply(long key, byte value) {
            return true;
        }
    }

    /* ---------------------------------------------------------------------- */
    /* Benchmark methods – read‑only operations                              */
    /* ---------------------------------------------------------------------- */
    @Benchmark
    public byte getExisting(ReadOnlyState s) {
        return s.map.get(s.nextKey());
    }

    @Benchmark
    public byte getMissing(ReadOnlyState s) {
        return s.map.get(s.missingKey());
    }

    @Benchmark
    public byte getOrDefaultExisting(ReadOnlyState s) {
        return s.map.getOrDefault(s.nextKey(), (byte) -1);
    }

    @Benchmark
    public byte getOrDefaultMissing(ReadOnlyState s) {
        return s.map.getOrDefault(s.missingKey(), (byte) -1);
    }

    @Benchmark
    public boolean containsKeyExisting(ReadOnlyState s) {
        return s.map.containsKey(s.nextKey());
    }

    @Benchmark
    public boolean containsKeyMissing(ReadOnlyState s) {
        return s.map.containsKey(s.missingKey());
    }

    @Benchmark
    public int size(ReadOnlyState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean isEmpty(ReadOnlyState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public int indexOfExisting(ReadOnlyState s) {
        return s.map.indexOf(s.nextKey());
    }

    @Benchmark
    public boolean indexExistsTrue(ReadOnlyState s) {
        int idx = s.map.indexOf(s.nextKey());
        return s.map.indexExists(idx);
    }

    @Benchmark
    public byte indexGet(ReadOnlyState s) {
        int idx = s.map.indexOf(s.nextKey());
        return s.map.indexGet(idx);
    }

    @Benchmark
    public byte indexReplace(ReadOnlyState s) {
        int idx = s.map.indexOf(s.nextKey());
        return s.map.indexReplace(idx, (byte) 42);
    }

    @Benchmark
    public LongByteProcedure forEachProcedure(ReadOnlyState s) {
        return s.map.forEach(new NoOpProcedure());
    }

    @Benchmark
    public LongBytePredicate forEachPredicate(ReadOnlyState s) {
        return s.map.forEach(new TruePredicate());
    }

    @Benchmark
    public boolean keysContainsExisting(ReadOnlyState s) {
        return s.map.keys().contains(s.nextKey());
    }

    @Benchmark
    public boolean valuesContainsExisting(ReadOnlyState s) {
        return s.map.values().contains(s.nextValue());
    }

    /* ---------------------------------------------------------------------- */
    /* Benchmark methods – mutating operations                              */
    /* ---------------------------------------------------------------------- */
    @Benchmark
    public byte putNew(MutableState s) {
        return s.map.put(s.nextKey(), s.nextValue());
    }

    @Benchmark
    public byte putOrAddNew(MutableState s) {
        return s.map.putOrAdd(s.nextKey(), s.nextValue(), (byte) 1);
    }

    @Benchmark
    public byte addToNew(MutableState s) {
        return s.map.addTo(s.nextKey(), (byte) 1);
    }

    @Benchmark
    public byte removeExisting(RemovalState s) {
        return s.map.remove(s.nextKey());
    }

    @Benchmark
    public void clearMap(RemovalState s, Blackhole bh) {
        s.map.clear();
        bh.consume(s.map.size());
    }
}

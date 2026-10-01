package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.LongIntHashMap;
import com.carrotsearch.hppc.cursors.LongIntCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.LongIntProcedure;
import com.carrotsearch.hppc.predicates.LongIntPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongIntHashMapBenchmark {

    private LongIntHashMap map;
    private long[] keys;
    private int[] values;
    private int[] indices;
    private int pos;

    @Setup(Level.Trial)
    public void setUp() {
        int size = 1024; // moderate size to keep operations sub‑millisecond
        map = new LongIntHashMap(size);
        keys = new long[size];
        values = new int[size];
        indices = new int[size];
        Random rnd = new Random(0);
        for (int i = 0; i < size; i++) {
            long k = rnd.nextLong();
            int v = rnd.nextInt();
            keys[i] = k;
            values[i] = v;
            map.put(k, v);
        }
        for (int i = 0; i < size; i++) {
            indices[i] = map.indexOf(keys[i]);
        }
        pos = 0;
    }

    private int nextPos() {
        int p = pos;
        pos = (p + 1) & (keys.length - 1);
        return p;
    }

    @Benchmark
    public int getExisting() {
        int i = nextPos();
        return map.get(keys[i]);
    }

    @Benchmark
    public int getOrDefaultExisting() {
        int i = nextPos();
        return map.getOrDefault(keys[i], -1);
    }

    @Benchmark
    public boolean containsKeyExisting() {
        int i = nextPos();
        return map.containsKey(keys[i]);
    }

    @Benchmark
    public int putOrAddExisting() {
        int i = nextPos();
        // update existing entry: add 1 to current value
        return map.putOrAdd(keys[i], 0, 1);
    }

    @Benchmark
    public int addToExisting() {
        int i = nextPos();
        return map.addTo(keys[i], 1);
    }

    @Benchmark
    public int indexOfExisting() {
        int i = nextPos();
        return map.indexOf(keys[i]);
    }

    @Benchmark
    public int indexGetExisting() {
        int i = nextPos();
        return map.indexGet(indices[i]);
    }

    @Benchmark
    public int indexReplaceExisting() {
        int i = nextPos();
        return map.indexReplace(indices[i], 12345);
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        map.forEach(new ConsumingProcedure(bh));
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        map.forEach(new ConsumingPredicate(bh));
    }

    @Benchmark
    public Iterator<LongIntCursor> iterator() {
        return map.iterator();
    }

    @Benchmark
    public Iterator<LongCursor> keysIterator() {
        return map.keys().iterator();
    }

    @Benchmark
    public Iterator<IntCursor> valuesIterator() {
        return map.values().iterator();
    }

    @Benchmark
    public int size() {
        return map.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return map.isEmpty();
    }

    // -------------------------------------------------------------------------
    // Helper procedure / predicate that consumes values via Blackhole.
    // -------------------------------------------------------------------------

    private static class ConsumingProcedure implements LongIntProcedure {
        private final Blackhole bh;
        ConsumingProcedure(Blackhole bh) {
            this.bh = bh;
        }
        @Override
        public void apply(long key, int value) {
            bh.consume(key);
            bh.consume(value);
        }
    }

    private static class ConsumingPredicate implements LongIntPredicate {
        private final Blackhole bh;
        ConsumingPredicate(Blackhole bh) {
            this.bh = bh;
        }
        @Override
        public boolean apply(long key, int value) {
            bh.consume(key);
            bh.consume(value);
            return true; // continue iteration
        }
    }
}

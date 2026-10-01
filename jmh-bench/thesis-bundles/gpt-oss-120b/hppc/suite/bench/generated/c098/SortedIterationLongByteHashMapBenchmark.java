package bench.generated.c098;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationLongByteHashMap;
import com.carrotsearch.hppc.LongByteHashMap;
import com.carrotsearch.hppc.cursors.LongByteCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.procedures.LongByteProcedure;
import com.carrotsearch.hppc.predicates.LongBytePredicate;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongByteComparator;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongByteHashMapBenchmark {

    private LongByteHashMap delegate;
    private SortedIterationLongByteHashMap viewKeyComparator;
    private SortedIterationLongByteHashMap viewKeyValueComparator;
    private long existingKey;
    private long missingKey;
    private LongComparator keyComparator;
    private LongByteComparator keyValueComparator;

    @Setup(Level.Trial)
    public void setup() {
        int size = 1024;
        delegate = new LongByteHashMap(size);
        Random rand = new Random(12345L);
        boolean firstSet = false;
        long firstKey = 0L;
        while (delegate.size() < size) {
            long k = rand.nextLong();
            byte v = (byte) rand.nextInt(256);
            if (!delegate.containsKey(k)) {
                delegate.put(k, v);
                if (!firstSet) {
                    firstKey = k;
                    firstSet = true;
                }
            }
        }
        existingKey = firstKey;
        missingKey = Long.MAX_VALUE;
        keyComparator = (a, b) -> Long.compare(a, b);
        keyValueComparator = (k1, v1, k2, v2) -> {
            int c = Long.compare(k1, k2);
            if (c != 0) return c;
            return Byte.compare(v1, v2);
        };
        viewKeyComparator = new SortedIterationLongByteHashMap(delegate, keyComparator);
        viewKeyValueComparator = new SortedIterationLongByteHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        viewKeyComparator.forEach((LongByteProcedure) (k, v) -> bh.consume(k ^ v));
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        viewKeyComparator.forEach((LongBytePredicate) (k, v) -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        for (LongByteCursor c : viewKeyComparator) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkKeysIterator(Blackhole bh) {
        for (LongCursor c : viewKeyComparator.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIterator(Blackhole bh) {
        for (ByteCursor c : viewKeyComparator.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public byte benchmarkGetExisting(Blackhole bh) {
        byte v = viewKeyComparator.get(existingKey);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public byte benchmarkGetOrDefaultMissing(Blackhole bh) {
        byte v = viewKeyComparator.getOrDefault(missingKey, (byte) -1);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public boolean benchmarkContainsKey(Blackhole bh) {
        boolean b = viewKeyComparator.containsKey(existingKey);
        bh.consume(b);
        return b;
    }

    @Benchmark
    public int benchmarkSize(Blackhole bh) {
        int s = viewKeyComparator.size();
        bh.consume(s);
        return s;
    }

    @Benchmark
    public int benchmarkIndexOf(Blackhole bh) {
        int idx = viewKeyComparator.indexOf(existingKey);
        bh.consume(idx);
        return idx;
    }

    @Benchmark
    public byte benchmarkIndexGet(Blackhole bh) {
        int idx = viewKeyComparator.indexOf(existingKey);
        byte val = viewKeyComparator.indexGet(idx);
        bh.consume(val);
        return val;
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution(Blackhole bh) {
        String s = viewKeyComparator.visualizeKeyDistribution(10);
        bh.consume(s);
        return s;
    }

    @Benchmark
    public SortedIterationLongByteHashMap benchmarkConstructionKeyComparator(Blackhole bh) {
        SortedIterationLongByteHashMap map = new SortedIterationLongByteHashMap(delegate, keyComparator);
        bh.consume(map);
        return map;
    }

    @Benchmark
    public SortedIterationLongByteHashMap benchmarkConstructionKeyValueComparator(Blackhole bh) {
        SortedIterationLongByteHashMap map = new SortedIterationLongByteHashMap(delegate, keyValueComparator);
        bh.consume(map);
        return map;
    }
}

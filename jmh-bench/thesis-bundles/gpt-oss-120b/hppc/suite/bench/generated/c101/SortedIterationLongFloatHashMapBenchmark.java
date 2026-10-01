package bench.generated.c101;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.SortedIterationLongFloatHashMap;
import com.carrotsearch.hppc.LongFloatHashMap;
import com.carrotsearch.hppc.cursors.LongFloatCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongFloatComparator;
import com.carrotsearch.hppc.procedures.LongFloatProcedure;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.procedures.FloatProcedure;
import com.carrotsearch.hppc.predicates.LongFloatPredicate;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.predicates.FloatPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongFloatHashMapBenchmark {

    private static final int ELEMENT_COUNT = 1024;

    private LongFloatHashMap delegate;
    private SortedIterationLongFloatHashMap sortedByKey;
    private SortedIterationLongFloatHashMap sortedByKeyValue;

    private long sampleKey;
    private int sampleIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(0x1234ABCDL);
        delegate = new LongFloatHashMap(ELEMENT_COUNT);
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            long key = rnd.nextLong();
            float value = rnd.nextFloat();
            delegate.put(key, value);
            if (i == 0) {
                sampleKey = key;
                sampleIndex = 0;
            }
        }
        LongComparator keyComparator = (a, b) -> Long.compare(a, b);
        LongFloatComparator keyValueComparator = (k1, v1, k2, v2) -> Long.compare(k1, k2);
        sortedByKey = new SortedIterationLongFloatHashMap(delegate, keyComparator);
        sortedByKeyValue = new SortedIterationLongFloatHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public int size() {
        return sortedByKey.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return sortedByKey.isEmpty();
    }

    @Benchmark
    public boolean containsKey() {
        return sortedByKey.containsKey(sampleKey);
    }

    @Benchmark
    public float get() {
        return sortedByKey.get(sampleKey);
    }

    @Benchmark
    public float getOrDefault() {
        return sortedByKey.getOrDefault(sampleKey, -1.0f);
    }

    @Benchmark
    public int indexOf() {
        return sortedByKey.indexOf(sampleKey);
    }

    @Benchmark
    public boolean indexExists() {
        return sortedByKey.indexExists(sampleIndex);
    }

    @Benchmark
    public float indexGet() {
        return sortedByKey.indexGet(sampleIndex);
    }

    @Benchmark
    public void iterate(Blackhole bh) {
        Iterator<LongFloatCursor> it = sortedByKey.iterator();
        while (it.hasNext()) {
            LongFloatCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        sortedByKey.forEach((LongFloatProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        sortedByKey.forEach((LongFloatPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void keysIteration(Blackhole bh) {
        Iterator<LongCursor> it = sortedByKey.keys().iterator();
        while (it.hasNext()) {
            LongCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void valuesIteration(Blackhole bh) {
        Iterator<FloatCursor> it = sortedByKey.values().iterator();
        while (it.hasNext()) {
            FloatCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void keysForEachProcedure(Blackhole bh) {
        sortedByKey.keys().forEach((LongProcedure) k -> bh.consume(k));
    }

    @Benchmark
    public void valuesForEachProcedure(Blackhole bh) {
        sortedByKey.values().forEach((FloatProcedure) v -> bh.consume(v));
    }

    @Benchmark
    public void keysForEachPredicate(Blackhole bh) {
        sortedByKey.keys().forEach((LongPredicate) k -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public void valuesForEachPredicate(Blackhole bh) {
        sortedByKey.values().forEach((FloatPredicate) v -> {
            bh.consume(v);
            return true;
        });
    }
}

package bench.generated.c104;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationLongObjectHashMap;
import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.cursors.LongObjectCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongObjectComparator;
import com.carrotsearch.hppc.procedures.LongObjectProcedure;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.predicates.LongObjectPredicate;
import com.carrotsearch.hppc.predicates.LongPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongObjectHashMapBenchmark {

    private int elementCount;
    private LongObjectHashMap<String> delegate;
    private SortedIterationLongObjectHashMap<String> viewByKey;
    private SortedIterationLongObjectHashMap<String> viewByKeyValue;
    private long[] keys;
    private int sampleIndex;
    private long sampleKey;
    private int sampleMapIndex;

    @Setup(Level.Trial)
    public void setUp() {
        elementCount = 1024;
        delegate = new LongObjectHashMap<>(elementCount);
        keys = new long[elementCount];
        Random rnd = new Random(0x1234ABCDL);
        for (int i = 0; i < elementCount; i++) {
            long k = rnd.nextLong();
            String v = "val" + i;
            delegate.put(k, v);
            keys[i] = k;
        }

        LongComparator keyComparator = (a, b) -> Long.compare(a, b);
        viewByKey = new SortedIterationLongObjectHashMap<>(delegate, keyComparator);

        LongObjectComparator<String> keyValueComparator = (k1, v1, k2, v2) -> Long.compare(k1, k2);
        viewByKeyValue = new SortedIterationLongObjectHashMap<>(delegate, keyValueComparator);

        sampleIndex = elementCount / 2;
        sampleKey = keys[sampleIndex];
        sampleMapIndex = viewByKey.indexOf(sampleKey);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return viewByKey.containsKey(sampleKey);
    }

    @Benchmark
    public int benchmarkSize() {
        return viewByKey.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return viewByKey.isEmpty();
    }

    @Benchmark
    public String benchmarkGet() {
        return viewByKey.get(sampleKey);
    }

    @Benchmark
    public String benchmarkGetOrDefault() {
        return viewByKey.getOrDefault(sampleKey, "default");
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        for (LongObjectCursor<String> c : viewByKey) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        viewByKey.forEach((LongObjectProcedure<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        viewByKey.forEach((LongObjectPredicate<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkKeysIteration(Blackhole bh) {
        for (LongCursor c : viewByKey.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(Blackhole bh) {
        for (ObjectCursor<String> c : viewByKey.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return viewByKey.indexOf(sampleKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        return viewByKey.indexExists(sampleMapIndex);
    }

    @Benchmark
    public String benchmarkIndexGet() {
        return viewByKey.indexGet(sampleMapIndex);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return viewByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void benchmarkIteratorByKeyValue(Blackhole bh) {
        for (LongObjectCursor<String> c : viewByKeyValue) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }
}

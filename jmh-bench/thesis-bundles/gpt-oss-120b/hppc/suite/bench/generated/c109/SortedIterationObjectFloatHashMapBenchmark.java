package bench.generated.c109;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationObjectFloatHashMap;
import com.carrotsearch.hppc.ObjectFloatHashMap;
import com.carrotsearch.hppc.cursors.ObjectFloatCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.procedures.ObjectFloatProcedure;
import com.carrotsearch.hppc.predicates.ObjectFloatPredicate;
import com.carrotsearch.hppc.predicates.ObjectPredicate;
import com.carrotsearch.hppc.procedures.FloatProcedure;
import com.carrotsearch.hppc.predicates.FloatPredicate;
import com.carrotsearch.hppc.comparators.ObjectFloatComparator;
import java.util.Comparator;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectFloatHashMapBenchmark {
    private static final int SIZE = 1024;

    private ObjectFloatHashMap<String> delegate;
    private SortedIterationObjectFloatHashMap<String> viewByKey;
    private SortedIterationObjectFloatHashMap<String> viewByKeyValue;
    private String[] keys;

    @Setup(Level.Trial)
    public void setup() {
        delegate = new ObjectFloatHashMap<>(SIZE);
        keys = new String[SIZE];
        Random rnd = new Random(12345L);
        for (int i = 0; i < SIZE; i++) {
            String k = "key" + i;
            float v = i * 1.0f + rnd.nextFloat();
            delegate.put(k, v);
            keys[i] = k;
        }
        viewByKey = new SortedIterationObjectFloatHashMap<>(delegate, Comparator.naturalOrder());
        viewByKeyValue = new SortedIterationObjectFloatHashMap<>(delegate, new ObjectFloatComparator<String>() {
            @Override
            public int compare(String k1, float v1, String k2, float v2) {
                int cmp = k1.compareTo(k2);
                if (cmp != 0) return cmp;
                return Float.compare(v1, v2);
            }
        });
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
    public boolean benchmarkContainsKey() {
        return viewByKey.containsKey(keys[0]);
    }

    @Benchmark
    public float benchmarkGet() {
        return viewByKey.get(keys[0]);
    }

    @Benchmark
    public float benchmarkGetOrDefault() {
        return viewByKey.getOrDefault(keys[0], -1f);
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        Iterator<ObjectFloatCursor<String>> it = viewByKey.iterator();
        if (it.hasNext()) {
            ObjectFloatCursor<String> c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        viewByKey.forEach(new ObjectFloatProcedure<String>() {
            @Override
            public void apply(String key, float value) {
                bh.consume(key);
                bh.consume(value);
            }
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        viewByKey.forEach(new ObjectFloatPredicate<String>() {
            @Override
            public boolean apply(String key, float value) {
                bh.consume(key);
                bh.consume(value);
                return true;
            }
        });
    }

    @Benchmark
    public void benchmarkForEachProcedureKeyValue(Blackhole bh) {
        viewByKeyValue.forEach(new ObjectFloatProcedure<String>() {
            @Override
            public void apply(String key, float value) {
                bh.consume(key);
                bh.consume(value);
            }
        });
    }

    @Benchmark
    public void benchmarkKeysIteration(Blackhole bh) {
        Iterator<ObjectCursor<String>> it = viewByKey.keys().iterator();
        while (it.hasNext()) {
            ObjectCursor<String> c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(Blackhole bh) {
        Iterator<FloatCursor> it = viewByKey.values().iterator();
        while (it.hasNext()) {
            FloatCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return viewByKey.indexOf(keys[0]);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        int idx = viewByKey.indexOf(keys[0]);
        return viewByKey.indexExists(idx);
    }

    @Benchmark
    public float benchmarkIndexGet() {
        int idx = viewByKey.indexOf(keys[0]);
        return viewByKey.indexGet(idx);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return viewByKey.visualizeKeyDistribution(10);
    }
}

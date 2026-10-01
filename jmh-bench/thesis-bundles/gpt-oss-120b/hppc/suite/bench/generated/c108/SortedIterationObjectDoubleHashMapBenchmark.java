package bench.generated.c108;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Comparator;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationObjectDoubleHashMap;
import com.carrotsearch.hppc.ObjectDoubleHashMap;
import com.carrotsearch.hppc.cursors.ObjectDoubleCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.ObjectDoubleProcedure;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.procedures.DoubleProcedure;
import com.carrotsearch.hppc.predicates.ObjectDoublePredicate;
import com.carrotsearch.hppc.predicates.ObjectPredicate;
import com.carrotsearch.hppc.predicates.DoublePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectDoubleHashMapBenchmark {
    private ObjectDoubleHashMap<String> delegate;
    private SortedIterationObjectDoubleHashMap<String> view;
    private String[] keys;
    private double[] values;
    private int size;
    private int lookupIndex;

    @Setup(Level.Trial)
    public void setup() {
        size = 1024;
        delegate = new ObjectDoubleHashMap<>(size);
        keys = new String[size];
        values = new double[size];
        Random rnd = new Random(12345L);
        for (int i = 0; i < size; i++) {
            String k = "key" + i;
            double v = rnd.nextDouble();
            delegate.put(k, v);
            keys[i] = k;
            values[i] = v;
        }
        view = new SortedIterationObjectDoubleHashMap<>(delegate, Comparator.naturalOrder());
        lookupIndex = 0;
    }

    @Benchmark
    public int benchmarkSize() {
        return view.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return view.isEmpty();
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        String key = keys[lookupIndex];
        lookupIndex = (lookupIndex + 1) % size;
        return view.containsKey(key);
    }

    @Benchmark
    public double benchmarkGet() {
        String key = keys[lookupIndex];
        lookupIndex = (lookupIndex + 1) % size;
        return view.get(key);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        String key = keys[lookupIndex];
        lookupIndex = (lookupIndex + 1) % size;
        return view.indexOf(key);
    }

    @Benchmark
    public double benchmarkIndexGet(Blackhole bh) {
        String key = keys[lookupIndex];
        lookupIndex = (lookupIndex + 1) % size;
        int idx = view.indexOf(key);
        double val = view.indexGet(idx);
        bh.consume(val);
        return val;
    }

    @Benchmark
    public void benchmarkIterateEntries(Blackhole bh) {
        for (ObjectDoubleCursor<String> c : view) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        view.forEach((ObjectDoubleProcedure<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        view.forEach((ObjectDoublePredicate<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkIterateKeys(Blackhole bh) {
        for (ObjectCursor<String> c : view.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkKeysForEachProcedure(Blackhole bh) {
        view.keys().forEach((ObjectProcedure<String>) k -> bh.consume(k));
    }

    @Benchmark
    public void benchmarkKeysForEachPredicate(Blackhole bh) {
        view.keys().forEach((ObjectPredicate<String>) k -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public boolean benchmarkKeysContains() {
        String key = keys[lookupIndex];
        lookupIndex = (lookupIndex + 1) % size;
        return view.keys().contains(key);
    }

    @Benchmark
    public void benchmarkIterateValues(Blackhole bh) {
        for (DoubleCursor c : view.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesForEachProcedure(Blackhole bh) {
        view.values().forEach((DoubleProcedure) v -> bh.consume(v));
    }

    @Benchmark
    public void benchmarkValuesForEachPredicate(Blackhole bh) {
        view.values().forEach((DoublePredicate) v -> {
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public boolean benchmarkValuesContains() {
        double v = values[lookupIndex];
        lookupIndex = (lookupIndex + 1) % size;
        return view.values().contains(v);
    }
}

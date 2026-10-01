package bench.generated.c116;

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
import java.util.Iterator;
import com.carrotsearch.hppc.SortedIterationShortDoubleHashMap;
import com.carrotsearch.hppc.ShortDoubleHashMap;
import com.carrotsearch.hppc.comparators.ShortComparator;
import com.carrotsearch.hppc.comparators.ShortDoubleComparator;
import com.carrotsearch.hppc.cursors.ShortDoubleCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.ShortDoubleProcedure;
import com.carrotsearch.hppc.predicates.ShortDoublePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationShortDoubleHashMapBenchmark {

    private ShortDoubleHashMap delegate;
    private SortedIterationShortDoubleHashMap viewByKey;
    private SortedIterationShortDoubleHashMap viewByKeyValue;
    private short randomKey;
    private int randomIndex;

    @Setup(Level.Trial)
    public void setup() {
        int size = 1024;
        delegate = new ShortDoubleHashMap(size);
        Random rnd = new Random(12345L);
        short firstKey = 0;
        for (int i = 0; i < size; i++) {
            short key = (short) rnd.nextInt(Short.MAX_VALUE + 1);
            double value = rnd.nextDouble();
            delegate.put(key, value);
            if (i == 0) {
                firstKey = key;
            }
        }
        randomKey = firstKey;

        ShortComparator keyComparator = new ShortComparator() {
            @Override
            public int compare(short a, short b) {
                return Short.compare(a, b);
            }
        };

        ShortDoubleComparator keyValueComparator = new ShortDoubleComparator() {
            @Override
            public int compare(short key1, double value1, short key2, double value2) {
                int cmp = Short.compare(key1, key2);
                if (cmp != 0) {
                    return cmp;
                }
                return Double.compare(value1, value2);
            }
        };

        viewByKey = new SortedIterationShortDoubleHashMap(delegate, keyComparator);
        viewByKeyValue = new SortedIterationShortDoubleHashMap(delegate, keyValueComparator);
        randomIndex = viewByKey.size() / 2;
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
        return viewByKey.containsKey(randomKey);
    }

    @Benchmark
    public double benchmarkGet() {
        return viewByKey.get(randomKey);
    }

    @Benchmark
    public double benchmarkGetOrDefault() {
        return viewByKey.getOrDefault(randomKey, -1.0);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return viewByKey.indexOf(randomKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        return viewByKey.indexExists(randomIndex);
    }

    @Benchmark
    public double benchmarkIndexGet() {
        return viewByKey.indexGet(randomIndex);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return viewByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void benchmarkIterateEntries(Blackhole bh) {
        for (ShortDoubleCursor c : viewByKey) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkIteratorNext(Blackhole bh) {
        Iterator<ShortDoubleCursor> it = viewByKey.iterator();
        while (it.hasNext()) {
            ShortDoubleCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        viewByKey.forEach((ShortDoubleProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        viewByKey.forEach((ShortDoublePredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkKeysIteration(Blackhole bh) {
        Iterator<ShortCursor> it = viewByKey.keys().iterator();
        while (it.hasNext()) {
            ShortCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(Blackhole bh) {
        Iterator<DoubleCursor> it = viewByKey.values().iterator();
        while (it.hasNext()) {
            DoubleCursor c = it.next();
            bh.consume(c.value);
        }
    }
}

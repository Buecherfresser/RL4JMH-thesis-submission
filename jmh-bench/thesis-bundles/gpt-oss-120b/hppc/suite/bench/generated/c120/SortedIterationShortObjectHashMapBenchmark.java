package bench.generated.c120;

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
import com.carrotsearch.hppc.SortedIterationShortObjectHashMap;
import com.carrotsearch.hppc.ShortObjectHashMap;
import com.carrotsearch.hppc.comparators.ShortComparator;
import com.carrotsearch.hppc.comparators.ShortObjectComparator;
import com.carrotsearch.hppc.procedures.ShortObjectProcedure;
import com.carrotsearch.hppc.predicates.ShortObjectPredicate;
import com.carrotsearch.hppc.cursors.ShortObjectCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.ShortCollection;
import com.carrotsearch.hppc.ObjectContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationShortObjectHashMapBenchmark {
    private ShortObjectHashMap<String> delegate;
    private SortedIterationShortObjectHashMap<String> sortedByKey;
    private SortedIterationShortObjectHashMap<String> sortedByKeyValue;
    private short knownKey;
    private ShortComparator keyComparator;
    private ShortObjectComparator<String> keyValueComparator;

    @Setup(Level.Trial)
    public void setup() {
        int size = 10_000;
        Random rnd = new Random(12345);
        delegate = new ShortObjectHashMap<>(size);
        short[] insertedKeys = new short[size];
        for (int i = 0; i < size; i++) {
            short key = (short) rnd.nextInt(Short.MAX_VALUE + 1);
            delegate.put(key, "val" + i);
            insertedKeys[i] = key;
        }
        knownKey = insertedKeys[0];

        keyComparator = (a, b) -> Short.compare(a, b);
        keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Short.compare(k1, k2);
            if (cmp != 0) return cmp;
            return v1.compareTo(v2);
        };

        sortedByKey = new SortedIterationShortObjectHashMap<>(delegate, keyComparator);
        sortedByKeyValue = new SortedIterationShortObjectHashMap<>(delegate, keyValueComparator);
    }

    @Benchmark
    public int benchmarkSize() {
        return sortedByKey.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return sortedByKey.isEmpty();
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return sortedByKey.containsKey(knownKey);
    }

    @Benchmark
    public String benchmarkGet() {
        return sortedByKey.get(knownKey);
    }

    @Benchmark
    public String benchmarkGetOrDefault() {
        return sortedByKey.getOrDefault(knownKey, "default");
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        for (ShortObjectCursor<String> c : sortedByKey) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        sortedByKey.forEach((ShortObjectProcedure<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        sortedByKey.forEach((ShortObjectPredicate<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkKeysIteration(Blackhole bh) {
        for (ShortCursor c : sortedByKey.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(Blackhole bh) {
        for (ObjectCursor<String> c : sortedByKey.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return sortedByKey.indexOf(knownKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        int idx = sortedByKey.indexOf(knownKey);
        return sortedByKey.indexExists(idx);
    }

    @Benchmark
    public String benchmarkIndexGet() {
        int idx = sortedByKey.indexOf(knownKey);
        return sortedByKey.indexGet(idx);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return sortedByKey.visualizeKeyDistribution(10);
    }
}

package bench.generated.c121;

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
import com.carrotsearch.hppc.SortedIterationShortShortHashMap;
import com.carrotsearch.hppc.ShortShortHashMap;
import com.carrotsearch.hppc.comparators.ShortComparator;
import com.carrotsearch.hppc.comparators.ShortShortComparator;
import com.carrotsearch.hppc.cursors.ShortShortCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.procedures.ShortShortProcedure;
import com.carrotsearch.hppc.predicates.ShortShortPredicate;
import com.carrotsearch.hppc.ShortCollection;
import com.carrotsearch.hppc.ShortContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationShortShortHashMapBenchmark {
    private ShortShortHashMap delegate;
    private SortedIterationShortShortHashMap sortedByKey;
    private SortedIterationShortShortHashMap sortedByKeyValue;
    private short sampleKey;
    private ShortComparator keyComparator;
    private ShortShortComparator keyValueComparator;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(12345L);
        delegate = new ShortShortHashMap(2048, 0.5f);
        short lastKey = 0;
        for (int i = 0; i < 1000; i++) {
            short k = (short) rnd.nextInt(Short.MAX_VALUE + 1);
            short v = (short) rnd.nextInt(Short.MAX_VALUE + 1);
            delegate.put(k, v);
            lastKey = k;
        }
        sampleKey = lastKey;

        keyComparator = (a, b) -> Short.compare(a, b);
        keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Short.compare(k1, k2);
            if (cmp != 0) return cmp;
            return Short.compare(v1, v2);
        };

        sortedByKey = new SortedIterationShortShortHashMap(delegate, keyComparator);
        sortedByKeyValue = new SortedIterationShortShortHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public int size() {
        return sortedByKey.size();
    }

    @Benchmark
    public boolean containsKey() {
        return sortedByKey.containsKey(sampleKey);
    }

    @Benchmark
    public short get() {
        return sortedByKey.get(sampleKey);
    }

    @Benchmark
    public short getOrDefault() {
        return sortedByKey.getOrDefault(sampleKey, (short) -1);
    }

    @Benchmark
    public void iterateEntries(Blackhole bh) {
        for (ShortShortCursor c : sortedByKey) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        sortedByKey.forEach((ShortShortProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        sortedByKey.forEach((ShortShortPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void iterateKeys(Blackhole bh) {
        for (ShortCursor c : sortedByKey.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void iterateValues(Blackhole bh) {
        for (ShortCursor c : sortedByKey.values()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int indexOf() {
        return sortedByKey.indexOf(sampleKey);
    }

    @Benchmark
    public boolean indexExists() {
        int idx = sortedByKey.indexOf(sampleKey);
        return sortedByKey.indexExists(idx);
    }

    @Benchmark
    public short indexGet() {
        int idx = sortedByKey.indexOf(sampleKey);
        return sortedByKey.indexGet(idx);
    }

    @Benchmark
    public String visualize() {
        return sortedByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void forEachProcedureKeyValue(Blackhole bh) {
        sortedByKeyValue.forEach((ShortShortProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }
}

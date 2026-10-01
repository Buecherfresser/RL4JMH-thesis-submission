package bench.generated.c083;

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
import com.carrotsearch.hppc.SortedIterationCharCharHashMap;
import com.carrotsearch.hppc.CharCharHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharCharComparator;
import com.carrotsearch.hppc.cursors.CharCharCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.predicates.CharCharPredicate;
import com.carrotsearch.hppc.procedures.CharCharProcedure;
import com.carrotsearch.hppc.CharCollection;
import com.carrotsearch.hppc.CharContainer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharCharHashMapBenchmark {
    private CharCharHashMap delegate;
    private SortedIterationCharCharHashMap viewByKey;
    private SortedIterationCharCharHashMap viewByKeyValue;
    private char existingKey;
    private char nonExistingKey;
    private int existingIndex;

    @Setup(Level.Trial)
    public void setup() {
        final int size = 1024;
        delegate = new CharCharHashMap(size);
        for (int i = 0; i < size; i++) {
            char key = (char) i;
            char value = (char) (i * 2);
            delegate.put(key, value);
        }

        CharComparator keyComparator = (a, b) -> Character.compare(a, b);
        CharCharComparator keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Character.compare(k1, k2);
            if (cmp != 0) {
                return cmp;
            }
            return Character.compare(v1, v2);
        };

        viewByKey = new SortedIterationCharCharHashMap(delegate, keyComparator);
        viewByKeyValue = new SortedIterationCharCharHashMap(delegate, keyValueComparator);

        existingKey = (char) 0;
        nonExistingKey = (char) (size + 1);
        existingIndex = viewByKey.indexOf(existingKey);
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
        return viewByKey.containsKey(existingKey);
    }

    @Benchmark
    public char benchmarkGet() {
        return viewByKey.get(existingKey);
    }

    @Benchmark
    public char benchmarkGetOrDefault() {
        return viewByKey.getOrDefault(nonExistingKey, (char) -1);
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        for (CharCharCursor c : viewByKey) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        viewByKey.forEach((CharCharProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        viewByKey.forEach((CharCharPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkKeysIteration(Blackhole bh) {
        CharCollection keys = viewByKey.keys();
        for (CharCursor c : keys) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(Blackhole bh) {
        CharContainer values = viewByKey.values();
        for (CharCursor c : values) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return viewByKey.indexOf(existingKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        return viewByKey.indexExists(existingIndex);
    }

    @Benchmark
    public char benchmarkIndexGet() {
        return viewByKey.indexGet(existingIndex);
    }
}

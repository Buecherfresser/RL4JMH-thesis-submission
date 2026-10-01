package bench.generated.c113;

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
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Comparator;
import java.util.Iterator;

import com.carrotsearch.hppc.SortedIterationObjectShortHashMap;
import com.carrotsearch.hppc.ObjectShortHashMap;
import com.carrotsearch.hppc.comparators.ObjectShortComparator;
import com.carrotsearch.hppc.procedures.ObjectShortProcedure;
import com.carrotsearch.hppc.predicates.ObjectShortPredicate;
import com.carrotsearch.hppc.cursors.ObjectShortCursor;
import com.carrotsearch.hppc.ObjectCollection;
import com.carrotsearch.hppc.predicates.ObjectPredicate;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.ShortContainer;
import com.carrotsearch.hppc.cursors.ShortCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectShortHashMapBenchmark {

    private int size;
    private ObjectShortHashMap<String> delegate;
    private SortedIterationObjectShortHashMap<String> sortedByKey;
    private SortedIterationObjectShortHashMap<String> sortedByKeyValue;
    private String[] keys;
    private short[] values;
    private Random random;

    @Setup
    public void setup() {
        size = 1024;
        random = new Random(0x1234ABCD);
        delegate = new ObjectShortHashMap<>(size);
        keys = new String[size];
        values = new short[size];
        for (int i = 0; i < size; i++) {
            String key = "key" + i;
            short value = (short) random.nextInt(Short.MAX_VALUE + 1);
            delegate.put(key, value);
            keys[i] = key;
            values[i] = value;
        }

        Comparator<String> keyComparator = Comparator.naturalOrder();
        sortedByKey = new SortedIterationObjectShortHashMap<>(delegate, keyComparator);

        ObjectShortComparator<String> keyValueComparator = new ObjectShortComparator<String>() {
            @Override
            public int compare(String k1, short v1, String k2, short v2) {
                int cmp = k1.compareTo(k2);
                if (cmp != 0) return cmp;
                return Short.compare(v1, v2);
            }
        };
        sortedByKeyValue = new SortedIterationObjectShortHashMap<>(delegate, keyValueComparator);
    }

    @Benchmark
    public short benchmarkGet() {
        return sortedByKey.get(keys[0]);
    }

    @Benchmark
    public short benchmarkGetOrDefault() {
        return sortedByKey.getOrDefault("nonexistent", (short) -1);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return sortedByKey.containsKey(keys[size / 2]);
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
    public void benchmarkForEachProcedure(Blackhole bh) {
        sortedByKey.forEach(new ObjectShortProcedure<String>() {
            @Override
            public void apply(String key, short value) {
                bh.consume(key);
                bh.consume(value);
            }
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        sortedByKey.forEach(new ObjectShortPredicate<String>() {
            @Override
            public boolean apply(String key, short value) {
                bh.consume(key);
                bh.consume(value);
                return true;
            }
        });
    }

    @Benchmark
    public void benchmarkIterate(Blackhole bh) {
        Iterator<ObjectShortCursor<String>> it = sortedByKey.iterator();
        while (it.hasNext()) {
            ObjectShortCursor<String> c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkKeysContainerIterate(Blackhole bh) {
        ObjectCollection<String> keysView = sortedByKey.keys();
        Iterator<ObjectCursor<String>> it = keysView.iterator();
        while (it.hasNext()) {
            ObjectCursor<String> c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesContainerIterate(Blackhole bh) {
        ShortContainer valuesView = sortedByKey.values();
        Iterator<ShortCursor> it = valuesView.iterator();
        while (it.hasNext()) {
            ShortCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public short benchmarkIndexGet() {
        return sortedByKey.indexGet(0);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        return sortedByKey.indexExists(0);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return sortedByKey.indexOf(keys[size - 1]);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return sortedByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void benchmarkForEachProcedureKeyValueComparator(Blackhole bh) {
        sortedByKeyValue.forEach(new ObjectShortProcedure<String>() {
            @Override
            public void apply(String key, short value) {
                bh.consume(key);
                bh.consume(value);
            }
        });
    }

    @Benchmark
    public void benchmarkIterateKeyValueComparator(Blackhole bh) {
        Iterator<ObjectShortCursor<String>> it = sortedByKeyValue.iterator();
        while (it.hasNext()) {
            ObjectShortCursor<String> c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }
}

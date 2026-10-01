package bench.generated.c087;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationCharLongHashMap;
import com.carrotsearch.hppc.CharLongHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharLongComparator;
import com.carrotsearch.hppc.cursors.CharLongCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.procedures.CharLongProcedure;
import com.carrotsearch.hppc.predicates.CharLongPredicate;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharLongHashMapBenchmark {

    private final int ELEMENT_COUNT = 1024;

    private CharLongHashMap delegate;
    private SortedIterationCharLongHashMap viewByKey;
    private SortedIterationCharLongHashMap viewByKeyValue;
    private char[] keys;

    private final CharComparator keyComparator = (a, b) -> Character.compare(a, b);

    private final CharLongComparator keyValueComparator = (k1, v1, k2, v2) -> {
        int cmp = Character.compare(k1, k2);
        if (cmp != 0) {
            return cmp;
        }
        return Long.compare(v1, v2);
    };

    @Setup(Level.Trial)
    public void setup() {
        delegate = new CharLongHashMap(ELEMENT_COUNT);
        keys = new char[ELEMENT_COUNT];
        Random rnd = new Random(12345L);
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            char key = (char) rnd.nextInt(Character.MAX_VALUE + 1);
            long value = rnd.nextLong();
            delegate.put(key, value);
            keys[i] = key;
        }
        viewByKey = new SortedIterationCharLongHashMap(delegate, keyComparator);
        viewByKeyValue = new SortedIterationCharLongHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public void benchmarkForEach(Blackhole bh) {
        viewByKey.forEach((CharLongProcedure) (k, v) -> bh.consume(k ^ v));
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        viewByKey.forEach((CharLongPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        Iterator<CharLongCursor> it = viewByKey.iterator();
        while (it.hasNext()) {
            CharLongCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkKeysIterator(Blackhole bh) {
        Iterator<CharCursor> it = viewByKey.keys().iterator();
        while (it.hasNext()) {
            CharCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIterator(Blackhole bh) {
        Iterator<LongCursor> it = viewByKey.values().iterator();
        while (it.hasNext()) {
            LongCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public long benchmarkGet() {
        char key = keys[0];
        return viewByKey.get(key);
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        char key = keys[0];
        return viewByKey.containsKey(key);
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
    public int benchmarkIndexOf() {
        char key = keys[0];
        return viewByKey.indexOf(key);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        char key = keys[0];
        int idx = viewByKey.indexOf(key);
        return viewByKey.indexExists(idx);
    }

    @Benchmark
    public long benchmarkIndexGet() {
        char key = keys[0];
        int idx = viewByKey.indexOf(key);
        return viewByKey.indexGet(idx);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return viewByKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public void benchmarkForEachKeyValueComparator(Blackhole bh) {
        viewByKeyValue.forEach((CharLongProcedure) (k, v) -> bh.consume(k ^ v));
    }
}

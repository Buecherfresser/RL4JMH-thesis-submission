package bench.generated.c099;

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
import java.util.Iterator;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationLongCharHashMap;
import com.carrotsearch.hppc.LongCharHashMap;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongCharComparator;
import com.carrotsearch.hppc.procedures.LongCharProcedure;
import com.carrotsearch.hppc.predicates.LongCharPredicate;
import com.carrotsearch.hppc.cursors.LongCharCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.CharCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongCharHashMapBenchmark {
    private LongCharHashMap delegate;
    private SortedIterationLongCharHashMap viewByKey;
    private SortedIterationLongCharHashMap viewByKeyValue;
    private long existingKey;
    private char existingValue;

    @Setup
    public void setup() {
        Random rnd = new Random(12345L);
        int size = 1024;
        delegate = new LongCharHashMap(size);
        for (int i = 0; i < size; i++) {
            long key = rnd.nextLong();
            char value = (char) rnd.nextInt(Character.MAX_VALUE + 1);
            delegate.put(key, value);
        }

        LongComparator keyComp = Long::compare;
        viewByKey = new SortedIterationLongCharHashMap(delegate, keyComp);

        LongCharComparator keyValComp = (k1, v1, k2, v2) -> {
            int cmp = Long.compare(k1, k2);
            if (cmp != 0) return cmp;
            return Character.compare(v1, v2);
        };
        viewByKeyValue = new SortedIterationLongCharHashMap(delegate, keyValComp);

        Iterator<LongCursor> keyIt = viewByKey.keys().iterator();
        if (keyIt.hasNext()) {
            existingKey = keyIt.next().value;
        }
        Iterator<CharCursor> valIt = viewByKey.values().iterator();
        if (valIt.hasNext()) {
            existingValue = valIt.next().value;
        }
    }

    @Benchmark
    public int sizeByKey() {
        return viewByKey.size();
    }

    @Benchmark
    public int sizeByKeyValue() {
        return viewByKeyValue.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return viewByKey.isEmpty();
    }

    @Benchmark
    public boolean containsKeyExisting(Blackhole bh) {
        boolean result = viewByKey.containsKey(existingKey);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public char getExisting(Blackhole bh) {
        char v = viewByKey.get(existingKey);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public char getOrDefaultMissing(Blackhole bh) {
        char v = viewByKey.getOrDefault(Long.MAX_VALUE, (char) 0);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        viewByKey.forEach((LongCharProcedure) (k, v) -> bh.consume(k ^ v));
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        viewByKey.forEach((LongCharPredicate) (k, v) -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public Iterator<LongCharCursor> iterator(Blackhole bh) {
        Iterator<LongCharCursor> it = viewByKey.iterator();
        if (it.hasNext()) {
            LongCharCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
        return it;
    }

    @Benchmark
    public Iterator<LongCursor> keysIterator(Blackhole bh) {
        Iterator<LongCursor> it = viewByKey.keys().iterator();
        if (it.hasNext()) {
            LongCursor c = it.next();
            bh.consume(c.value);
        }
        return it;
    }

    @Benchmark
    public Iterator<CharCursor> valuesIterator(Blackhole bh) {
        Iterator<CharCursor> it = viewByKey.values().iterator();
        if (it.hasNext()) {
            CharCursor c = it.next();
            bh.consume(c.value);
        }
        return it;
    }

    @Benchmark
    public boolean keysContains(Blackhole bh) {
        boolean contains = viewByKey.keys().contains(existingKey);
        bh.consume(contains);
        return contains;
    }

    @Benchmark
    public boolean valuesContains(Blackhole bh) {
        boolean contains = viewByKey.values().contains(existingValue);
        bh.consume(contains);
        return contains;
    }

    @Benchmark
    public int indexOfExisting() {
        return viewByKey.indexOf(existingKey);
    }

    @Benchmark
    public char indexGet(Blackhole bh) {
        int idx = viewByKey.indexOf(existingKey);
        char v = viewByKey.indexGet(idx);
        bh.consume(v);
        return v;
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return viewByKey.visualizeKeyDistribution(10);
    }
}

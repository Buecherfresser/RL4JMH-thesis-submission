package bench.generated.c099;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.LongCharHashMap;
import com.carrotsearch.hppc.SortedIterationLongCharHashMap;
import com.carrotsearch.hppc.cursors.LongCharCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongCharComparator;
import com.carrotsearch.hppc.procedures.LongCharProcedure;
import com.carrotsearch.hppc.predicates.LongCharPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongCharHashMapBenchmark {

    private LongCharHashMap delegate;
    private SortedIterationLongCharHashMap view;
    private long existingKey;
    private long missingKey;
    private int existingIndex;
    private LongComparator keyComparator;
    private LongCharComparator keyValueComparator;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(0x12345678L);
        delegate = new LongCharHashMap();
        int size = 1000;
        for (int i = 0; i < size; i++) {
            long key = random.nextLong();
            while (delegate.containsKey(key)) {
                key = random.nextLong();
            }
            char value = (char) random.nextInt(Character.MAX_VALUE + 1);
            delegate.put(key, value);
        }
        existingKey = delegate.keys().iterator().next().value;
        missingKey = existingKey ^ 0xDEADBEEFL;
        keyComparator = (a, b) -> Long.compare(a, b);
        keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Long.compare(k1, k2);
            if (cmp == 0) cmp = Character.compare(v1, v2);
            return cmp;
        };
        view = new SortedIterationLongCharHashMap(delegate, keyComparator);
        existingIndex = view.indexOf(existingKey);
    }

    @Benchmark
    public SortedIterationLongCharHashMap constructorKeyComparator() {
        return new SortedIterationLongCharHashMap(delegate, keyComparator);
    }

    @Benchmark
    public SortedIterationLongCharHashMap constructorKeyValueComparator() {
        return new SortedIterationLongCharHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public boolean containsKey() {
        return view.containsKey(existingKey);
    }

    @Benchmark
    public char get() {
        return view.get(existingKey);
    }

    @Benchmark
    public char getOrDefault() {
        return view.getOrDefault(existingKey, (char) 0);
    }

    @Benchmark
    public int indexOf() {
        return view.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExists() {
        return view.indexExists(existingIndex);
    }

    @Benchmark
    public char indexGet() {
        return view.indexGet(existingIndex);
    }

    @Benchmark
    public int size() {
        return view.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return view.isEmpty();
    }

    @Benchmark
    public void iterateWithIterator(Blackhole bh) {
        for (LongCharCursor cursor : view) {
            bh.consume(cursor.key);
            bh.consume(cursor.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        view.forEach((LongCharProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        view.forEach((LongCharPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void keysIteration(Blackhole bh) {
        for (LongCursor cursor : view.keys()) {
            bh.consume(cursor.value);
        }
    }

    @Benchmark
    public void valuesIteration(Blackhole bh) {
        for (CharCursor cursor : view.values()) {
            bh.consume(cursor.value);
        }
    }
}

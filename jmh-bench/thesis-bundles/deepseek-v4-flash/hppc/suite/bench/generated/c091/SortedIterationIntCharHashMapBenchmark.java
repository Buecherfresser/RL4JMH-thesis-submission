package bench.generated.c091;

import com.carrotsearch.hppc.IntCharHashMap;
import com.carrotsearch.hppc.SortedIterationIntCharHashMap;
import com.carrotsearch.hppc.cursors.IntCharCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.predicates.IntCharPredicate;
import com.carrotsearch.hppc.predicates.IntPredicate;
import com.carrotsearch.hppc.procedures.IntCharProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.procedures.CharProcedure;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntCharComparator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntCharHashMapBenchmark {

    private IntCharHashMap delegate;
    private SortedIterationIntCharHashMap sortedView;
    private int keyPresent;
    private int keyAbsent;
    private int index;
    private IntComparator keyComparator;
    private IntCharComparator keyValueComparator;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(0x12345678L);
        delegate = new IntCharHashMap();
        int size = 1000;
        // Generate unique keys
        java.util.HashSet<Integer> used = new java.util.HashSet<>();
        while (used.size() < size) {
            int k = rnd.nextInt(100000);
            if (used.add(k)) {
                delegate.put(k, (char) rnd.nextInt(65536));
            }
        }
        // Pick a present key
        keyPresent = delegate.keys().toArray()[0];
        // Pick an absent key (not in the map)
        do {
            keyAbsent = rnd.nextInt(100000);
        } while (delegate.containsKey(keyAbsent));

        keyComparator = (a, b) -> Integer.compare(a, b);
        keyValueComparator = (a, va, b, vb) -> {
            int cmp = Integer.compare(a, b);
            if (cmp != 0) return cmp;
            return Character.compare(va, vb);
        };

        sortedView = new SortedIterationIntCharHashMap(delegate, keyComparator);
        index = sortedView.indexOf(keyPresent);
    }

    // ----- Read-only operations -----

    @Benchmark
    public boolean containsKeyPresent() {
        return sortedView.containsKey(keyPresent);
    }

    @Benchmark
    public boolean containsKeyAbsent() {
        return sortedView.containsKey(keyAbsent);
    }

    @Benchmark
    public char getPresent() {
        return sortedView.get(keyPresent);
    }

    @Benchmark
    public char getAbsent() {
        return sortedView.get(keyAbsent);
    }

    @Benchmark
    public char getOrDefaultPresent() {
        return sortedView.getOrDefault(keyPresent, (char) 0);
    }

    @Benchmark
    public char getOrDefaultAbsent() {
        return sortedView.getOrDefault(keyAbsent, (char) 0);
    }

    @Benchmark
    public int size() {
        return sortedView.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return sortedView.isEmpty();
    }

    @Benchmark
    public int indexOfPresent() {
        return sortedView.indexOf(keyPresent);
    }

    @Benchmark
    public boolean indexExists() {
        return sortedView.indexExists(index);
    }

    @Benchmark
    public char indexGet() {
        return sortedView.indexGet(index);
    }

    // ----- Iteration and forEach -----

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        sortedView.forEach((IntCharProcedure) (k, v) -> bh.consume(v));
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        sortedView.forEach((IntCharPredicate) (k, v) -> {
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void iterator(Blackhole bh) {
        for (IntCharCursor c : sortedView) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void keysForEach(Blackhole bh) {
        sortedView.keys().forEach((IntProcedure) k -> bh.consume(k));
    }

    @Benchmark
    public void valuesForEach(Blackhole bh) {
        sortedView.values().forEach((CharProcedure) v -> bh.consume(v));
    }

    @Benchmark
    public void keysIterator(Blackhole bh) {
        for (IntCursor c : sortedView.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void valuesIterator(Blackhole bh) {
        for (CharCursor c : sortedView.values()) {
            bh.consume(c.value);
        }
    }

    // ----- Construction (sorting) -----

    @Benchmark
    public SortedIterationIntCharHashMap constructWithKeyComparator() {
        return new SortedIterationIntCharHashMap(delegate, keyComparator);
    }

    @Benchmark
    public SortedIterationIntCharHashMap constructWithKeyValueComparator() {
        return new SortedIterationIntCharHashMap(delegate, keyValueComparator);
    }
}

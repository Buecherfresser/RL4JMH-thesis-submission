package bench.generated.c097;

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
import com.carrotsearch.hppc.SortedIterationIntShortHashMap;
import com.carrotsearch.hppc.IntShortHashMap;
import com.carrotsearch.hppc.cursors.IntShortCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.procedures.IntShortProcedure;
import com.carrotsearch.hppc.predicates.IntShortPredicate;
import com.carrotsearch.hppc.comparators.IntComparator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntShortHashMapBenchmark {

    private static final int MAP_SIZE = 1024;

    private IntShortHashMap delegate;
    private SortedIterationIntShortHashMap view;
    private int existingKey;
    private int missingKey;

    @Setup
    public void setup() {
        delegate = new IntShortHashMap();
        for (int i = 0; i < MAP_SIZE; i++) {
            int key = i * 2 + 1; // avoid zero (empty key sentinel)
            short value = (short) i;
            delegate.put(key, value);
        }
        existingKey = 1; // first inserted key
        missingKey = 0;   // sentinel not used as a key

        IntComparator keyComparator = (a, b) -> Integer.compare(a, b);
        view = new SortedIterationIntShortHashMap(delegate, keyComparator);
    }

    @Benchmark
    public int size() {
        return view.size();
    }

    @Benchmark
    public boolean containsKey() {
        return view.containsKey(existingKey);
    }

    @Benchmark
    public short getExisting() {
        return view.get(existingKey);
    }

    @Benchmark
    public short getOrDefaultExisting() {
        return view.getOrDefault(existingKey, (short) -1);
    }

    @Benchmark
    public short getOrDefaultMissing() {
        return view.getOrDefault(missingKey, (short) -1);
    }

    @Benchmark
    public int indexOf() {
        return view.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExists() {
        int idx = view.indexOf(existingKey);
        return view.indexExists(idx);
    }

    @Benchmark
    public short indexGet() {
        int idx = view.indexOf(existingKey);
        return view.indexGet(idx);
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return view.visualizeKeyDistribution(10);
    }

    @Benchmark
    public IntShortCursor iteratorNext() {
        Iterator<IntShortCursor> it = view.iterator();
        return it.hasNext() ? it.next() : null;
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        view.forEach(new IntShortProcedure() {
            @Override
            public void apply(int key, short value) {
                bh.consume(key);
                bh.consume(value);
            }
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        view.forEach(new IntShortPredicate() {
            @Override
            public boolean apply(int key, short value) {
                bh.consume(key);
                bh.consume(value);
                return true;
            }
        });
    }

    @Benchmark
    public int keysContainerSize() {
        return view.keys().size();
    }

    @Benchmark
    public int valuesContainerSize() {
        return view.values().size();
    }

    @Benchmark
    public int keysIteratorNext(Blackhole bh) {
        Iterator<IntCursor> it = view.keys().iterator();
        if (it.hasNext()) {
            IntCursor c = it.next();
            bh.consume(c.value);
            return 1;
        }
        return 0;
    }

    @Benchmark
    public int valuesIteratorNext(Blackhole bh) {
        Iterator<ShortCursor> it = view.values().iterator();
        if (it.hasNext()) {
            ShortCursor c = it.next();
            bh.consume(c.value);
            return 1;
        }
        return 0;
    }
}

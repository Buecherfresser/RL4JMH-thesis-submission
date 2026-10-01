package bench.generated.c089;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import com.carrotsearch.hppc.SortedIterationCharShortHashMap;
import com.carrotsearch.hppc.CharShortHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharShortComparator;
import com.carrotsearch.hppc.cursors.CharShortCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.predicates.CharShortPredicate;
import com.carrotsearch.hppc.procedures.CharShortProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharShortHashMapBenchmark {

    private static final int MAP_SIZE = 1024;

    private CharShortHashMap delegate;
    private SortedIterationCharShortHashMap view;
    private CharComparator keyComparator;
    private CharShortComparator keyValueComparator;
    private char sampleKey;
    private short sampleValue;

    @Setup(Level.Trial)
    public void setUp() {
        delegate = new CharShortHashMap(MAP_SIZE);
        keyComparator = new CharComparator() {
            @Override
            public int compare(char a, char b) {
                return Character.compare(a, b);
            }
        };
        keyValueComparator = new CharShortComparator() {
            @Override
            public int compare(char k1, short v1, char k2, short v2) {
                int cmp = Character.compare(k1, k2);
                if (cmp != 0) return cmp;
                return Short.compare(v1, v2);
            }
        };
        char lastKey = 0;
        short lastValue = 0;
        for (int i = 0; i < MAP_SIZE; i++) {
            char key = (char) i;
            short value = (short) (i * 2);
            delegate.put(key, value);
            lastKey = key;
            lastValue = value;
        }
        sampleKey = lastKey;
        sampleValue = lastValue;
        view = new SortedIterationCharShortHashMap(delegate, keyComparator);
    }

    @Benchmark
    public int benchmarkSize() {
        return view.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return view.isEmpty();
    }

    @Benchmark
    public boolean benchmarkContainsKey() {
        return view.containsKey(sampleKey);
    }

    @Benchmark
    public short benchmarkGet() {
        return view.get(sampleKey);
    }

    @Benchmark
    public short benchmarkGetOrDefault() {
        return view.getOrDefault(sampleKey, (short) -1);
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        Iterator<CharShortCursor> it = view.iterator();
        while (it.hasNext()) {
            CharShortCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        view.forEach((CharShortProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        view.forEach((CharShortPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkKeysContainerIteration(Blackhole bh) {
        Iterator<CharCursor> it = view.keys().iterator();
        while (it.hasNext()) {
            CharCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesContainerIteration(Blackhole bh) {
        Iterator<ShortCursor> it = view.values().iterator();
        while (it.hasNext()) {
            ShortCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return view.indexOf(sampleKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        int idx = view.indexOf(sampleKey);
        return view.indexExists(idx);
    }

    @Benchmark
    public short benchmarkIndexGet() {
        int idx = view.indexOf(sampleKey);
        return view.indexGet(idx);
    }

    @Benchmark
    public boolean benchmarkKeysContains() {
        return view.keys().contains(sampleKey);
    }

    @Benchmark
    public boolean benchmarkValuesContains() {
        return view.values().contains(sampleValue);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return view.visualizeKeyDistribution(10);
    }
}

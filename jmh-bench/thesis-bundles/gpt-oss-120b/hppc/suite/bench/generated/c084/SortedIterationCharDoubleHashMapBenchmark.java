package bench.generated.c084;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationCharDoubleHashMap;
import com.carrotsearch.hppc.CharDoubleHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharDoubleComparator;
import com.carrotsearch.hppc.CharCollection;
import com.carrotsearch.hppc.DoubleContainer;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.CharDoubleCursor;
import com.carrotsearch.hppc.cursors.DoubleCursor;
import com.carrotsearch.hppc.procedures.CharDoubleProcedure;
import com.carrotsearch.hppc.predicates.CharDoublePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharDoubleHashMapBenchmark {
    private CharDoubleHashMap delegate;
    private SortedIterationCharDoubleHashMap viewKeyComparator;
    private SortedIterationCharDoubleHashMap viewKeyValueComparator;
    private CharComparator keyComparator;
    private CharDoubleComparator keyValueComparator;

    @Setup
    public void setup() {
        int size = 1024;
        delegate = new CharDoubleHashMap(size);
        Random rnd = new Random(0);
        for (int i = 0; i < size; i++) {
            char key = (char) (rnd.nextInt(Character.MAX_VALUE - 1) + 1);
            double value = rnd.nextDouble();
            delegate.put(key, value);
        }

        keyComparator = (a, b) -> Character.compare(a, b);
        keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Character.compare(k1, k2);
            if (cmp != 0) return cmp;
            return Double.compare(v1, v2);
        };

        viewKeyComparator = new SortedIterationCharDoubleHashMap(delegate, keyComparator);
        viewKeyValueComparator = new SortedIterationCharDoubleHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public SortedIterationCharDoubleHashMap constructKeyView() {
        return new SortedIterationCharDoubleHashMap(delegate, keyComparator);
    }

    @Benchmark
    public SortedIterationCharDoubleHashMap constructKeyValueView() {
        return new SortedIterationCharDoubleHashMap(delegate, keyValueComparator);
    }

    @Benchmark
    public int sizeKeyView() {
        return viewKeyComparator.size();
    }

    @Benchmark
    public int sizeKeyValueView() {
        return viewKeyValueComparator.size();
    }

    @Benchmark
    public boolean isEmptyKeyView() {
        return viewKeyComparator.isEmpty();
    }

    @Benchmark
    public boolean isEmptyKeyValueView() {
        return viewKeyValueComparator.isEmpty();
    }

    @Benchmark
    public boolean containsKeyKeyView() {
        return viewKeyComparator.containsKey('a');
    }

    @Benchmark
    public boolean containsKeyKeyValueView() {
        return viewKeyValueComparator.containsKey('a');
    }

    @Benchmark
    public double getKeyView() {
        return viewKeyComparator.get('a');
    }

    @Benchmark
    public double getKeyValueView() {
        return viewKeyValueComparator.get('a');
    }

    @Benchmark
    public double getOrDefaultKeyView() {
        return viewKeyComparator.getOrDefault('a', -1.0);
    }

    @Benchmark
    public double getOrDefaultKeyValueView() {
        return viewKeyValueComparator.getOrDefault('a', -1.0);
    }

    @Benchmark
    public Iterator<CharDoubleCursor> iteratorKeyView() {
        return viewKeyComparator.iterator();
    }

    @Benchmark
    public Iterator<CharDoubleCursor> iteratorKeyValueView() {
        return viewKeyValueComparator.iterator();
    }

    @Benchmark
    public void forEachProcedureKeyView(Blackhole bh) {
        viewKeyComparator.forEach((CharDoubleProcedure) (k, v) -> bh.consume(k + v));
    }

    @Benchmark
    public void forEachProcedureKeyValueView(Blackhole bh) {
        viewKeyValueComparator.forEach((CharDoubleProcedure) (k, v) -> bh.consume(k + v));
    }

    @Benchmark
    public void forEachPredicateKeyView(Blackhole bh) {
        viewKeyComparator.forEach((CharDoublePredicate) (k, v) -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public void forEachPredicateKeyValueView(Blackhole bh) {
        viewKeyValueComparator.forEach((CharDoublePredicate) (k, v) -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public CharCollection keysKeyView() {
        return viewKeyComparator.keys();
    }

    @Benchmark
    public CharCollection keysKeyValueView() {
        return viewKeyValueComparator.keys();
    }

    @Benchmark
    public DoubleContainer valuesKeyView() {
        return viewKeyComparator.values();
    }

    @Benchmark
    public DoubleContainer valuesKeyValueView() {
        return viewKeyValueComparator.values();
    }

    @Benchmark
    public Iterator<CharCursor> keysIteratorKeyView(Blackhole bh) {
        Iterator<CharCursor> it = viewKeyComparator.keys().iterator();
        while (it.hasNext()) {
            CharCursor c = it.next();
            bh.consume(c.value);
        }
        return it;
    }

    @Benchmark
    public Iterator<DoubleCursor> valuesIteratorKeyView(Blackhole bh) {
        Iterator<DoubleCursor> it = viewKeyComparator.values().iterator();
        while (it.hasNext()) {
            DoubleCursor c = it.next();
            bh.consume(c.value);
        }
        return it;
    }

    @Benchmark
    public int indexOfKeyView() {
        return viewKeyComparator.indexOf('a');
    }

    @Benchmark
    public int indexOfKeyValueView() {
        return viewKeyValueComparator.indexOf('a');
    }

    @Benchmark
    public boolean indexExistsKeyView() {
        int idx = viewKeyComparator.indexOf('a');
        return viewKeyComparator.indexExists(idx);
    }

    @Benchmark
    public boolean indexExistsKeyValueView() {
        int idx = viewKeyValueComparator.indexOf('a');
        return viewKeyValueComparator.indexExists(idx);
    }

    @Benchmark
    public double indexGetKeyView() {
        int idx = viewKeyComparator.indexOf('a');
        return viewKeyComparator.indexGet(idx);
    }

    @Benchmark
    public double indexGetKeyValueView() {
        int idx = viewKeyValueComparator.indexOf('a');
        return viewKeyValueComparator.indexGet(idx);
    }

    @Benchmark
    public String visualizeKeyDistributionKeyView() {
        return viewKeyComparator.visualizeKeyDistribution(10);
    }

    @Benchmark
    public String visualizeKeyDistributionKeyValueView() {
        return viewKeyValueComparator.visualizeKeyDistribution(10);
    }
}

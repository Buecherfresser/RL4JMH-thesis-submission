package bench.generated.c110;

import com.carrotsearch.hppc.ObjectIntHashMap;
import com.carrotsearch.hppc.SortedIterationObjectIntHashMap;
import com.carrotsearch.hppc.comparators.ObjectIntComparator;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.ObjectIntCursor;
import com.carrotsearch.hppc.predicates.ObjectIntPredicate;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.procedures.ObjectIntProcedure;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectIntHashMapBenchmark {

    private ObjectIntHashMap<String> delegate;
    private SortedIterationObjectIntHashMap<String> view;
    private Comparator<String> keyComparator;
    private ObjectIntComparator<String> kvComparator;
    private String existingKey;
    private String missingKey;
    private int existingValue;
    private int existingIndex;

    @Setup(Level.Trial)
    public void setup() {
        int expectedElements = 1024;
        delegate = new ObjectIntHashMap<>(expectedElements);
        Random random = new Random(0x12345678L);
        for (int i = 0; i < expectedElements; i++) {
            delegate.put("key-" + i, random.nextInt());
        }

        keyComparator = Comparator.naturalOrder();
        kvComparator = (k1, v1, k2, v2) -> k1.compareTo(k2);

        view = new SortedIterationObjectIntHashMap<>(delegate, keyComparator);

        existingKey = "key-" + (expectedElements / 2);
        missingKey = "missing-key";
        existingValue = delegate.get(existingKey);
        existingIndex = delegate.indexOf(existingKey);
    }

    @Benchmark
    public SortedIterationObjectIntHashMap<String> constructorKeyComparator() {
        return new SortedIterationObjectIntHashMap<>(delegate, keyComparator);
    }

    @Benchmark
    public SortedIterationObjectIntHashMap<String> constructorKeyValueComparator() {
        return new SortedIterationObjectIntHashMap<>(delegate, kvComparator);
    }

    @Benchmark
    public int iterator() {
        int sum = 0;
        Iterator<ObjectIntCursor<String>> it = view.iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        bh.consume(view.forEach((ObjectIntProcedure<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        }));
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        bh.consume(view.forEach((ObjectIntPredicate<String>) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        }));
    }

    @Benchmark
    public boolean containsKeyHit() {
        return view.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsKeyMiss() {
        return view.containsKey(missingKey);
    }

    @Benchmark
    public int getHit() {
        return view.get(existingKey);
    }

    @Benchmark
    public int getOrDefaultHit() {
        return view.getOrDefault(existingKey, -1);
    }

    @Benchmark
    public int getOrDefaultMiss() {
        return view.getOrDefault(missingKey, -1);
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
    public int indexOf() {
        return view.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExists() {
        return view.indexExists(existingIndex);
    }

    @Benchmark
    public int indexGet() {
        return view.indexGet(existingIndex);
    }

    @Benchmark
    public boolean keysContains() {
        return view.keys().contains(existingKey);
    }

    @Benchmark
    public boolean valuesContains() {
        return view.values().contains(existingValue);
    }

    @Benchmark
    public void keysForEach(Blackhole bh) {
        bh.consume(view.keys().forEach((ObjectProcedure<String>) k -> bh.consume(k)));
    }

    @Benchmark
    public void valuesForEach(Blackhole bh) {
        bh.consume(view.values().forEach((IntProcedure) v -> bh.consume(v)));
    }

    @Benchmark
    public int keysIterator() {
        int count = 0;
        for (ObjectCursor<String> c : view.keys()) {
            count++;
        }
        return count;
    }

    @Benchmark
    public int valuesIterator() {
        int sum = 0;
        for (IntCursor c : view.values()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return view.visualizeKeyDistribution(100);
    }
}

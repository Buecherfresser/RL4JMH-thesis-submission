package bench.generated.c093;

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
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationIntFloatHashMap;
import com.carrotsearch.hppc.IntFloatHashMap;
import com.carrotsearch.hppc.cursors.IntFloatCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.FloatCursor;
import com.carrotsearch.hppc.IntCollection;
import com.carrotsearch.hppc.FloatContainer;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.predicates.IntFloatPredicate;
import com.carrotsearch.hppc.procedures.IntFloatProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntFloatHashMapBenchmark {

    private int elementCount;
    private IntFloatHashMap delegate;
    private SortedIterationIntFloatHashMap view;
    private int presentKey;
    private int absentKey;

    @Setup
    public void setup() {
        elementCount = 10_000;
        delegate = new IntFloatHashMap(elementCount);
        for (int i = 0; i < elementCount; i++) {
            delegate.put(i, i * 1.1f);
        }
        presentKey = elementCount / 2;
        absentKey = -1; // not present in the map

        IntComparator keyComparator = (a, b) -> Integer.compare(a, b);
        view = new SortedIterationIntFloatHashMap(delegate, keyComparator);
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
        return view.containsKey(presentKey);
    }

    @Benchmark
    public float benchmarkGet() {
        return view.get(presentKey);
    }

    @Benchmark
    public float benchmarkGetOrDefault() {
        return view.getOrDefault(absentKey, -1.0f);
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        Iterator<IntFloatCursor> it = view.iterator();
        while (it.hasNext()) {
            IntFloatCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        view.forEach((IntFloatProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        view.forEach((IntFloatPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkKeysIteration(Blackhole bh) {
        IntCollection keys = view.keys();
        Iterator<IntCursor> it = keys.iterator();
        while (it.hasNext()) {
            IntCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIteration(Blackhole bh) {
        FloatContainer values = view.values();
        Iterator<FloatCursor> it = values.iterator();
        while (it.hasNext()) {
            FloatCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return view.indexOf(presentKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists() {
        int idx = view.indexOf(presentKey);
        return view.indexExists(idx);
    }

    @Benchmark
    public float benchmarkIndexGet() {
        int idx = view.indexOf(presentKey);
        return view.indexGet(idx);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return view.visualizeKeyDistribution(10);
    }
}

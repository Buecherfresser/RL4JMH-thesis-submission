package bench.generated.c111;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Comparator;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.SortedIterationObjectLongHashMap;
import com.carrotsearch.hppc.ObjectLongHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectLongHashMapBenchmark {

    private static final int MAP_SIZE = 1024;
    private SortedIterationObjectLongHashMap<String> view;
    private ObjectLongHashMap<String> delegate;
    private String sampleKey;
    private Comparator<String> keyComparator;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(12345L);
        delegate = new ObjectLongHashMap<>(MAP_SIZE);
        for (int i = 0; i < MAP_SIZE; i++) {
            String key = "key-" + i;
            long value = rnd.nextLong();
            delegate.put(key, value);
        }
        sampleKey = "key-0";
        keyComparator = Comparator.naturalOrder();
        view = new SortedIterationObjectLongHashMap<>(delegate, keyComparator);
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
    public long benchmarkGet() {
        return view.get(sampleKey);
    }

    @Benchmark
    public long benchmarkGetOrDefault() {
        return view.getOrDefault(sampleKey, -1L);
    }

    @Benchmark
    public void benchmarkIterate(Blackhole bh) {
        Iterator<?> it = view.iterator();
        bh.consume(it);
    }

    @Benchmark
    public int benchmarkKeysSize() {
        return view.keys().size();
    }

    @Benchmark
    public int benchmarkValuesSize() {
        return view.values().size();
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
    public long benchmarkIndexGet() {
        int idx = view.indexOf(sampleKey);
        return view.indexGet(idx);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution() {
        return view.visualizeKeyDistribution(10);
    }
}

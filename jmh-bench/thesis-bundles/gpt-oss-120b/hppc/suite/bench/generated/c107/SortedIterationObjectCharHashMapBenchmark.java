package bench.generated.c107;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Comparator;
import com.carrotsearch.hppc.SortedIterationObjectCharHashMap;
import com.carrotsearch.hppc.ObjectCharHashMap;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectCharHashMapBenchmark {
    private ObjectCharHashMap<String> delegate;
    private SortedIterationObjectCharHashMap<String> view;
    private String lookupKey;
    private int lookupIndex;
    private int size;

    @Setup
    public void setup() {
        int expected = 1024;
        delegate = new ObjectCharHashMap<>(expected);
        Random rnd = new Random(12345);
        for (int i = 0; i < expected; i++) {
            String key = "key" + i;
            char value = (char) ('a' + (i % 26));
            delegate.put(key, value);
        }
        Comparator<String> keyComparator = Comparator.naturalOrder();
        view = new SortedIterationObjectCharHashMap<>(delegate, keyComparator);
        size = view.size();
        // Choose a key that is guaranteed to exist.
        lookupKey = "key" + (size / 2);
        lookupIndex = view.indexOf(lookupKey);
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
    public boolean containsKey() {
        return view.containsKey(lookupKey);
    }

    @Benchmark
    public char get() {
        return view.get(lookupKey);
    }

    @Benchmark
    public char getOrDefault() {
        return view.getOrDefault(lookupKey, (char) 0);
    }

    @Benchmark
    public int indexOf() {
        return view.indexOf(lookupKey);
    }

    @Benchmark
    public boolean indexExists() {
        return view.indexExists(lookupIndex);
    }

    @Benchmark
    public char indexGet() {
        return view.indexGet(lookupIndex);
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return view.visualizeKeyDistribution(10);
    }
}

package bench.generated.c083;

import com.carrotsearch.hppc.CharCharHashMap;
import com.carrotsearch.hppc.SortedIterationCharCharHashMap;
import com.carrotsearch.hppc.cursors.CharCharCursor;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.predicates.CharCharPredicate;
import com.carrotsearch.hppc.procedures.CharCharProcedure;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationCharCharHashMapBenchmark {

    private static final int SIZE = 1000;
    private static final long SEED = 12345L;

    private CharCharHashMap delegate;
    private SortedIterationCharCharHashMap view;
    private char sampleKey;
    private char sampleValue;
    private int sampleIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(SEED);
        delegate = new CharCharHashMap(SIZE);
        for (int i = 0; i < SIZE; i++) {
            char key = (char) random.nextInt(Character.MAX_VALUE);
            char value = (char) random.nextInt(Character.MAX_VALUE);
            delegate.put(key, value);
        }
        // Build the view once for read benchmarks
        view = new SortedIterationCharCharHashMap(delegate, (a, b) -> Character.compare(a, b));
        // Pick a key that exists
        sampleKey = delegate.keys().iterator().next().value;
        sampleValue = delegate.get(sampleKey);
        sampleIndex = view.indexOf(sampleKey);
    }

    // --- Construction benchmarks ---

    @Benchmark
    public SortedIterationCharCharHashMap constructorWithKeyComparator() {
        return new SortedIterationCharCharHashMap(delegate, (a, b) -> Character.compare(a, b));
    }

    @Benchmark
    public SortedIterationCharCharHashMap constructorWithKeyValueComparator() {
        return new SortedIterationCharCharHashMap(delegate,
                (k1, v1, k2, v2) -> {
                    int cmp = Character.compare(k1, k2);
                    return cmp != 0 ? cmp : Character.compare(v1, v2);
                });
    }

    // --- Read benchmarks ---

    @Benchmark
    public boolean containsKey() {
        return view.containsKey(sampleKey);
    }

    @Benchmark
    public char get() {
        return view.get(sampleKey);
    }

    @Benchmark
    public char getOrDefault() {
        return view.getOrDefault(sampleKey, (char) 0);
    }

    @Benchmark
    public int indexOf() {
        return view.indexOf(sampleKey);
    }

    @Benchmark
    public boolean indexExists() {
        return view.indexExists(sampleIndex);
    }

    @Benchmark
    public char indexGet() {
        return view.indexGet(sampleIndex);
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
    public long forEachProcedure() {
        final long[] sum = new long[1];
        view.forEach((CharCharProcedure) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public long forEachPredicate() {
        final long[] sum = new long[1];
        view.forEach((CharCharPredicate) (k, v) -> {
            sum[0] += v;
            return true;
        });
        return sum[0];
    }

    @Benchmark
    public long iterator() {
        long sum = 0;
        for (CharCharCursor cursor : view) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public long keys() {
        long sum = 0;
        for (CharCursor cursor : view.keys()) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public long values() {
        long sum = 0;
        for (CharCursor cursor : view.values()) {
            sum += cursor.value;
        }
        return sum;
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return view.visualizeKeyDistribution(64);
    }
}

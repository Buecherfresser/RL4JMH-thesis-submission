package bench.generated.c086;

import com.carrotsearch.hppc.CharIntHashMap;
import com.carrotsearch.hppc.SortedIterationCharIntHashMap;
import com.carrotsearch.hppc.comparators.CharComparator;
import com.carrotsearch.hppc.comparators.CharIntComparator;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.CharIntCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.CharIntProcedure;
import com.carrotsearch.hppc.predicates.CharIntPredicate;
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
public class SortedIterationCharIntHashMapBenchmark {

    private static final int MAP_SIZE = 1_000;

    private CharIntHashMap delegate;
    private SortedIterationCharIntHashMap view;
    private char queryKey;
    private char nonExistingKey;
    private int validIndex;
    private final CharComparator charComparator = (a, b) -> Character.compare(a, b);
    private final CharIntComparator charIntComparator = (k1, v1, k2, v2) -> {
        int c = Character.compare(k1, k2);
        return c != 0 ? c : Integer.compare(v1, v2);
    };
    private final int defaultValue = Integer.MIN_VALUE;

    @Setup
    public void setup() {
        delegate = new CharIntHashMap();
        Random r = new Random(0x12345678L);
        for (int i = 0; i < MAP_SIZE; i++) {
            char key;
            do {
                key = (char) (r.nextInt(Character.MAX_VALUE + 1));
            } while (delegate.containsKey(key));
            int value = r.nextInt();
            delegate.put(key, value);
        }

        view = new SortedIterationCharIntHashMap(delegate, charComparator);

        // Pick a key that exists (from the view's iteration order)
        validIndex = view.iterationOrder[r.nextInt(view.iterationOrder.length)];
        queryKey = delegate.keys[validIndex];

        // Pick a key that does not exist
        do {
            nonExistingKey = (char) (r.nextInt(Character.MAX_VALUE + 1));
        } while (delegate.containsKey(nonExistingKey));
    }

    // ===== Construction =====

    @Benchmark
    public SortedIterationCharIntHashMap constructionKeyComparator() {
        return new SortedIterationCharIntHashMap(delegate, charComparator);
    }

    @Benchmark
    public SortedIterationCharIntHashMap constructionKeyValueComparator() {
        return new SortedIterationCharIntHashMap(delegate, charIntComparator);
    }

    // ===== Lookup =====

    @Benchmark
    public boolean containsKeyHit() {
        return view.containsKey(queryKey);
    }

    @Benchmark
    public boolean containsKeyMiss() {
        return view.containsKey(nonExistingKey);
    }

    @Benchmark
    public int getHit() {
        return view.get(queryKey);
    }

    @Benchmark
    public int getOrDefaultHit() {
        return view.getOrDefault(queryKey, defaultValue);
    }

    @Benchmark
    public int indexOfHit() {
        return view.indexOf(queryKey);
    }

    @Benchmark
    public boolean indexExists() {
        return view.indexExists(validIndex);
    }

    @Benchmark
    public int indexGet() {
        return view.indexGet(validIndex);
    }

    // ===== Size / isEmpty =====

    @Benchmark
    public int size() {
        return view.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return view.isEmpty();
    }

    // ===== Iteration / ForEach =====

    @Benchmark
    public long iteratorSum() {
        long sum = 0;
        for (CharIntCursor c : view) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long forEachProcedureSum() {
        long[] sum = new long[1];
        view.forEach((CharIntProcedure) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public long forEachPredicateSum() {
        long[] sum = new long[1];
        view.forEach((CharIntPredicate) (k, v) -> {
            sum[0] += v;
            return true;
        });
        return sum[0];
    }

    // ===== Key and Value views =====

    @Benchmark
    public long keysIteratorSum() {
        long sum = 0;
        for (CharCursor c : view.keys()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long valuesIteratorSum() {
        long sum = 0;
        for (IntCursor c : view.values()) {
            sum += c.value;
        }
        return sum;
    }

    // ===== Visualize =====

    @Benchmark
    public String visualizeKeyDistribution() {
        return view.visualizeKeyDistribution(10);
    }
}

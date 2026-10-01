package bench.generated.c114;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ShortByteHashMap;
import com.carrotsearch.hppc.SortedIterationShortByteHashMap;
import com.carrotsearch.hppc.comparators.ShortComparator;
import com.carrotsearch.hppc.cursors.ShortByteCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.procedures.ShortByteProcedure;
import com.carrotsearch.hppc.predicates.ShortBytePredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationShortByteHashMapBenchmark {

    private ShortByteHashMap delegate;
    private SortedIterationShortByteHashMap sortedView;
    private short[] keys;
    private short keyToLookup;
    private int indexOfKey;
    private byte valueAtIndex;
    private int size;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(12345);
        int n = 1000;
        delegate = new ShortByteHashMap(n);
        keys = new short[n];
        // Use keys 1..n (avoid 0 which is the empty key sentinel)
        for (int i = 0; i < n; i++) {
            keys[i] = (short) (i + 1);
        }
        // Shuffle keys
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            short tmp = keys[i];
            keys[i] = keys[j];
            keys[j] = tmp;
        }
        for (int i = 0; i < n; i++) {
            byte value = (byte) random.nextInt(256);
            delegate.put(keys[i], value);
        }
        // Create sorted view with a key comparator
        sortedView = new SortedIterationShortByteHashMap(delegate,
                (ShortComparator) (a, b) -> Short.compare(a, b));
        size = sortedView.size();
        keyToLookup = keys[0];
        indexOfKey = sortedView.indexOf(keyToLookup);
        valueAtIndex = sortedView.indexGet(indexOfKey);
    }

    // Constructor (sorting cost)
    @Benchmark
    public SortedIterationShortByteHashMap constructView() {
        return new SortedIterationShortByteHashMap(delegate,
                (ShortComparator) (a, b) -> Short.compare(a, b));
    }

    // Basic read operations
    @Benchmark
    public byte get() {
        return sortedView.get(keyToLookup);
    }

    @Benchmark
    public byte getOrDefault() {
        return sortedView.getOrDefault(keyToLookup, (byte) 0);
    }

    @Benchmark
    public boolean containsKey() {
        return sortedView.containsKey(keyToLookup);
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
    public int indexOf() {
        return sortedView.indexOf(keyToLookup);
    }

    @Benchmark
    public byte indexGet() {
        return sortedView.indexGet(indexOfKey);
    }

    // Iteration via forEach (procedure)
    @Benchmark
    public long forEachProcedure() {
        final long[] sum = new long[1];
        sortedView.forEach((ShortByteProcedure) (k, v) -> sum[0] += k + v);
        return sum[0];
    }

    // Iteration via forEach (predicate, always true)
    @Benchmark
    public long forEachPredicate() {
        final long[] sum = new long[1];
        sortedView.forEach((ShortBytePredicate) (k, v) -> {
            sum[0] += k + v;
            return true;
        });
        return sum[0];
    }

    // Iteration via iterator
    @Benchmark
    public long iterator() {
        long sum = 0;
        for (ShortByteCursor c : sortedView) {
            sum += c.key + c.value;
        }
        return sum;
    }

    // Keys view iteration
    @Benchmark
    public long keysIteration() {
        long sum = 0;
        for (ShortCursor c : sortedView.keys()) {
            sum += c.value;
        }
        return sum;
    }

    // Values view iteration
    @Benchmark
    public long valuesIteration() {
        long sum = 0;
        for (ByteCursor c : sortedView.values()) {
            sum += c.value;
        }
        return sum;
    }

    // Keys view contains
    @Benchmark
    public boolean keysContains() {
        return sortedView.keys().contains(keyToLookup);
    }

    // Values view contains (iterates internally)
    @Benchmark
    public boolean valuesContains() {
        return sortedView.values().contains(valueAtIndex);
    }

    // Visualize key distribution
    @Benchmark
    public String visualizeKeyDistribution() {
        return sortedView.visualizeKeyDistribution(10);
    }
}

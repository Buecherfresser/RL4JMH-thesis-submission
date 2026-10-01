package bench.generated.c115;

import com.carrotsearch.hppc.ShortCharHashMap;
import com.carrotsearch.hppc.SortedIterationShortCharHashMap;
import com.carrotsearch.hppc.comparators.ShortComparator;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.cursors.ShortCharCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.predicates.ShortCharPredicate;
import com.carrotsearch.hppc.procedures.ShortCharProcedure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationShortCharHashMapBenchmark {

    private static final int SIZE = 1000;

    private ShortCharHashMap delegate;
    private SortedIterationShortCharHashMap view;
    private short[] presentKeys;
    private short[] absentKeys;
    private char[] values;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(0x12345678L);
        delegate = new ShortCharHashMap(SIZE);
        presentKeys = new short[SIZE];
        values = new char[SIZE];

        // Generate unique non-zero short keys
        HashSet<Short> keySet = new HashSet<>();
        while (keySet.size() < SIZE) {
            short k = (short) random.nextInt(Short.MAX_VALUE + 1);
            if (k != 0) {
                keySet.add(k);
            }
        }

        int i = 0;
        for (Short k : keySet) {
            presentKeys[i] = k;
            values[i] = (char) random.nextInt(Character.MAX_VALUE + 1);
            delegate.put(k, values[i]);
            i++;
        }

        // Generate absent keys (non-zero, not in the map)
        absentKeys = new short[SIZE];
        int j = 0;
        while (j < SIZE) {
            short k = (short) random.nextInt(Short.MAX_VALUE + 1);
            if (k != 0 && !keySet.contains(k)) {
                absentKeys[j++] = k;
            }
        }

        // Create the sorted view with a key comparator
        view = new SortedIterationShortCharHashMap(delegate, new ShortComparator() {
            @Override
            public int compare(short a, short b) {
                return Short.compare(a, b);
            }
        });
    }

    // --- Construction ---
    @Benchmark
    public SortedIterationShortCharHashMap construct() {
        return new SortedIterationShortCharHashMap(delegate, new ShortComparator() {
            @Override
            public int compare(short a, short b) {
                return Short.compare(a, b);
            }
        });
    }

    // --- Lookup operations ---
    @Benchmark
    public boolean containsKeyPresent() {
        return view.containsKey(presentKeys[0]);
    }

    @Benchmark
    public boolean containsKeyAbsent() {
        return view.containsKey(absentKeys[0]);
    }

    @Benchmark
    public char getPresent() {
        return view.get(presentKeys[0]);
    }

    @Benchmark
    public char getAbsent() {
        return view.get(absentKeys[0]);
    }

    @Benchmark
    public char getOrDefaultPresent() {
        return view.getOrDefault(presentKeys[0], (char) 0);
    }

    @Benchmark
    public char getOrDefaultAbsent() {
        return view.getOrDefault(absentKeys[0], (char) 42);
    }

    // --- Index-based access ---
    @Benchmark
    public int indexOfPresent() {
        return view.indexOf(presentKeys[0]);
    }

    @Benchmark
    public boolean indexExists() {
        int index = view.indexOf(presentKeys[0]);
        return view.indexExists(index);
    }

    @Benchmark
    public char indexGet() {
        int index = view.indexOf(presentKeys[0]);
        return view.indexGet(index);
    }

    // --- Size and emptiness ---
    @Benchmark
    public int size() {
        return view.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return view.isEmpty();
    }

    // --- Iteration ---
    @Benchmark
    public void iterator(Blackhole bh) {
        for (ShortCharCursor c : view) {
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        view.forEach((ShortCharProcedure) (k, v) -> bh.consume(k + v));
    }

    @Benchmark
    public void forEachPredicateEarlyStop(Blackhole bh) {
        view.forEach((ShortCharPredicate) (k, v) -> {
            bh.consume(k);
            return k < 1000; // stop early after a condition
        });
    }

    // --- Key and value views ---
    @Benchmark
    public void keysIterate(Blackhole bh) {
        for (ShortCursor c : view.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void valuesIterate(Blackhole bh) {
        for (CharCursor c : view.values()) {
            bh.consume(c.value);
        }
    }

    // --- Distribution visualization ---
    @Benchmark
    public String visualizeKeyDistribution() {
        return view.visualizeKeyDistribution(10);
    }
}

package bench.generated.c098;

import com.carrotsearch.hppc.LongByteHashMap;
import com.carrotsearch.hppc.SortedIterationLongByteHashMap;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.cursors.LongByteCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongBytePredicate;
import com.carrotsearch.hppc.procedures.LongByteProcedure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongByteHashMapBenchmark {

    private int size = 1000;
    private int visualizeChars = 20;

    private LongByteHashMap delegate;
    private SortedIterationLongByteHashMap view;
    private long existingKey;
    private long missingKey;
    private int existingIndex;
    private LongComparator keyComparator;

    @Setup
    public void setup() {
        Random random = new Random(0x12345678L);
        delegate = new LongByteHashMap(size);
        for (int i = 0; i < size; i++) {
            long key;
            do {
                key = random.nextLong();
            } while (delegate.containsKey(key));
            byte value = (byte) random.nextInt();
            delegate.put(key, value);
        }

        keyComparator = new LongComparator() {
            @Override
            public int compare(long a, long b) {
                return Long.compare(a, b);
            }
        };
        view = new SortedIterationLongByteHashMap(delegate, keyComparator);

        // Pick an existing key from the sorted iteration order
        existingKey = delegate.keys[view.iterationOrder[0]];
        existingIndex = view.indexOf(existingKey);

        // Find a missing key
        missingKey = existingKey + 1;
        while (delegate.containsKey(missingKey)) {
            missingKey++;
        }
    }

    // --- Read-only operations ---

    @Benchmark
    public boolean containsKeyExisting() {
        return view.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsKeyMissing() {
        return view.containsKey(missingKey);
    }

    @Benchmark
    public byte getExisting() {
        return view.get(existingKey);
    }

    @Benchmark
    public byte getOrDefaultExisting() {
        return view.getOrDefault(existingKey, (byte) 0);
    }

    @Benchmark
    public byte getOrDefaultMissing() {
        return view.getOrDefault(missingKey, (byte) 42);
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
    public int indexOfExisting() {
        return view.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExists() {
        return view.indexExists(existingIndex);
    }

    @Benchmark
    public byte indexGet() {
        return view.indexGet(existingIndex);
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return view.visualizeKeyDistribution(visualizeChars);
    }

    // --- Iteration ---

    @Benchmark
    public void iterator(Blackhole bh) {
        Iterator<LongByteCursor> it = view.iterator();
        while (it.hasNext()) {
            LongByteCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        LongByteProcedure procedure = (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        };
        view.forEach(procedure);
        bh.consume(procedure);
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        LongBytePredicate predicate = (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        };
        view.forEach(predicate);
        bh.consume(predicate);
    }

    @Benchmark
    public void keysIterate(Blackhole bh) {
        for (LongCursor c : view.keys()) {
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void valuesIterate(Blackhole bh) {
        for (ByteCursor c : view.values()) {
            bh.consume(c.value);
        }
    }

    // --- Construction (sorting) ---

    @Benchmark
    public SortedIterationLongByteHashMap constructWithKeyComparator() {
        return new SortedIterationLongByteHashMap(delegate, keyComparator);
    }
}

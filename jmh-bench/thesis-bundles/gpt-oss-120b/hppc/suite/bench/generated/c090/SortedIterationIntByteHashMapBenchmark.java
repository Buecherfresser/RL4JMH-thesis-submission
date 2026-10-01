package bench.generated.c090;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationIntByteHashMap;
import com.carrotsearch.hppc.IntByteHashMap;
import com.carrotsearch.hppc.cursors.IntByteCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.procedures.IntByteProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.procedures.ByteProcedure;
import com.carrotsearch.hppc.predicates.IntBytePredicate;
import com.carrotsearch.hppc.predicates.IntPredicate;
import com.carrotsearch.hppc.predicates.BytePredicate;
import com.carrotsearch.hppc.comparators.IntComparator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntByteHashMapBenchmark {

    private IntByteHashMap delegate;
    private SortedIterationIntByteHashMap view;
    private int[] keysArray;
    private int[] shuffledIndices;
    private int sampleKey;
    private int sampleIndex;

    @Setup(Level.Trial)
    public void setup() {
        final int size = 1024;
        Random rand = new Random(12345);
        delegate = new IntByteHashMap(size);
        for (int i = 0; i < size; i++) {
            int key = rand.nextInt();
            byte value = (byte) rand.nextInt(256);
            delegate.put(key, value);
        }

        IntComparator keyComparator = (a, b) -> Integer.compare(a, b);
        view = new SortedIterationIntByteHashMap(delegate, keyComparator);

        // capture keys for random access
        keysArray = new int[size];
        int idx = 0;
        for (IntByteCursor c : delegate) {
            keysArray[idx++] = c.key;
        }

        // shuffled indices for indexGet benchmark
        shuffledIndices = new int[size];
        for (int i = 0; i < size; i++) {
            shuffledIndices[i] = i;
        }
        for (int i = size - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = shuffledIndices[i];
            shuffledIndices[i] = shuffledIndices[j];
            shuffledIndices[j] = tmp;
        }

        sampleKey = keysArray[0];
        sampleIndex = shuffledIndices[0];
    }

    @Benchmark
    public void benchmarkForEachProcedure(Blackhole bh) {
        view.forEach((IntByteProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void benchmarkForEachPredicate(Blackhole bh) {
        view.forEach((IntBytePredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkIterator(Blackhole bh) {
        Iterator<IntByteCursor> it = view.iterator();
        while (it.hasNext()) {
            IntByteCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkKeysIterator(Blackhole bh) {
        Iterator<IntCursor> it = view.keys().iterator();
        while (it.hasNext()) {
            IntCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkValuesIterator(Blackhole bh) {
        Iterator<ByteCursor> it = view.values().iterator();
        while (it.hasNext()) {
            ByteCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void benchmarkKeysForEachProcedure(Blackhole bh) {
        view.keys().forEach((IntProcedure) k -> bh.consume(k));
    }

    @Benchmark
    public void benchmarkKeysForEachPredicate(Blackhole bh) {
        view.keys().forEach((IntPredicate) k -> {
            bh.consume(k);
            return true;
        });
    }

    @Benchmark
    public void benchmarkValuesForEachProcedure(Blackhole bh) {
        view.values().forEach((ByteProcedure) v -> bh.consume(v));
    }

    @Benchmark
    public void benchmarkValuesForEachPredicate(Blackhole bh) {
        view.values().forEach((BytePredicate) v -> {
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void benchmarkGet(Blackhole bh) {
        byte v = view.get(sampleKey);
        bh.consume(v);
    }

    @Benchmark
    public void benchmarkGetOrDefault(Blackhole bh) {
        byte v = view.getOrDefault(sampleKey, (byte) -1);
        bh.consume(v);
    }

    @Benchmark
    public void benchmarkContainsKey(Blackhole bh) {
        boolean b = view.containsKey(sampleKey);
        bh.consume(b);
    }

    @Benchmark
    public void benchmarkSize(Blackhole bh) {
        int s = view.size();
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkIsEmpty(Blackhole bh) {
        boolean e = view.isEmpty();
        bh.consume(e);
    }

    @Benchmark
    public void benchmarkIndexOf(Blackhole bh) {
        int idx = view.indexOf(sampleKey);
        bh.consume(idx);
    }

    @Benchmark
    public void benchmarkIndexExists(Blackhole bh) {
        boolean exists = view.indexExists(sampleIndex);
        bh.consume(exists);
    }

    @Benchmark
    public void benchmarkIndexGet(Blackhole bh) {
        byte val = view.indexGet(sampleIndex);
        bh.consume(val);
    }
}

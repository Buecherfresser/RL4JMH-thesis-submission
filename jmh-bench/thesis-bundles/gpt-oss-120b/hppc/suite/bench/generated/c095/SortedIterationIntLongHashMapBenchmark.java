package bench.generated.c095;

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
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.SortedIterationIntLongHashMap;
import com.carrotsearch.hppc.IntLongHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntLongComparator;
import com.carrotsearch.hppc.procedures.IntLongProcedure;
import com.carrotsearch.hppc.predicates.IntLongPredicate;
import com.carrotsearch.hppc.cursors.IntLongCursor;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.cursors.LongCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationIntLongHashMapBenchmark {
    private SortedIterationIntLongHashMap map;
    private int existingKey;
    private int missingKey;

    @Setup(Level.Trial)
    public void setup() {
        int mapSize = 1024;
        Random rand = new Random(12345);
        IntLongHashMap delegate = new IntLongHashMap(mapSize);
        for (int i = 0; i < mapSize; i++) {
            int key;
            do {
                key = rand.nextInt();
            } while (delegate.containsKey(key));
            long value = rand.nextLong();
            delegate.put(key, value);
            if (i == 0) {
                existingKey = key;
            }
        }
        int miss;
        do {
            miss = rand.nextInt();
        } while (delegate.containsKey(miss));
        missingKey = miss;

        IntComparator keyComp = (IntComparator) (a, b) -> Integer.compare(a, b);
        map = new SortedIterationIntLongHashMap(delegate, keyComp);
    }

    @Benchmark
    public int size() {
        return map.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Benchmark
    public long getExisting() {
        return map.get(existingKey);
    }

    @Benchmark
    public long getMissing() {
        return map.get(missingKey);
    }

    @Benchmark
    public boolean containsExisting() {
        return map.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsMissing() {
        return map.containsKey(missingKey);
    }

    @Benchmark
    public int indexOfExisting() {
        return map.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExistsExisting() {
        return map.indexExists(map.indexOf(existingKey));
    }

    @Benchmark
    public long indexGetExisting() {
        int idx = map.indexOf(existingKey);
        return map.indexGet(idx);
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        map.forEach((IntLongProcedure) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
        });
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        map.forEach((IntLongPredicate) (k, v) -> {
            bh.consume(k);
            bh.consume(v);
            return true;
        });
    }

    @Benchmark
    public void iteratorConsume(Blackhole bh) {
        Iterator<IntLongCursor> it = map.iterator();
        while (it.hasNext()) {
            IntLongCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void keysIteratorConsume(Blackhole bh) {
        Iterator<IntCursor> it = map.keys().iterator();
        while (it.hasNext()) {
            IntCursor c = it.next();
            bh.consume(c.value);
        }
    }

    @Benchmark
    public void valuesIteratorConsume(Blackhole bh) {
        Iterator<LongCursor> it = map.values().iterator();
        while (it.hasNext()) {
            LongCursor c = it.next();
            bh.consume(c.value);
        }
    }
}

package bench.generated.c111;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Comparator;
import java.util.Random;
import com.carrotsearch.hppc.SortedIterationObjectLongHashMap;
import com.carrotsearch.hppc.ObjectLongHashMap;
import com.carrotsearch.hppc.comparators.ObjectLongComparator;
import com.carrotsearch.hppc.cursors.ObjectLongCursor;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.procedures.ObjectLongProcedure;
import com.carrotsearch.hppc.predicates.ObjectLongPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationObjectLongHashMapBenchmark {

    private ObjectLongHashMap<String> delegate;
    private SortedIterationObjectLongHashMap<String> sorted;
    private Comparator<String> keyComparator;
    private ObjectLongComparator<String> keyValueComparator;
    private String existingKey;
    private String missingKey;
    private long existingValue;
    private long missingValue;
    private int existingIndex;
    private int size;

    @Setup(Level.Trial)
    public void setup() {
        size = 1000;
        Random random = new Random(0x12345678L);
        delegate = new ObjectLongHashMap<String>();
        for (int i = 0; i < size; i++) {
            delegate.put("key-" + i, random.nextLong());
        }
        existingKey = "key-0";
        existingValue = delegate.get(existingKey);
        missingKey = "missing-key";

        long candidate = 0;
        while (delegate.values().contains(candidate)) {
            candidate++;
        }
        missingValue = candidate;

        keyComparator = Comparator.naturalOrder();
        keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = k1.compareTo(k2);
            if (cmp == 0) {
                cmp = Long.compare(v1, v2);
            }
            return cmp;
        };
        sorted = new SortedIterationObjectLongHashMap<String>(delegate, keyComparator);
        existingIndex = sorted.indexOf(existingKey);
    }

    @Benchmark
    public SortedIterationObjectLongHashMap<String> constructorKeyComparator() {
        return new SortedIterationObjectLongHashMap<String>(delegate, keyComparator);
    }

    @Benchmark
    public SortedIterationObjectLongHashMap<String> constructorKeyValueComparator() {
        return new SortedIterationObjectLongHashMap<String>(delegate, keyValueComparator);
    }

    @Benchmark
    public boolean containsKeyHit() {
        return sorted.containsKey(existingKey);
    }

    @Benchmark
    public boolean containsKeyMiss() {
        return sorted.containsKey(missingKey);
    }

    @Benchmark
    public long getHit() {
        return sorted.get(existingKey);
    }

    @Benchmark
    public void getHitConsume(Blackhole bh) {
        bh.consume(sorted.get(existingKey));
    }

    @Benchmark
    public long getOrDefaultHit() {
        return sorted.getOrDefault(existingKey, -1L);
    }

    @Benchmark
    public long getOrDefaultMiss() {
        return sorted.getOrDefault(missingKey, -1L);
    }

    @Benchmark
    public int size() {
        return sorted.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return sorted.isEmpty();
    }

    @Benchmark
    public int indexOfHit() {
        return sorted.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExists() {
        return sorted.indexExists(existingIndex);
    }

    @Benchmark
    public long indexGet() {
        return sorted.indexGet(existingIndex);
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        ObjectLongProcedure<String> proc = (k, v) -> bh.consume(v);
        bh.consume(sorted.forEach(proc));
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        ObjectLongPredicate<String> pred = (k, v) -> {
            bh.consume(v);
            return true;
        };
        bh.consume(sorted.forEach(pred));
    }

    @Benchmark
    public long iteratorIterate() {
        long sum = 0;
        for (ObjectLongCursor<String> c : sorted) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long keysIterator() {
        long count = 0;
        for (ObjectCursor<String> c : sorted.keys()) {
            count++;
        }
        return count;
    }

    @Benchmark
    public long valuesIterator() {
        long sum = 0;
        for (LongCursor c : sorted.values()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public boolean valuesContainsHit() {
        return sorted.values().contains(existingValue);
    }

    @Benchmark
    public boolean valuesContainsMiss() {
        return sorted.values().contains(missingValue);
    }

    @Benchmark
    public String visualizeKeyDistribution() {
        return sorted.visualizeKeyDistribution(64);
    }
}

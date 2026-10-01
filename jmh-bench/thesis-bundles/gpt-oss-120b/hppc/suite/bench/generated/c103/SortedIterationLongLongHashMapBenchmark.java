package bench.generated.c103;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationLongLongHashMap;
import com.carrotsearch.hppc.LongLongHashMap;
import com.carrotsearch.hppc.cursors.LongLongCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.predicates.LongPredicate;
import com.carrotsearch.hppc.predicates.LongLongPredicate;
import com.carrotsearch.hppc.procedures.LongLongProcedure;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongLongComparator;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongLongHashMapBenchmark {

    private static final int MAP_SIZE = 1024;
    private static final long MISSING_KEY = Long.MAX_VALUE;

    private LongLongHashMap delegate;
    private SortedIterationLongLongHashMap viewKeyComparator;
    private SortedIterationLongLongHashMap viewKeyValueComparator;
    private long[] keys;
    private long[] values;
    private long existingKey;
    private int existingIndex;
    private long threshold;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(0x1234ABCDL);
        delegate = new LongLongHashMap(MAP_SIZE);
        keys = new long[MAP_SIZE];
        values = new long[MAP_SIZE];
        for (int i = 0; i < MAP_SIZE; i++) {
            long k = rnd.nextLong();
            long v = rnd.nextLong();
            delegate.put(k, v);
            keys[i] = k;
            values[i] = v;
        }
        // natural order comparator for keys
        LongComparator keyComp = new LongComparator() {
            @Override
            public int compare(long a, long b) {
                return Long.compare(a, b);
            }
        };
        // comparator on key+value
        LongLongComparator keyValueComp = new LongLongComparator() {
            @Override
            public int compare(long k1, long v1, long k2, long v2) {
                int c = Long.compare(k1, k2);
                return (c != 0) ? c : Long.compare(v1, v2);
            }
        };
        viewKeyComparator = new SortedIterationLongLongHashMap(delegate, keyComp);
        viewKeyValueComparator = new SortedIterationLongLongHashMap(delegate, keyValueComp);
        existingKey = keys[MAP_SIZE / 2];
        existingIndex = viewKeyComparator.indexOf(existingKey);
        threshold = keys[MAP_SIZE / 4];
    }

    @Benchmark
    public int size() {
        return viewKeyComparator.size();
    }

    @Benchmark
    public boolean isEmpty() {
        return viewKeyComparator.isEmpty();
    }

    @Benchmark
    public boolean containsKey() {
        return viewKeyComparator.containsKey(existingKey);
    }

    @Benchmark
    public long getExisting() {
        return viewKeyComparator.get(existingKey);
    }

    @Benchmark
    public long getOrDefaultMissing() {
        return viewKeyComparator.getOrDefault(MISSING_KEY, -1L);
    }

    @Benchmark
    public long iterateSumKeys() {
        long sum = 0L;
        for (LongLongCursor c : viewKeyComparator) {
            sum += c.key;
        }
        return sum;
    }

    @Benchmark
    public long iterateSumValues() {
        long sum = 0L;
        for (LongLongCursor c : viewKeyComparator) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long forEachSum(Blackhole bh) {
        final long[] acc = new long[1];
        viewKeyComparator.forEach(new LongLongProcedure() {
            @Override
            public void apply(long k, long v) {
                acc[0] += k + v;
            }
        });
        bh.consume(acc[0]);
        return acc[0];
    }

    @Benchmark
    public long forEachPredicateSum(Blackhole bh) {
        final long[] acc = new long[1];
        viewKeyComparator.forEach(new LongLongPredicate() {
            @Override
            public boolean apply(long k, long v) {
                if (k > threshold) {
                    return false;
                }
                acc[0] += k;
                return true;
            }
        });
        bh.consume(acc[0]);
        return acc[0];
    }

    @Benchmark
    public int keysSize() {
        return viewKeyComparator.keys().size();
    }

    @Benchmark
    public long keysIterateSum() {
        long sum = 0L;
        Iterator<LongCursor> it = viewKeyComparator.keys().iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public long keysForEachSum(Blackhole bh) {
        final long[] acc = new long[1];
        viewKeyComparator.keys().forEach(new LongProcedure() {
            @Override
            public void apply(long v) {
                acc[0] += v;
            }
        });
        bh.consume(acc[0]);
        return acc[0];
    }

    @Benchmark
    public int valuesSize() {
        return viewKeyComparator.values().size();
    }

    @Benchmark
    public long valuesIterateSum() {
        long sum = 0L;
        Iterator<LongCursor> it = viewKeyComparator.values().iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    @Benchmark
    public long valuesForEachSum(Blackhole bh) {
        final long[] acc = new long[1];
        viewKeyComparator.values().forEach(new LongProcedure() {
            @Override
            public void apply(long v) {
                acc[0] += v;
            }
        });
        bh.consume(acc[0]);
        return acc[0];
    }

    @Benchmark
    public int indexOfExisting() {
        return viewKeyComparator.indexOf(existingKey);
    }

    @Benchmark
    public boolean indexExists() {
        return viewKeyComparator.indexExists(existingIndex);
    }

    @Benchmark
    public long indexGet() {
        return viewKeyComparator.indexGet(existingIndex);
    }

    @Benchmark
    public int visualizeKeyDistributionLength() {
        return viewKeyComparator.visualizeKeyDistribution(10).length();
    }
}

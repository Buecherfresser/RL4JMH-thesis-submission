package bench.generated.c095;

import com.carrotsearch.hppc.IntLongHashMap;
import com.carrotsearch.hppc.SortedIterationIntLongHashMap;
import com.carrotsearch.hppc.comparators.IntComparator;
import com.carrotsearch.hppc.comparators.IntLongComparator;
import com.carrotsearch.hppc.procedures.IntLongProcedure;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.predicates.IntLongPredicate;
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
public class SortedIterationIntLongHashMapBenchmark {

    private IntLongHashMap delegate;
    private SortedIterationIntLongHashMap sortedView;

    private IntComparator keyComparator;
    private IntLongComparator keyValueComparator;

    private int[] probeKeys;
    private int[] missingKeys;
    private long[] sumBox;

    private IntLongProcedure sumProcedure;
    private IntLongPredicate sumPredicate;
    private IntProcedure sumIntProcedure;
    private LongProcedure sumLongProcedure;

    private int probeIndex;
    private int missingIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(0x123456789ABCDEFL);
        int size = 10_000;

        // Generate unique keys
        int[] keys = new int[size];
        HashSet<Integer> seen = new HashSet<>();
        int added = 0;
        while (added < size) {
            int key = 1 + random.nextInt(Integer.MAX_VALUE - 1);
            if (seen.add(key)) {
                keys[added++] = key;
            }
        }

        // Populate delegate map
        delegate = new IntLongHashMap(size);
        for (int i = 0; i < size; i++) {
            long value = random.nextLong() & Long.MAX_VALUE;
            delegate.put(keys[i], value);
        }

        // Comparators
        keyComparator = (a, b) -> Integer.compare(a, b);
        keyValueComparator = (k1, v1, k2, v2) -> {
            int cmp = Integer.compare(k1, k2);
            return cmp != 0 ? cmp : Long.compare(v1, v2);
        };

        // Create sorted view
        sortedView = new SortedIterationIntLongHashMap(delegate, keyComparator);

        // Probe keys (existing)
        int probeCount = 1000;
        probeKeys = new int[probeCount];
        for (int i = 0; i < probeCount; i++) {
            probeKeys[i] = keys[random.nextInt(size)];
        }

        // Missing keys (not in map)
        missingKeys = new int[probeCount];
        int missingAdded = 0;
        while (missingAdded < probeCount) {
            int candidate = 1 + random.nextInt(Integer.MAX_VALUE - 1);
            if (!delegate.containsKey(candidate)) {
                missingKeys[missingAdded++] = candidate;
            }
        }

        // Accumulator for side effects
        sumBox = new long[1];

        // Procedures and predicates
        sumProcedure = (k, v) -> sumBox[0] += v;
        sumPredicate = (k, v) -> {
            sumBox[0] += v;
            return true;
        };
        sumIntProcedure = k -> sumBox[0] += k;
        sumLongProcedure = v -> sumBox[0] += v;

        probeIndex = 0;
        missingIndex = 0;
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        sumBox[0] = 0;
        sortedView.forEach(sumProcedure);
        bh.consume(sumBox[0]);
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        sumBox[0] = 0;
        sortedView.forEach(sumPredicate);
        bh.consume(sumBox[0]);
    }

    @Benchmark
    public void keysForEach(Blackhole bh) {
        sumBox[0] = 0;
        sortedView.keys().forEach(sumIntProcedure);
        bh.consume(sumBox[0]);
    }

    @Benchmark
    public void valuesForEach(Blackhole bh) {
        sumBox[0] = 0;
        sortedView.values().forEach(sumLongProcedure);
        bh.consume(sumBox[0]);
    }

    @Benchmark
    public void get(Blackhole bh) {
        int key = probeKeys[probeIndex++ % probeKeys.length];
        bh.consume(sortedView.get(key));
    }

    @Benchmark
    public void containsKey(Blackhole bh) {
        int key = probeKeys[probeIndex++ % probeKeys.length];
        bh.consume(sortedView.containsKey(key));
    }

    @Benchmark
    public void containsKeyMissing(Blackhole bh) {
        int key = missingKeys[missingIndex++ % missingKeys.length];
        bh.consume(sortedView.containsKey(key));
    }
}

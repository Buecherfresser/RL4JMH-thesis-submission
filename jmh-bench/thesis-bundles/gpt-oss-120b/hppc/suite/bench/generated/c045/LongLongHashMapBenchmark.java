package bench.generated.c045;

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
import com.carrotsearch.hppc.LongLongHashMap;
import com.carrotsearch.hppc.cursors.LongLongCursor;
import com.carrotsearch.hppc.procedures.LongLongProcedure;
import com.carrotsearch.hppc.predicates.LongLongPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongLongHashMapBenchmark {
    private int mapSize;
    private LongLongHashMap baseMap;
    private LongLongHashMap mutableMap;
    private long[] keys;
    private long[] values;
    private long missingKey;

    @Setup(Level.Trial)
    public void trialSetup() {
        mapSize = 1024;
        Random rnd = new Random(12345L);
        baseMap = new LongLongHashMap(mapSize);
        keys = new long[mapSize];
        values = new long[mapSize];
        for (int i = 0; i < mapSize; i++) {
            long k = rnd.nextLong();
            long v = rnd.nextLong();
            keys[i] = k;
            values[i] = v;
            baseMap.put(k, v);
        }
        missingKey = Long.MAX_VALUE;
        while (baseMap.containsKey(missingKey)) {
            missingKey = rnd.nextLong();
        }
    }

    @Setup(Level.Invocation)
    public void invocationSetup() {
        // Clone the base map for mutating benchmarks to avoid state bleed‑over.
        mutableMap = baseMap.clone();
    }

    @Benchmark
    public long getExisting() {
        return baseMap.get(keys[0]);
    }

    @Benchmark
    public long getOrDefaultMissing() {
        return baseMap.getOrDefault(missingKey, -1L);
    }

    @Benchmark
    public boolean containsExisting() {
        return baseMap.containsKey(keys[0]);
    }

    @Benchmark
    public boolean containsMissing() {
        return baseMap.containsKey(missingKey);
    }

    @Benchmark
    public long putUpdate() {
        // update the value for an existing key
        return mutableMap.put(keys[0], values[0] + 1L);
    }

    @Benchmark
    public long putOrAddExisting() {
        // key exists, increments value
        return mutableMap.putOrAdd(keys[0], 0L, 1L);
    }

    @Benchmark
    public long addToExisting() {
        return mutableMap.addTo(keys[0], 1L);
    }

    @Benchmark
    public int indexOfExisting() {
        return baseMap.indexOf(keys[0]);
    }

    @Benchmark
    public boolean indexExistsExisting() {
        int idx = baseMap.indexOf(keys[0]);
        return baseMap.indexExists(idx);
    }

    @Benchmark
    public long indexGetExisting() {
        int idx = baseMap.indexOf(keys[0]);
        return baseMap.indexGet(idx);
    }

    @Benchmark
    public long indexReplaceExisting() {
        int idx = baseMap.indexOf(keys[0]);
        return baseMap.indexReplace(idx, values[0] + 2L);
    }

    @Benchmark
    public long iterateSum() {
        long sum = 0L;
        for (LongLongCursor c : baseMap) {
            sum += c.key + c.value;
        }
        return sum;
    }

    @Benchmark
    public long forEachSum() {
        SumProcedure proc = new SumProcedure();
        baseMap.forEach(proc);
        return proc.sum;
    }

    @Benchmark
    public long forEachPredicateSum() {
        SumPredicate pred = new SumPredicate();
        baseMap.forEach(pred);
        return pred.sum;
    }

    @Benchmark
    public LongLongHashMap cloneMap() {
        return baseMap.clone();
    }

    @Benchmark
    public int size() {
        return baseMap.size();
    }

    @Benchmark
    public long ramBytesUsed() {
        return baseMap.ramBytesUsed();
    }

    // Helper classes for forEach benchmarks
    private static class SumProcedure implements LongLongProcedure {
        long sum = 0L;
        @Override
        public void apply(long key, long value) {
            sum += key + value;
        }
    }

    private static class SumPredicate implements LongLongPredicate {
        long sum = 0L;
        @Override
        public boolean apply(long key, long value) {
            sum += key + value;
            return true;
        }
    }
}

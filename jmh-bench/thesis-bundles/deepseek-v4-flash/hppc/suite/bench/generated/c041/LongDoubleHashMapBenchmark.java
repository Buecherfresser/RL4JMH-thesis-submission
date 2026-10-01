package bench.generated.c041;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.LongDoubleHashMap;
import com.carrotsearch.hppc.procedures.LongDoubleProcedure;
import com.carrotsearch.hppc.predicates.LongDoublePredicate;
import com.carrotsearch.hppc.predicates.LongPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongDoubleHashMapBenchmark {

    private LongDoubleHashMap map;
    private long[] keys;
    private double[] values;
    private long[] missingKeys;
    private int[] indices;
    private int[] missingIndices;
    private LongDoubleProcedure sumProcedure;
    private LongDoublePredicate countingPredicate;
    private LongDoublePredicate earlyExitPredicate;
    private LongPredicate earlyExitKeyPredicate;
    private double sum;
    private int predicateCount;
    private int earlyExitCount;
    private int size;
    private int nextIndex;

    @Setup(Level.Trial)
    public void setup() {
        Random random = new Random(0x12345678L);
        size = 1024;
        map = new LongDoubleHashMap(size);
        keys = new long[size];
        values = new double[size];

        for (int i = 0; i < size; i++) {
            long key;
            if (i == 0) {
                key = 0L;
            } else {
                do {
                    key = random.nextLong() & Long.MAX_VALUE;
                } while (key == 0L || map.containsKey(key));
            }
            double value = random.nextDouble();
            keys[i] = key;
            values[i] = value;
            map.put(key, value);
        }

        missingKeys = new long[size];
        missingIndices = new int[size];
        for (int i = 0; i < size; i++) {
            long missing;
            do {
                missing = random.nextLong() & Long.MAX_VALUE;
            } while (missing == 0L || map.containsKey(missing));
            missingKeys[i] = missing;
            missingIndices[i] = map.indexOf(missing);
        }

        indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = map.indexOf(keys[i]);
        }

        sumProcedure = (k, v) -> sum += v;
        countingPredicate = (k, v) -> {
            predicateCount++;
            return true;
        };
        earlyExitPredicate = (k, v) -> {
            earlyExitCount++;
            return false;
        };
        earlyExitKeyPredicate = k -> {
            earlyExitCount++;
            return false;
        };
        nextIndex = 0;
    }

    private int next() {
        int i = nextIndex;
        nextIndex = (nextIndex + 1) & (size - 1);
        return i;
    }

    @Benchmark
    public double putUpdate() {
        int i = next();
        return map.put(keys[i], values[i] + 1.0);
    }

    @Benchmark
    public double get() {
        return map.get(keys[next()]);
    }

    @Benchmark
    public double getOrDefaultExisting() {
        return map.getOrDefault(keys[next()], 0d);
    }

    @Benchmark
    public double getOrDefaultMissing() {
        return map.getOrDefault(missingKeys[next()], 42d);
    }

    @Benchmark
    public boolean containsKeyExisting() {
        return map.containsKey(keys[next()]);
    }

    @Benchmark
    public boolean containsKeyMissing() {
        return map.containsKey(missingKeys[next()]);
    }

    @Benchmark
    public double putOrAddExisting() {
        return map.putOrAdd(keys[next()], 1d, 1d);
    }

    @Benchmark
    public double addToExisting() {
        return map.addTo(keys[next()], 1d);
    }

    @Benchmark
    public int indexOfExisting() {
        return map.indexOf(keys[next()]);
    }

    @Benchmark
    public int indexOfMissing() {
        return map.indexOf(missingKeys[next()]);
    }

    @Benchmark
    public void forEachProcedure(Blackhole bh) {
        sum = 0;
        map.forEach(sumProcedure);
        bh.consume(sum);
    }

    @Benchmark
    public void forEachPredicate(Blackhole bh) {
        predicateCount = 0;
        map.forEach(countingPredicate);
        bh.consume(predicateCount);
    }

    @Benchmark
    public void forEachPredicateEarlyExit(Blackhole bh) {
        earlyExitCount = 0;
        map.forEach(earlyExitPredicate);
        bh.consume(earlyExitCount);
    }

    @Benchmark
    public void forEachKeysPredicateEarlyExit(Blackhole bh) {
        earlyExitCount = 0;
        map.keys().forEach(earlyExitKeyPredicate);
        bh.consume(earlyExitCount);
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
    public double indexGet() {
        return map.indexGet(indices[next()]);
    }

    @Benchmark
    public boolean indexExists() {
        return map.indexExists(indices[next()]);
    }

    @Benchmark
    public double indexReplace() {
        int idx = indices[next()];
        return map.indexReplace(idx, values[idx] + 1.0);
    }

    @Benchmark
    public int hashCodeBench() {
        return map.hashCode();
    }

    @Benchmark
    public boolean equalsBench() {
        return map.equals(map.clone());
    }

    @Benchmark
    public String toStringBench() {
        return map.toString();
    }

    @Benchmark
    public long ramBytesAllocated() {
        return map.ramBytesAllocated();
    }

    @Benchmark
    public long ramBytesUsed() {
        return map.ramBytesUsed();
    }
}

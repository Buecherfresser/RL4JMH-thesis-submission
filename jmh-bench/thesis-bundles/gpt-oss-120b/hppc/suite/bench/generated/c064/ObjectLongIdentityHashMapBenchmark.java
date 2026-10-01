package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectLongIdentityHashMap;
import com.carrotsearch.hppc.cursors.ObjectLongCursor;
import com.carrotsearch.hppc.procedures.ObjectLongProcedure;
import com.carrotsearch.hppc.predicates.ObjectLongPredicate;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectLongIdentityHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ObjectLongIdentityHashMap<Object> map;
        Object[] keys;
        long[] values;
        int size = 1024;
        Random rand = new Random(0);

        @Setup(Level.Trial)
        public void setUp() {
            keys = new Object[size];
            values = new long[size];
            for (int i = 0; i < size; i++) {
                keys[i] = new Object();
                values[i] = rand.nextLong();
            }
            map = new ObjectLongIdentityHashMap<>(size);
            for (int i = 0; i < size; i++) {
                map.put(keys[i], values[i]);
            }
        }

        @Setup(Level.Invocation)
        public void reset() {
            map.clear();
            for (int i = 0; i < size; i++) {
                map.put(keys[i], values[i]);
            }
        }
    }

    @Benchmark
    public long benchmarkPut(BenchmarkState s) {
        Object newKey = new Object();
        long newValue = s.rand.nextLong();
        return s.map.put(newKey, newValue);
    }

    @Benchmark
    public long benchmarkGet(BenchmarkState s) {
        int idx = s.rand.nextInt(s.size);
        return s.map.get(s.keys[idx]);
    }

    @Benchmark
    public long benchmarkGetOrDefault(BenchmarkState s) {
        int idx = s.rand.nextInt(s.size);
        return s.map.getOrDefault(s.keys[idx], -1L);
    }

    @Benchmark
    public boolean benchmarkContainsKey(BenchmarkState s) {
        int idx = s.rand.nextInt(s.size);
        return s.map.containsKey(s.keys[idx]);
    }

    @Benchmark
    public long benchmarkRemove(BenchmarkState s) {
        int idx = s.rand.nextInt(s.size);
        return s.map.remove(s.keys[idx]);
    }

    @Benchmark
    public long benchmarkPutOrAdd(BenchmarkState s) {
        int idx = s.rand.nextInt(s.size);
        long putVal = s.rand.nextLong();
        long incVal = s.rand.nextLong();
        return s.map.putOrAdd(s.keys[idx], putVal, incVal);
    }

    @Benchmark
    public long benchmarkAddTo(BenchmarkState s) {
        int idx = s.rand.nextInt(s.size);
        long add = s.rand.nextLong();
        return s.map.addTo(s.keys[idx], add);
    }

    @Benchmark
    public int benchmarkIndexOf(BenchmarkState s) {
        int idx = s.rand.nextInt(s.size);
        return s.map.indexOf(s.keys[idx]);
    }

    @Benchmark
    public boolean benchmarkIndexExists(BenchmarkState s) {
        int index = s.map.indexOf(s.keys[0]);
        return s.map.indexExists(index);
    }

    @Benchmark
    public long benchmarkIndexGet(BenchmarkState s) {
        int index = s.map.indexOf(s.keys[0]);
        return s.map.indexGet(index);
    }

    @Benchmark
    public void benchmarkIndexInsert(BenchmarkState s) {
        int index = s.map.indexOf(s.keys[0]);
        Object newKey = new Object();
        long newVal = s.rand.nextLong();
        s.map.indexInsert(index, newKey, newVal);
    }

    @Benchmark
    public long benchmarkIndexReplace(BenchmarkState s) {
        int index = s.map.indexOf(s.keys[0]);
        long newVal = s.rand.nextLong();
        return s.map.indexReplace(index, newVal);
    }

    @Benchmark
    public int benchmarkClear(BenchmarkState s) {
        s.map.clear();
        return s.map.size();
    }

    @Benchmark
    public int benchmarkRelease(BenchmarkState s) {
        s.map.release();
        return s.map.size();
    }

    @Benchmark
    public int benchmarkEnsureCapacity(BenchmarkState s) {
        s.map.ensureCapacity(s.size * 2);
        return s.map.size();
    }

    @Benchmark
    public int benchmarkSize(BenchmarkState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty(BenchmarkState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public long benchmarkRamBytesUsed(BenchmarkState s) {
        return s.map.ramBytesUsed();
    }

    @Benchmark
    public long benchmarkIterationForEach(BenchmarkState s) {
        long sum = 0L;
        for (ObjectLongCursor<Object> c : s.map) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public long benchmarkForEachProcedure(BenchmarkState s) {
        final long[] holder = new long[1];
        s.map.forEach(new ObjectLongProcedure<Object>() {
            @Override
            public void apply(Object key, long value) {
                holder[0] += value;
            }
        });
        return holder[0];
    }

    @Benchmark
    public long benchmarkForEachPredicate(BenchmarkState s) {
        final long[] holder = new long[1];
        s.map.forEach(new ObjectLongPredicate<Object>() {
            @Override
            public boolean apply(Object key, long value) {
                holder[0] += value;
                return true; // continue
            }
        });
        return holder[0];
    }

    @Benchmark
    public int benchmarkKeysSize(BenchmarkState s) {
        return s.map.keys().size();
    }

    @Benchmark
    public int benchmarkValuesSize(BenchmarkState s) {
        return s.map.values().size();
    }
}

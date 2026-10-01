package bench.generated.c047;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.LongShortHashMap;
import com.carrotsearch.hppc.cursors.LongShortCursor;
import com.carrotsearch.hppc.procedures.LongShortProcedure;
import com.carrotsearch.hppc.predicates.LongShortPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongShortHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class FilledMapState {
        LongShortHashMap map;
        long[] keys;
        short[] values;
        int size = 1024;
        int index = 0;

        @Setup(Level.Trial)
        public void setUp() {
            Random rnd = new Random(12345L);
            keys = new long[size];
            values = new short[size];
            for (int i = 0; i < size; i++) {
                long k = rnd.nextLong();
                if (k == 0L) {
                    k = 1L;
                }
                keys[i] = k;
                values[i] = (short) rnd.nextInt(Short.MAX_VALUE + 1);
            }
            map = new LongShortHashMap(size);
            for (int i = 0; i < size; i++) {
                map.put(keys[i], values[i]);
            }
        }
    }

    @State(Scope.Benchmark)
    public static class EmptyMapState {
        LongShortHashMap map;
        long[] keys;
        short[] values;
        int size = 1024;
        int index = 0;

        @Setup(Level.Trial)
        public void setUp() {
            Random rnd = new Random(54321L);
            keys = new long[size];
            values = new short[size];
            for (int i = 0; i < size; i++) {
                long k = rnd.nextLong();
                if (k == 0L) {
                    k = 1L;
                }
                keys[i] = k;
                values[i] = (short) rnd.nextInt(Short.MAX_VALUE + 1);
            }
            map = new LongShortHashMap(size);
        }
    }

    @Benchmark
    public short benchmarkPut(EmptyMapState s) {
        int i = s.index++;
        if (s.index >= s.keys.length) {
            s.index = 0;
        }
        return s.map.put(s.keys[i], s.values[i]);
    }

    @Benchmark
    public short benchmarkGetExisting(FilledMapState s) {
        return s.map.get(s.keys[0]);
    }

    @Benchmark
    public short benchmarkGetMissing(FilledMapState s) {
        return s.map.get(Long.MAX_VALUE);
    }

    @Benchmark
    public boolean benchmarkContainsKeyExisting(FilledMapState s) {
        return s.map.containsKey(s.keys[0]);
    }

    @Benchmark
    public short benchmarkRemove(EmptyMapState s) {
        s.map.put(s.keys[0], s.values[0]);
        return s.map.remove(s.keys[0]);
    }

    @Benchmark
    public short benchmarkPutOrAddExisting(FilledMapState s) {
        return s.map.putOrAdd(s.keys[0], (short) 1, (short) 1);
    }

    @Benchmark
    public short benchmarkAddToExisting(FilledMapState s) {
        return s.map.addTo(s.keys[0], (short) 1);
    }

    @Benchmark
    public int benchmarkIndexOfExisting(FilledMapState s) {
        return s.map.indexOf(s.keys[0]);
    }

    @Benchmark
    public short benchmarkIndexGet(FilledMapState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexGet(idx);
    }

    @Benchmark
    public short benchmarkIndexReplace(FilledMapState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexReplace(idx, (short) 123);
    }

    @Benchmark
    public short benchmarkIndexInsert(FilledMapState s) {
        long newKey = Long.MAX_VALUE;
        int idx = s.map.indexOf(newKey);
        s.map.indexInsert(idx, newKey, (short) 5);
        return s.map.get(newKey);
    }

    @Benchmark
    public short benchmarkIndexRemove(FilledMapState s) {
        int idx = s.map.indexOf(s.keys[0]);
        return s.map.indexRemove(idx);
    }

    @Benchmark
    public int benchmarkClear(FilledMapState s) {
        LongShortHashMap copy = s.map.clone();
        copy.clear();
        return copy.size();
    }

    @Benchmark
    public int benchmarkSize(FilledMapState s) {
        return s.map.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty(FilledMapState s) {
        return s.map.isEmpty();
    }

    @Benchmark
    public int benchmarkForEachProcedure(FilledMapState s) {
        s.map.forEach((LongShortProcedure) (k, v) -> {
        });
        return s.map.size();
    }

    @Benchmark
    public int benchmarkForEachPredicate(FilledMapState s) {
        s.map.forEach((LongShortPredicate) (k, v) -> true);
        return s.map.size();
    }

    @Benchmark
    public int benchmarkIterate(FilledMapState s, Blackhole bh) {
        int sum = 0;
        for (LongShortCursor c : s.map) {
            sum += c.key;
        }
        bh.consume(sum);
        return sum;
    }

    @Benchmark
    public boolean benchmarkKeysContains(FilledMapState s) {
        return s.map.keys().contains(s.keys[0]);
    }

    @Benchmark
    public boolean benchmarkValuesContains(FilledMapState s) {
        return s.map.values().contains(s.values[0]);
    }
}

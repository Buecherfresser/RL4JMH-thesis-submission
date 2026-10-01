package bench.generated.c114;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.SortedIterationShortByteHashMap;
import com.carrotsearch.hppc.ShortByteHashMap;
import com.carrotsearch.hppc.cursors.ShortByteCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.cursors.ByteCursor;
import com.carrotsearch.hppc.procedures.ShortByteProcedure;
import com.carrotsearch.hppc.predicates.ShortBytePredicate;
import com.carrotsearch.hppc.predicates.ShortPredicate;
import com.carrotsearch.hppc.comparators.ShortComparator;
import com.carrotsearch.hppc.comparators.ShortByteComparator;
import java.util.Random;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationShortByteHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        ShortByteHashMap delegate;
        SortedIterationShortByteHashMap byKey;
        SortedIterationShortByteHashMap byKeyValue;
        short existingKey;
        short missingKey;
        byte defaultValue;

        @Setup(Level.Trial)
        public void setUp() {
            int size = 1024;
            delegate = new ShortByteHashMap(size);
            Random rnd = new Random(12345L);
            for (int i = 0; i < size; i++) {
                short k = (short) rnd.nextInt(Short.MAX_VALUE + 1);
                byte v = (byte) rnd.nextInt(256);
                delegate.put(k, v);
                if (i == 0) {
                    existingKey = k;
                }
            }
            // ensure missingKey is not present
            missingKey = (short) (existingKey + 1);
            while (delegate.containsKey(missingKey)) {
                missingKey++;
            }
            defaultValue = (byte) 0;

            ShortComparator keyComparator = (a, b) -> Short.compare(a, b);
            ShortByteComparator keyValueComparator = (ka, va, kb, vb) -> {
                int cmp = Short.compare(ka, kb);
                if (cmp != 0) return cmp;
                return Byte.compare(va, vb);
            };
            byKey = new SortedIterationShortByteHashMap(delegate, keyComparator);
            byKeyValue = new SortedIterationShortByteHashMap(delegate, keyValueComparator);
        }
    }

    @Benchmark
    public int benchmarkSize(BenchmarkState s) {
        return s.byKey.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty(BenchmarkState s) {
        return s.byKey.isEmpty();
    }

    @Benchmark
    public boolean benchmarkContainsKey(BenchmarkState s) {
        return s.byKey.containsKey(s.existingKey);
    }

    @Benchmark
    public byte benchmarkGet(BenchmarkState s) {
        return s.byKey.get(s.existingKey);
    }

    @Benchmark
    public byte benchmarkGetOrDefault(BenchmarkState s) {
        return s.byKey.getOrDefault(s.missingKey, s.defaultValue);
    }

    @Benchmark
    public int benchmarkIndexOf(BenchmarkState s) {
        return s.byKey.indexOf(s.existingKey);
    }

    @Benchmark
    public boolean benchmarkIndexExists(BenchmarkState s) {
        int idx = s.byKey.indexOf(s.existingKey);
        return s.byKey.indexExists(idx);
    }

    @Benchmark
    public byte benchmarkIndexGet(BenchmarkState s) {
        int idx = s.byKey.indexOf(s.existingKey);
        return s.byKey.indexGet(idx);
    }

    @Benchmark
    public String benchmarkVisualizeKeyDistribution(BenchmarkState s) {
        return s.byKey.visualizeKeyDistribution(10);
    }

    @Benchmark
    public int benchmarkForEachProcedure(BenchmarkState s) {
        final int[] sum = new int[1];
        s.byKey.forEach((ShortByteProcedure) (k, v) -> sum[0] += v);
        return sum[0];
    }

    @Benchmark
    public int benchmarkForEachPredicate(BenchmarkState s) {
        final int[] count = new int[1];
        s.byKey.forEach((ShortBytePredicate) (k, v) -> {
            count[0]++;
            return count[0] < 10; // stop after 10 elements
        });
        return count[0];
    }

    @Benchmark
    public int benchmarkIterateEntries(BenchmarkState s) {
        int cnt = 0;
        for (ShortByteCursor c : s.byKey) {
            cnt++;
        }
        return cnt;
    }

    @Benchmark
    public int benchmarkIterateKeys(BenchmarkState s) {
        int cnt = 0;
        for (ShortCursor c : s.byKey.keys()) {
            cnt++;
        }
        return cnt;
    }

    @Benchmark
    public int benchmarkIterateValues(BenchmarkState s) {
        int cnt = 0;
        for (ByteCursor c : s.byKey.values()) {
            cnt++;
        }
        return cnt;
    }

    @Benchmark
    public void benchmarkIteratorConsume(BenchmarkState s, Blackhole bh) {
        Iterator<ShortByteCursor> it = s.byKey.iterator();
        while (it.hasNext()) {
            ShortByteCursor c = it.next();
            bh.consume(c.key);
            bh.consume(c.value);
        }
    }
}

package bench.generated.c105;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import com.carrotsearch.hppc.LongShortHashMap;
import com.carrotsearch.hppc.SortedIterationLongShortHashMap;
import com.carrotsearch.hppc.comparators.LongComparator;
import com.carrotsearch.hppc.comparators.LongShortComparator;
import com.carrotsearch.hppc.cursors.LongShortCursor;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.procedures.LongShortProcedure;
import com.carrotsearch.hppc.predicates.LongShortPredicate;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.procedures.ShortProcedure;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SortedIterationLongShortHashMapBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        LongShortHashMap delegate;
        SortedIterationLongShortHashMap viewByKey;
        SortedIterationLongShortHashMap viewByKeyValue;
        long existingKey;
        short existingValue;
        int existingIndex;
        int size = 1000;

        LongComparator keyComparator = (a, b) -> Long.compare(a, b);
        LongShortComparator keyValueComparator = (a, b, c, d) -> {
            int cmp = Long.compare(a, c);
            if (cmp == 0) cmp = Short.compare(b, d);
            return cmp;
        };

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(0x12345678L);
            delegate = new LongShortHashMap(size * 2);
            for (int i = 0; i < size; i++) {
                long key = rnd.nextLong();
                short value = (short) rnd.nextInt();
                delegate.put(key, value);
            }
            for (LongShortCursor c : delegate) {
                existingKey = c.key;
                existingValue = c.value;
                break;
            }
            existingIndex = delegate.indexOf(existingKey);
            viewByKey = new SortedIterationLongShortHashMap(delegate, keyComparator);
            viewByKeyValue = new SortedIterationLongShortHashMap(delegate, keyValueComparator);
        }
    }

    @Benchmark
    public SortedIterationLongShortHashMap constructByKey(BenchmarkState state) {
        return new SortedIterationLongShortHashMap(state.delegate, state.keyComparator);
    }

    @Benchmark
    public SortedIterationLongShortHashMap constructByKeyValue(BenchmarkState state) {
        return new SortedIterationLongShortHashMap(state.delegate, state.keyValueComparator);
    }

    @Benchmark
    public short get(BenchmarkState state) {
        return state.viewByKey.get(state.existingKey);
    }

    @Benchmark
    public short getOrDefault(BenchmarkState state) {
        return state.viewByKey.getOrDefault(state.existingKey, (short) 0);
    }

    @Benchmark
    public boolean containsKey(BenchmarkState state) {
        return state.viewByKey.containsKey(state.existingKey);
    }

    @Benchmark
    public int size(BenchmarkState state) {
        return state.viewByKey.size();
    }

    @Benchmark
    public boolean isEmpty(BenchmarkState state) {
        return state.viewByKey.isEmpty();
    }

    @Benchmark
    public LongShortProcedure forEachProcedure(BenchmarkState state) {
        return state.viewByKey.forEach((LongShortProcedure) (k, v) -> {});
    }

    @Benchmark
    public LongShortPredicate forEachPredicate(BenchmarkState state) {
        return state.viewByKey.forEach((LongShortPredicate) (k, v) -> true);
    }

    @Benchmark
    public long iterate(BenchmarkState state) {
        long sum = 0;
        for (LongShortCursor c : state.viewByKey) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public boolean keysContains(BenchmarkState state) {
        return state.viewByKey.keys().contains(state.existingKey);
    }

    @Benchmark
    public LongProcedure keysForEach(BenchmarkState state) {
        return state.viewByKey.keys().forEach((LongProcedure) (k) -> {});
    }

    @Benchmark
    public long keysIterate(BenchmarkState state) {
        long sum = 0;
        for (LongCursor c : state.viewByKey.keys()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public boolean valuesContains(BenchmarkState state) {
        return state.viewByKey.values().contains(state.existingValue);
    }

    @Benchmark
    public ShortProcedure valuesForEach(BenchmarkState state) {
        return state.viewByKey.values().forEach((ShortProcedure) (v) -> {});
    }

    @Benchmark
    public long valuesIterate(BenchmarkState state) {
        long sum = 0;
        for (ShortCursor c : state.viewByKey.values()) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public int indexOf(BenchmarkState state) {
        return state.viewByKey.indexOf(state.existingKey);
    }

    @Benchmark
    public boolean indexExists(BenchmarkState state) {
        return state.viewByKey.indexExists(state.existingIndex);
    }

    @Benchmark
    public short indexGet(BenchmarkState state) {
        return state.viewByKey.indexGet(state.existingIndex);
    }

    @Benchmark
    public String visualizeKeyDistribution(BenchmarkState state) {
        return state.viewByKey.visualizeKeyDistribution(100);
    }
}

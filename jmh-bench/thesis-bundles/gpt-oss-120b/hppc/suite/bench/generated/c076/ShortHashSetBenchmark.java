package bench.generated.c076;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.ShortHashSet;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.predicates.ShortPredicate;
import com.carrotsearch.hppc.procedures.ShortProcedure;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortHashSetBenchmark {
    private ShortHashSet set;
    private short[] elements;
    private short existingKey;
    private short nonExistingKey;
    private ShortPredicate evenPredicate;
    private ShortProcedure noopProcedure;
    private Random random;

    @Setup(Level.Trial)
    public void setup() {
        random = new Random(12345L);
        int size = 1024;
        elements = new short[size];
        for (int i = 0; i < size; i++) {
            short v;
            do {
                v = (short) (random.nextInt(Short.MAX_VALUE - 1) + 1); // avoid zero
            } while (containsInArray(v, elements, i));
            elements[i] = v;
        }
        set = new ShortHashSet();
        set.addAll(elements);
        existingKey = elements[0];
        short candidate = (short) (existingKey + 1);
        while (candidate == 0 || set.contains(candidate)) {
            candidate++;
        }
        nonExistingKey = candidate;
        evenPredicate = new ShortPredicate() {
            @Override
            public boolean apply(short value) {
                return (value & 1) == 0;
            }
        };
        noopProcedure = new ShortProcedure() {
            @Override
            public void apply(short value) {
                // no-op
            }
        };
    }

    private boolean containsInArray(short v, short[] arr, int upTo) {
        for (int i = 0; i < upTo; i++) {
            if (arr[i] == v) return true;
        }
        return false;
    }

    @Benchmark
    public boolean benchmarkAdd() {
        ShortHashSet s = set.clone();
        return s.add(nonExistingKey);
    }

    @Benchmark
    public int benchmarkAddAllVarargs() {
        ShortHashSet s = set.clone();
        return s.addAll(new short[]{nonExistingKey});
    }

    @Benchmark
    public boolean benchmarkContainsExisting() {
        return set.contains(existingKey);
    }

    @Benchmark
    public boolean benchmarkContainsNonExisting() {
        return set.contains(nonExistingKey);
    }

    @Benchmark
    public boolean benchmarkRemoveExisting() {
        ShortHashSet s = set.clone();
        return s.remove(existingKey);
    }

    @Benchmark
    public boolean benchmarkRemoveNonExisting() {
        ShortHashSet s = set.clone();
        return s.remove(nonExistingKey);
    }

    @Benchmark
    public int benchmarkRemoveAllKey() {
        ShortHashSet s = set.clone();
        return s.removeAll(existingKey);
    }

    @Benchmark
    public int benchmarkRemoveAllPredicate() {
        ShortHashSet s = set.clone();
        return s.removeAll(evenPredicate);
    }

    @Benchmark
    public int benchmarkIndexOfExisting() {
        return set.indexOf(existingKey);
    }

    @Benchmark
    public boolean benchmarkIndexExistsTrue() {
        int idx = set.indexOf(existingKey);
        return set.indexExists(idx);
    }

    @Benchmark
    public short benchmarkIndexGet() {
        int idx = set.indexOf(existingKey);
        return set.indexGet(idx);
    }

    @Benchmark
    public int benchmarkIndexInsert() {
        int idx = set.indexOf(nonExistingKey); // negative
        ShortHashSet s = set.clone();
        s.indexInsert(idx, nonExistingKey);
        return s.size();
    }

    @Benchmark
    public int benchmarkIndexRemove() {
        int idx = set.indexOf(existingKey);
        ShortHashSet s = set.clone();
        s.indexRemove(idx);
        return s.size();
    }

    @Benchmark
    public boolean benchmarkClear() {
        ShortHashSet s = set.clone();
        s.clear();
        return s.isEmpty();
    }

    @Benchmark
    public int benchmarkEnsureCapacity() {
        ShortHashSet s = set.clone();
        s.ensureCapacity(s.size() + 1000);
        return s.size();
    }

    @Benchmark
    public int benchmarkToArray() {
        short[] arr = set.toArray();
        return arr.length;
    }

    @Benchmark
    public ShortProcedure benchmarkForEachProcedure() {
        return set.forEach(noopProcedure);
    }

    @Benchmark
    public ShortPredicate benchmarkForEachPredicate() {
        return set.forEach(evenPredicate);
    }

    @Benchmark
    public int benchmarkClone() {
        ShortHashSet s = set.clone();
        return s.size();
    }

    @Benchmark
    public int benchmarkIterator(Blackhole bh) {
        int count = 0;
        for (ShortCursor c : set) {
            bh.consume(c.value);
            count++;
        }
        return count;
    }

    @Benchmark
    public int benchmarkSize() {
        return set.size();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return set.hashCode();
    }

    @Benchmark
    public boolean benchmarkEqualsSelf() {
        return set.equals(set);
    }
}

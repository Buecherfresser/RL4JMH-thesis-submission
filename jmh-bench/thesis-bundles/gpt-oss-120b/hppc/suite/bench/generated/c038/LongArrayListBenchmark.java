package bench.generated.c038;

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
import java.util.Iterator;
import com.carrotsearch.hppc.LongArrayList;
import com.carrotsearch.hppc.cursors.LongCursor;
import com.carrotsearch.hppc.procedures.LongProcedure;
import com.carrotsearch.hppc.predicates.LongPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongArrayListBenchmark {
    private static final int SIZE = 1024;
    private static final long SEED = 0x1234abcdL;

    private LongArrayList baseList;
    private long existingValue;
    private long nonExistingValue = Long.MAX_VALUE;

    @Setup(Level.Trial)
    public void setup() {
        Random rnd = new Random(SEED);
        baseList = new LongArrayList(SIZE);
        for (int i = 0; i < SIZE; i++) {
            baseList.add(rnd.nextLong());
        }
        existingValue = baseList.get(0);
    }

    private LongArrayList cloneBase() {
        return baseList.clone();
    }

    @Benchmark
    public int benchmarkAdd() {
        LongArrayList list = cloneBase();
        list.add(42L);
        return list.size();
    }

    @Benchmark
    public int benchmarkAddTwo() {
        LongArrayList list = cloneBase();
        list.add(1L, 2L);
        return list.size();
    }

    @Benchmark
    public int benchmarkInsert() {
        LongArrayList list = cloneBase();
        int idx = SIZE / 2;
        list.insert(idx, 99L);
        return (int) list.get(idx);
    }

    @Benchmark
    public long benchmarkGet() {
        int idx = SIZE / 2;
        return baseList.get(idx);
    }

    @Benchmark
    public long benchmarkSet() {
        LongArrayList list = cloneBase();
        int idx = SIZE / 2;
        return list.set(idx, 77L);
    }

    @Benchmark
    public long benchmarkRemoveAt() {
        LongArrayList list = cloneBase();
        int idx = SIZE / 2;
        return list.removeAt(idx);
    }

    @Benchmark
    public long benchmarkRemoveLast() {
        LongArrayList list = cloneBase();
        return list.removeLast();
    }

    @Benchmark
    public boolean benchmarkRemoveElement() {
        LongArrayList list = cloneBase();
        return list.removeElement(existingValue);
    }

    @Benchmark
    public int benchmarkRemoveAllValue() {
        LongArrayList list = cloneBase();
        return list.removeAll(existingValue);
    }

    @Benchmark
    public int benchmarkRemoveAllPredicate() {
        LongArrayList list = cloneBase();
        LongPredicate pred = v -> v == existingValue;
        return list.removeAll(pred);
    }

    @Benchmark
    public boolean benchmarkContains() {
        return baseList.contains(existingValue);
    }

    @Benchmark
    public int benchmarkIndexOf() {
        return baseList.indexOf(existingValue);
    }

    @Benchmark
    public int benchmarkLastIndexOf() {
        return baseList.lastIndexOf(existingValue);
    }

    @Benchmark
    public int benchmarkSize() {
        return baseList.size();
    }

    @Benchmark
    public boolean benchmarkIsEmpty() {
        return baseList.isEmpty();
    }

    @Benchmark
    public int benchmarkClear() {
        LongArrayList list = cloneBase();
        list.clear();
        return list.size();
    }

    @Benchmark
    public int benchmarkRelease() {
        LongArrayList list = cloneBase();
        list.release();
        return list.buffer.length;
    }

    @Benchmark
    public int benchmarkToArray() {
        return baseList.toArray().length;
    }

    @Benchmark
    public long benchmarkIterator() {
        long sum = 0;
        Iterator<LongCursor> it = baseList.iterator();
        while (it.hasNext()) {
            sum += it.next().value;
        }
        return sum;
    }

    private static class SumProcedure implements LongProcedure {
        long sum = 0;
        @Override
        public void apply(long value) {
            sum += value;
        }
    }

    @Benchmark
    public long benchmarkForEachProcedure() {
        SumProcedure proc = new SumProcedure();
        baseList.forEach(proc);
        return proc.sum;
    }

    @Benchmark
    public int benchmarkForEachPredicate() {
        baseList.forEach((LongPredicate) v -> true);
        return baseList.size();
    }

    @Benchmark
    public long benchmarkSort() {
        LongArrayList list = cloneBase();
        list.sort();
        return list.get(0);
    }

    @Benchmark
    public long benchmarkReverse() {
        LongArrayList list = cloneBase();
        list.reverse();
        return list.get(0);
    }

    @Benchmark
    public int benchmarkResize() {
        LongArrayList list = cloneBase();
        list.resize(SIZE * 2);
        return list.size();
    }

    @Benchmark
    public int benchmarkTrimToSize() {
        LongArrayList list = cloneBase();
        list.ensureCapacity(SIZE * 2);
        list.trimToSize();
        return list.buffer.length;
    }

    @Benchmark
    public long benchmarkRamBytesUsed() {
        return baseList.ramBytesUsed();
    }

    @Benchmark
    public int benchmarkClone() {
        LongArrayList cloned = baseList.clone();
        return cloned.size();
    }

    @Benchmark
    public long benchmarkStreamSum() {
        return baseList.stream().sum();
    }
}

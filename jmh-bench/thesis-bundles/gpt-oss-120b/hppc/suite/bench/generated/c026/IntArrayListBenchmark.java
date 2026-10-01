package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntArrayList;
import java.util.Random;
import java.util.Iterator;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.predicates.IntPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntArrayListBenchmark {

    @State(Scope.Benchmark)
    public static class ReadOnlyState {
        IntArrayList list;

        @Setup(Level.Trial)
        public void setUp() {
            list = new IntArrayList();
            Random rand = new Random(12345L);
            for (int i = 0; i < 1024; i++) {
                list.add(rand.nextInt());
            }
        }
    }

    @State(Scope.Benchmark)
    public static class MutableState {
        IntArrayList baseline;
        IntArrayList list;

        @Setup(Level.Trial)
        public void setUpTrial() {
            baseline = new IntArrayList();
            Random rand = new Random(12345L);
            for (int i = 0; i < 1024; i++) {
                baseline.add(rand.nextInt());
            }
        }

        @Setup(Level.Invocation)
        public void setUpInvocation() {
            list = baseline.clone();
        }
    }

    @Benchmark
    public int benchmarkAddSingle(MutableState s) {
        s.list.add(42);
        return s.list.size();
    }

    @Benchmark
    public int benchmarkAddTwo(MutableState s) {
        s.list.add(1, 2);
        return s.list.size();
    }

    @Benchmark
    public int benchmarkAddVarargs(MutableState s) {
        s.list.add(3, 4, 5);
        return s.list.size();
    }

    @Benchmark
    public int benchmarkAddAllFromArray(MutableState s) {
        int[] extra = new int[]{6, 7, 8};
        s.list.add(extra, 0, extra.length);
        return s.list.size();
    }

    @Benchmark
    public int benchmarkInsert(MutableState s) {
        s.list.insert(0, 99);
        return s.list.get(0);
    }

    @Benchmark
    public int benchmarkGet(ReadOnlyState s) {
        return s.list.get(0);
    }

    @Benchmark
    public int benchmarkSet(MutableState s) {
        return s.list.set(0, 123);
    }

    @Benchmark
    public int benchmarkRemoveAt(MutableState s) {
        return s.list.removeAt(0);
    }

    @Benchmark
    public int benchmarkRemoveLast(MutableState s) {
        return s.list.removeLast();
    }

    @Benchmark
    public int benchmarkRemoveRange(MutableState s) {
        s.list.removeRange(0, 10);
        return s.list.size();
    }

    @Benchmark
    public int benchmarkRemoveElement(MutableState s) {
        boolean removed = s.list.removeElement(s.list.get(0));
        return removed ? 1 : 0;
    }

    @Benchmark
    public int benchmarkRemoveFirstByValue(MutableState s) {
        int idx = s.list.removeFirst(s.list.get(0));
        return idx;
    }

    @Benchmark
    public int benchmarkRemoveLastByValue(MutableState s) {
        int idx = s.list.removeLast(s.list.get(s.list.size() - 1));
        return idx;
    }

    @Benchmark
    public int benchmarkRemoveAllByValue(MutableState s) {
        return s.list.removeAll(s.list.get(0));
    }

    @Benchmark
    public int benchmarkContains(ReadOnlyState s) {
        return s.list.contains(s.list.get(0)) ? 1 : 0;
    }

    @Benchmark
    public int benchmarkIndexOf(ReadOnlyState s) {
        return s.list.indexOf(s.list.get(0));
    }

    @Benchmark
    public int benchmarkLastIndexOf(ReadOnlyState s) {
        return s.list.lastIndexOf(s.list.get(s.list.size() - 1));
    }

    @Benchmark
    public int benchmarkEnsureCapacity(MutableState s) {
        s.list.ensureCapacity(2048);
        return s.list.buffer.length;
    }

    @Benchmark
    public int benchmarkResize(MutableState s) {
        s.list.resize(512);
        return s.list.size();
    }

    @Benchmark
    public int benchmarkTrimToSize(MutableState s) {
        s.list.trimToSize();
        return s.list.buffer.length;
    }

    @Benchmark
    public int benchmarkClear(MutableState s) {
        s.list.clear();
        return s.list.size();
    }

    @Benchmark
    public int benchmarkRelease(MutableState s) {
        s.list.release();
        return s.list.buffer.length;
    }

    @Benchmark
    public int benchmarkToArray(ReadOnlyState s) {
        int[] arr = s.list.toArray();
        return arr.length;
    }

    @Benchmark
    public int benchmarkSort(MutableState s) {
        s.list.sort();
        return s.list.get(0);
    }

    @Benchmark
    public int benchmarkReverse(MutableState s) {
        s.list.reverse();
        return s.list.get(0);
    }

    @Benchmark
    public int benchmarkClone(ReadOnlyState s) {
        IntArrayList clone = s.list.clone();
        return clone.size();
    }

    @Benchmark
    public int benchmarkHashCode(ReadOnlyState s) {
        return s.list.hashCode();
    }

    @Benchmark
    public int benchmarkEquals(ReadOnlyState s) {
        return s.list.equals(s.list) ? 1 : 0;
    }

    @Benchmark
    public long benchmarkRamBytesUsed(ReadOnlyState s) {
        return s.list.ramBytesUsed();
    }

    @Benchmark
    public void benchmarkIterator(ReadOnlyState s, Blackhole bh) {
        Iterator<IntCursor> it = s.list.iterator();
        while (it.hasNext()) {
            bh.consume(it.next().value);
        }
    }

    @Benchmark
    public void benchmarkForEachProcedure(MutableState s, Blackhole bh) {
        s.list.forEach((IntProcedure) v -> bh.consume(v));
    }

    @Benchmark
    public void benchmarkForEachPredicate(MutableState s, Blackhole bh) {
        s.list.forEach((IntPredicate) v -> {
            bh.consume(v);
            return true;
        });
    }
}

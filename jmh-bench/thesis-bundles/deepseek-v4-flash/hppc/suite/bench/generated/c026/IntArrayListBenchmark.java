package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;
import com.carrotsearch.hppc.procedures.IntProcedure;
import com.carrotsearch.hppc.predicates.IntPredicate;
import java.util.Random;
import java.util.ArrayList;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntArrayListBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        IntArrayList sharedList;          // read-only operations
        IntArrayList otherList;           // for equals / addAll
        IntArrayList[] pool;              // mutating operations pool
        int poolIndex;
        int[] randomValues;
        int[] addArray;                   // for add(int[], start, length)
        ArrayList<IntCursor> cursorIterable;  // for addAll(Iterable)

        @Setup(Level.Trial)
        public void setup() {
            Random rnd = new Random(12345);
            int size = 1000;
            sharedList = new IntArrayList(size);
            for (int i = 0; i < size; i++) {
                sharedList.add(rnd.nextInt(1000));
            }
            otherList = new IntArrayList(size);
            for (int i = 0; i < size; i++) {
                otherList.add(sharedList.get(i));
            }

            int poolSize = 1024;
            pool = new IntArrayList[poolSize];
            for (int i = 0; i < poolSize; i++) {
                IntArrayList list = new IntArrayList(100);
                for (int j = 0; j < 100; j++) {
                    list.add(rnd.nextInt(1000));
                }
                pool[i] = list;
            }
            poolIndex = 0;
            randomValues = new int[poolSize];
            for (int i = 0; i < poolSize; i++) {
                randomValues[i] = rnd.nextInt(1000);
            }
            addArray = new int[10];
            for (int i = 0; i < 10; i++) {
                addArray[i] = rnd.nextInt(1000);
            }

            // Build an iterable of IntCursor objects for addAll(Iterable)
            cursorIterable = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                IntCursor c = new IntCursor();
                c.value = rnd.nextInt(1000);
                cursorIterable.add(c);
            }
        }

        IntArrayList nextList() {
            return pool[poolIndex++ & (pool.length - 1)];
        }
    }

    // ========== Read-only operations (shared list) ==========

    @Benchmark
    public int get(BenchmarkState s) {
        int idx = s.randomValues[s.poolIndex & (s.pool.length - 1)] % 1000;
        return s.sharedList.get(idx);
    }

    @Benchmark
    public boolean contains(BenchmarkState s) {
        int val = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        return s.sharedList.contains(val);
    }

    @Benchmark
    public int indexOf(BenchmarkState s) {
        int val = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        return s.sharedList.indexOf(val);
    }

    @Benchmark
    public int lastIndexOf(BenchmarkState s) {
        int val = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        return s.sharedList.lastIndexOf(val);
    }

    @Benchmark
    public int[] toArray(BenchmarkState s) {
        return s.sharedList.toArray();
    }

    @Benchmark
    public int streamSum(BenchmarkState s) {
        return s.sharedList.stream().sum();
    }

    @Benchmark
    public int forEachProcedure(BenchmarkState s) {
        final int[] sum = {0};
        s.sharedList.forEach(new IntProcedure() {
            @Override
            public void apply(int value) {
                sum[0] += value;
            }
        });
        return sum[0];
    }

    @Benchmark
    public int forEachPredicate(BenchmarkState s) {
        final int[] count = {0};
        s.sharedList.forEach(new IntPredicate() {
            @Override
            public boolean apply(int value) {
                count[0]++;
                return true;
            }
        });
        return count[0];
    }

    @Benchmark
    public int iteratorSum(BenchmarkState s) {
        int sum = 0;
        for (IntCursor c : s.sharedList) {
            sum += c.value;
        }
        return sum;
    }

    @Benchmark
    public int hashCodeBench(BenchmarkState s) {
        return s.sharedList.hashCode();
    }

    @Benchmark
    public boolean equalsBench(BenchmarkState s) {
        return s.sharedList.equals(s.otherList);
    }

    @Benchmark
    public IntArrayList cloneBench(BenchmarkState s) {
        return s.sharedList.clone();
    }

    // ========== Mutating operations (pool of lists) ==========

    @Benchmark
    public int addSingle(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int val = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        list.add(val);
        return list.size();
    }

    @Benchmark
    public int addPair(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int v1 = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        int v2 = s.randomValues[(s.poolIndex + 1) & (s.pool.length - 1)];
        list.add(v1, v2);
        return list.size();
    }

    @Benchmark
    public int addArrayRange(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.add(s.addArray, 0, s.addArray.length);
        return list.size();
    }

    @Benchmark
    public int addVarargs(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.add(s.addArray);
        return list.size();
    }

    @Benchmark
    public int addAllContainer(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.addAll(s.otherList);
        return list.size();
    }

    @Benchmark
    public int addAllIterable(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.addAll(s.cursorIterable);
        return list.size();
    }

    @Benchmark
    public int insert(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int idx = s.randomValues[s.poolIndex & (s.pool.length - 1)] % list.size();
        int val = s.randomValues[(s.poolIndex + 1) & (s.pool.length - 1)];
        list.insert(idx, val);
        return list.size();
    }

    @Benchmark
    public int set(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int idx = s.randomValues[s.poolIndex & (s.pool.length - 1)] % list.size();
        int val = s.randomValues[(s.poolIndex + 1) & (s.pool.length - 1)];
        return list.set(idx, val);
    }

    @Benchmark
    public int removeAt(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int idx = s.randomValues[s.poolIndex & (s.pool.length - 1)] % list.size();
        return list.removeAt(idx);
    }

    @Benchmark
    public int removeLast(BenchmarkState s) {
        IntArrayList list = s.nextList();
        return list.removeLast();
    }

    @Benchmark
    public void removeRange(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int from = 10;
        int to = 20;
        list.removeRange(from, to);
    }

    @Benchmark
    public boolean removeElement(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int val = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        return list.removeElement(val);
    }

    @Benchmark
    public int removeFirst(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int val = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        return list.removeFirst(val);
    }

    @Benchmark
    public int removeLastValue(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int val = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        return list.removeLast(val);
    }

    @Benchmark
    public int removeAllValue(BenchmarkState s) {
        IntArrayList list = s.nextList();
        int val = s.randomValues[s.poolIndex & (s.pool.length - 1)];
        return list.removeAll(val);
    }

    @Benchmark
    public void ensureCapacity(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.ensureCapacity(list.size() + 100);
    }

    @Benchmark
    public void resizeGrow(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.resize(list.size() + 10);
    }

    @Benchmark
    public void resizeShrink(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.resize(list.size() - 10);
    }

    @Benchmark
    public void trimToSize(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.trimToSize();
    }

    @Benchmark
    public void clear(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.clear();
    }

    @Benchmark
    public void release(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.release();
    }

    @Benchmark
    public void sort(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.sort();
    }

    @Benchmark
    public void reverse(BenchmarkState s) {
        IntArrayList list = s.nextList();
        list.reverse();
    }

    @Benchmark
    public int removeAllPredicate(BenchmarkState s) {
        IntArrayList list = s.nextList();
        return list.removeAll(new IntPredicate() {
            @Override
            public boolean apply(int value) {
                return value > 500;
            }
        });
    }
}

package bench.generated.c071;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import com.carrotsearch.hppc.ShortArrayList;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.procedures.ShortProcedure;
import com.carrotsearch.hppc.predicates.ShortPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortArrayListBenchmark {

    // Configuration parameters – built in @Setup
    private int listSize;
    private short element;
    private short[] smallArray;
    private int insertIndex;
    private int removeIndex;
    private int resizeSize;
    private int ensureCapacityExtra;
    private int rangeFrom;
    private int rangeTo;

    private ShortArrayList baseList;
    private ShortArrayList otherList;
    private Iterable<ShortCursor> cursorIterable;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(0xdeadbeefL);

        listSize = 1024;
        element = 123;
        smallArray = new short[] { 1, 2 };
        insertIndex = listSize / 2;
        removeIndex = listSize / 3;
        resizeSize = listSize / 2;
        ensureCapacityExtra = 512;
        rangeFrom = 100;
        rangeTo = 200;

        baseList = new ShortArrayList(listSize);
        for (int i = 0; i < listSize; i++) {
            baseList.add((short) rnd.nextInt(Short.MAX_VALUE + 1));
        }

        otherList = new ShortArrayList(listSize);
        for (int i = 0; i < listSize; i++) {
            otherList.add((short) rnd.nextInt(Short.MAX_VALUE + 1));
        }

        cursorIterable = new Iterable<ShortCursor>() {
            @Override
            public java.util.Iterator<ShortCursor> iterator() {
                return baseList.iterator();
            }
        };
    }

    // -------------------------------------------------------------------------
    // Simple read‑only operations
    // -------------------------------------------------------------------------

    @Benchmark
    public short get() {
        return baseList.get(insertIndex);
    }

    @Benchmark
    public boolean contains() {
        return baseList.contains(element);
    }

    @Benchmark
    public int indexOf() {
        return baseList.indexOf(element);
    }

    @Benchmark
    public int lastIndexOf() {
        return baseList.lastIndexOf(element);
    }

    @Benchmark
    public int size() {
        return baseList.size();
    }

    @Benchmark
    public short[] toArray() {
        return baseList.toArray();
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return baseList.hashCode();
    }

    @Benchmark
    public boolean equalsClone() {
        ShortArrayList clone = baseList.clone();
        return baseList.equals(clone);
    }

    // -------------------------------------------------------------------------
    // Mutating operations – each works on a fresh clone to avoid state bleed.
    // -------------------------------------------------------------------------

    @Benchmark
    public int add() {
        ShortArrayList list = baseList.clone();
        list.add(element);
        return list.size();
    }

    @Benchmark
    public int addTwo() {
        ShortArrayList list = baseList.clone();
        list.add((short) 1, (short) 2);
        return list.size();
    }

    @Benchmark
    public int addArray() {
        ShortArrayList list = baseList.clone();
        list.add(smallArray, 0, smallArray.length);
        return list.size();
    }

    @Benchmark
    public int addVarargs() {
        ShortArrayList list = baseList.clone();
        list.add((short) 7, (short) 8, (short) 9);
        return list.size();
    }

    @Benchmark
    public int addAllContainer() {
        ShortArrayList list = baseList.clone();
        list.addAll(otherList);
        return list.size();
    }

    @Benchmark
    public int addAllIterable() {
        ShortArrayList list = baseList.clone();
        list.addAll(cursorIterable);
        return list.size();
    }

    @Benchmark
    public int insert() {
        ShortArrayList list = baseList.clone();
        list.insert(insertIndex, element);
        return list.size();
    }

    @Benchmark
    public short set() {
        ShortArrayList list = baseList.clone();
        return list.set(insertIndex, element);
    }

    @Benchmark
    public short removeAt() {
        ShortArrayList list = baseList.clone();
        return list.removeAt(removeIndex);
    }

    @Benchmark
    public short removeLast() {
        ShortArrayList list = baseList.clone();
        return list.removeLast();
    }

    @Benchmark
    public int removeRange() {
        ShortArrayList list = baseList.clone();
        list.removeRange(rangeFrom, rangeTo);
        return list.size();
    }

    @Benchmark
    public boolean removeElement() {
        ShortArrayList list = baseList.clone();
        return list.removeElement(element);
    }

    @Benchmark
    public int removeFirst() {
        ShortArrayList list = baseList.clone();
        return list.removeFirst(element);
    }

    @Benchmark
    public int removeLastElement() {
        ShortArrayList list = baseList.clone();
        return list.removeLast(element);
    }

    @Benchmark
    public int removeAllValue() {
        ShortArrayList list = baseList.clone();
        return list.removeAll(element);
    }

    @Benchmark
    public int ensureCapacity() {
        ShortArrayList list = baseList.clone();
        list.ensureCapacity(list.size() + ensureCapacityExtra);
        return list.size();
    }

    @Benchmark
    public int resize() {
        ShortArrayList list = baseList.clone();
        list.resize(resizeSize);
        return list.size();
    }

    @Benchmark
    public int trimToSize() {
        ShortArrayList list = baseList.clone();
        list.trimToSize();
        return list.size();
    }

    @Benchmark
    public boolean clear() {
        ShortArrayList list = baseList.clone();
        list.clear();
        return list.isEmpty();
    }

    @Benchmark
    public int release() {
        ShortArrayList list = baseList.clone();
        list.release();
        return list.size();
    }

    @Benchmark
    public ShortArrayList sort() {
        ShortArrayList list = baseList.clone();
        list.sort();
        return list;
    }

    @Benchmark
    public ShortArrayList reverse() {
        ShortArrayList list = baseList.clone();
        list.reverse();
        return list;
    }

    @Benchmark
    public ShortArrayList cloneBenchmark() {
        return baseList.clone();
    }

    // -------------------------------------------------------------------------
    // Procedure / Predicate based iteration
    // -------------------------------------------------------------------------

    private static final class SumProcedure implements ShortProcedure {
        int sum = 0;

        @Override
        public void apply(short value) {
            sum += value;
        }
    }

    @Benchmark
    public int forEachProcedure() {
        SumProcedure proc = new SumProcedure();
        baseList.forEach(proc);
        return proc.sum;
    }

    private static final class CountPredicate implements ShortPredicate {
        int count = 0;

        @Override
        public boolean apply(short value) {
            count++;
            return true;
        }
    }

    @Benchmark
    public int forEachPredicate() {
        CountPredicate pred = new CountPredicate();
        baseList.forEach(pred);
        return pred.count;
    }

    // -------------------------------------------------------------------------
    // Iterator benchmark
    // -------------------------------------------------------------------------

    @Benchmark
    public int iteratorSum(Blackhole bh) {
        int sum = 0;
        for (ShortCursor c : baseList) {
            sum += c.value;
        }
        bh.consume(sum);
        return sum;
    }
}

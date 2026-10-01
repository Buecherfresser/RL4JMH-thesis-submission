package bench.generated.c006;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharArrayList;
import com.carrotsearch.hppc.predicates.CharPredicate;
import com.carrotsearch.hppc.procedures.CharProcedure;
import com.carrotsearch.hppc.cursors.CharCursor;
import com.carrotsearch.hppc.CharIndexedContainer;
import java.util.Iterator;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharArrayListBenchmark {

    private CharArrayList list;          // read‑only baseline
    private CharArrayList mutableList;   // reset before each invocation
    private char[] data;
    private char element;
    private CharPredicate predicate;
    private CharProcedure procedure;

    @Setup(Level.Trial)
    public void setUpTrial() {
        int size = 1024;
        list = new CharArrayList(size);
        data = new char[size];
        Random rnd = new Random(0);
        for (int i = 0; i < size; i++) {
            char c = (char) (rnd.nextInt(26) + 'a');
            data[i] = c;
            list.add(c);
        }
        element = data[0];
        predicate = new CharPredicate() {
            @Override
            public boolean apply(char value) {
                return value == element;
            }
        };
        procedure = new CharProcedure() {
            @Override
            public void apply(char value) {
                // no‑op
            }
        };
    }

    @Setup(Level.Invocation)
    public void setUpInvocation() {
        mutableList = list.clone();
    }

    @Benchmark
    public CharArrayList benchAdd() {
        mutableList.add(element);
        return mutableList;
    }

    @Benchmark
    public CharArrayList benchAddTwo() {
        mutableList.add(element, element);
        return mutableList;
    }

    @Benchmark
    public CharArrayList benchAddArray() {
        mutableList.add(data, 0, data.length);
        return mutableList;
    }

    @Benchmark
    public CharArrayList benchInsert() {
        mutableList.insert(0, element);
        return mutableList;
    }

    @Benchmark
    public char benchGet() {
        return mutableList.get(0);
    }

    @Benchmark
    public char benchSet() {
        return mutableList.set(0, element);
    }

    @Benchmark
    public char benchRemoveAt() {
        return mutableList.removeAt(0);
    }

    @Benchmark
    public char benchRemoveLast() {
        return mutableList.removeLast();
    }

    @Benchmark
    public CharArrayList benchRemoveRange() {
        int mid = mutableList.size() / 2;
        mutableList.removeRange(0, mid);
        return mutableList;
    }

    @Benchmark
    public boolean benchContains() {
        return mutableList.contains(element);
    }

    @Benchmark
    public int benchIndexOf() {
        return mutableList.indexOf(element);
    }

    @Benchmark
    public int benchLastIndexOf() {
        return mutableList.lastIndexOf(element);
    }

    @Benchmark
    public CharArrayList benchEnsureCapacity() {
        mutableList.ensureCapacity(mutableList.size() * 2);
        return mutableList;
    }

    @Benchmark
    public CharArrayList benchResize() {
        mutableList.resize(mutableList.size() / 2);
        return mutableList;
    }

    @Benchmark
    public CharArrayList benchTrimToSize() {
        mutableList.trimToSize();
        return mutableList;
    }

    @Benchmark
    public CharArrayList benchClear() {
        mutableList.clear();
        return mutableList;
    }

    @Benchmark
    public CharArrayList benchRelease() {
        mutableList.release();
        return mutableList;
    }

    @Benchmark
    public char[] benchToArray() {
        return mutableList.toArray();
    }

    @Benchmark
    public CharIndexedContainer benchSort() {
        return mutableList.sort();
    }

    @Benchmark
    public CharIndexedContainer benchReverse() {
        return mutableList.reverse();
    }

    @Benchmark
    public CharArrayList benchClone() {
        return mutableList.clone();
    }

    @Benchmark
    public int benchHashCode() {
        return mutableList.hashCode();
    }

    @Benchmark
    public boolean benchEquals() {
        return mutableList.equals(list);
    }

    @Benchmark
    public Iterator<CharCursor> benchIterator() {
        return mutableList.iterator();
    }

    @Benchmark
    public CharProcedure benchForEachProcedure() {
        return mutableList.forEach(procedure);
    }

    @Benchmark
    public CharPredicate benchForEachPredicate() {
        return mutableList.forEach(predicate);
    }

    @Benchmark
    public int benchRemoveAllPredicate() {
        return mutableList.removeAll(predicate);
    }
}

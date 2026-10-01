package bench.generated.c070;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortArrayDeque;
import com.carrotsearch.hppc.cursors.ShortCursor;
import com.carrotsearch.hppc.procedures.ShortProcedure;
import com.carrotsearch.hppc.predicates.ShortPredicate;
import java.util.Iterator;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortArrayDequeBenchmark {

    private ShortArrayDeque baseDeque;
    private short[] sampleArray;
    private short sampleValue;
    private short missingValue;
    private int sampleIndex;
    private ShortArrayDeque otherDeque;
    private ShortProcedure sumProcedure;
    private ShortPredicate truePredicate;
    private ShortPredicate removePredicate;
    private List<ShortCursor> cursorList;

    @Setup(Level.Trial)
    public void init() {
        Random rand = new Random(0);
        int initialSize = 1024;
        baseDeque = new ShortArrayDeque(initialSize);
        for (int i = 0; i < initialSize; i++) {
            short v = (short) i;
            baseDeque.addLast(v);
        }
        sampleArray = new short[] {1, 2, 3, 4, 5};
        sampleValue = baseDeque.getFirst();
        missingValue = (short) -1;
        sampleIndex = baseDeque.bufferIndexOf(sampleValue);
        otherDeque = baseDeque.clone();

        sumProcedure = new ShortProcedure() {
            @Override
            public void apply(short value) {
                // consume to avoid dead code elimination
            }
        };

        truePredicate = new ShortPredicate() {
            @Override
            public boolean apply(short value) {
                return true;
            }
        };

        removePredicate = new ShortPredicate() {
            @Override
            public boolean apply(short value) {
                return value == sampleValue;
            }
        };

        cursorList = new ArrayList<>();
        for (short v : sampleArray) {
            ShortCursor c = new ShortCursor();
            c.value = v;
            cursorList.add(c);
        }
    }

    // --- Mutating operations (use clone to avoid affecting shared state) ---

    @Benchmark
    public short benchAddFirst(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.addFirst(sampleValue);
        bh.consume(d.size());
        return d.getFirst();
    }

    @Benchmark
    public short benchAddLast(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.addLast(sampleValue);
        bh.consume(d.size());
        return d.getLast();
    }

    @Benchmark
    public short benchAddFirstVarargs(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.addFirst(sampleArray);
        bh.consume(d.size());
        return d.getFirst();
    }

    @Benchmark
    public short benchAddLastVarargs(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.addLast(sampleArray);
        bh.consume(d.size());
        return d.getLast();
    }

    @Benchmark
    public short benchRemoveFirst(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        short removed = d.removeFirst();
        bh.consume(d.size());
        return removed;
    }

    @Benchmark
    public short benchRemoveLast(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        short removed = d.removeLast();
        bh.consume(d.size());
        return removed;
    }

    @Benchmark
    public int benchRemoveAllValue(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        int removed = d.removeAll(sampleValue);
        bh.consume(d.size());
        return removed;
    }

    @Benchmark
    public void benchRemoveAtBufferIndex(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.removeAtBufferIndex(sampleIndex);
        bh.consume(d.size());
    }

    @Benchmark
    public void benchClear(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.clear();
        bh.consume(d.size());
    }

    @Benchmark
    public void benchRelease(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.release();
        bh.consume(d.size());
    }

    @Benchmark
    public void benchEnsureCapacity(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.ensureCapacity(2048);
        bh.consume(d.size());
    }

    @Benchmark
    public void benchRemoveAllPredicate(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        d.removeAll(removePredicate);
        bh.consume(d.size());
    }

    @Benchmark
    public void benchForEachProcedure(Blackhole bh) {
        baseDeque.forEach(sumProcedure);
        bh.consume(baseDeque.size());
    }

    @Benchmark
    public void benchForEachPredicate(Blackhole bh) {
        baseDeque.forEach(truePredicate);
        bh.consume(baseDeque.size());
    }

    @Benchmark
    public void benchDescendingForEachProcedure(Blackhole bh) {
        baseDeque.descendingForEach(sumProcedure);
        bh.consume(baseDeque.size());
    }

    @Benchmark
    public void benchDescendingForEachPredicate(Blackhole bh) {
        baseDeque.descendingForEach(truePredicate);
        bh.consume(baseDeque.size());
    }

    // --- Read‑only operations ---

    @Benchmark
    public short benchGetFirst() {
        return baseDeque.getFirst();
    }

    @Benchmark
    public short benchGetLast() {
        return baseDeque.getLast();
    }

    @Benchmark
    public int benchBufferIndexOf() {
        return baseDeque.bufferIndexOf(sampleValue);
    }

    @Benchmark
    public int benchLastBufferIndexOf() {
        return baseDeque.lastBufferIndexOf(sampleValue);
    }

    @Benchmark
    public int benchSize() {
        return baseDeque.size();
    }

    @Benchmark
    public boolean benchIsEmpty() {
        return baseDeque.isEmpty();
    }

    @Benchmark
    public short[] benchToArray() {
        return baseDeque.toArray();
    }

    @Benchmark
    public Iterator<ShortCursor> benchIterator() {
        return baseDeque.iterator();
    }

    @Benchmark
    public Iterator<ShortCursor> benchDescendingIterator() {
        return baseDeque.descendingIterator();
    }

    @Benchmark
    public boolean benchContains() {
        return baseDeque.contains(sampleValue);
    }

    @Benchmark
    public int benchHashCode() {
        return baseDeque.hashCode();
    }

    @Benchmark
    public boolean benchEquals() {
        return baseDeque.equals(otherDeque);
    }

    @Benchmark
    public ShortArrayDeque benchClone() {
        return baseDeque.clone();
    }

    @Benchmark
    public ShortArrayDeque benchStaticFrom() {
        return ShortArrayDeque.from(sampleArray);
    }

    @Benchmark
    public int benchAddFirstContainer(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        int added = d.addFirst(baseDeque);
        bh.consume(d.size());
        return added;
    }

    @Benchmark
    public int benchAddLastContainer(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        int added = d.addLast(baseDeque);
        bh.consume(d.size());
        return added;
    }

    @Benchmark
    public int benchAddFirstIterable(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        int added = d.addFirst(cursorList);
        bh.consume(d.size());
        return added;
    }

    @Benchmark
    public int benchAddLastIterable(Blackhole bh) {
        ShortArrayDeque d = baseDeque.clone();
        int added = d.addLast(cursorList);
        bh.consume(d.size());
        return added;
    }
}

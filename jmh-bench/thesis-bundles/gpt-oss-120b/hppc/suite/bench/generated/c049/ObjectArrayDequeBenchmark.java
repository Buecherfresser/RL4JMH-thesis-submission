package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import com.carrotsearch.hppc.ObjectArrayDeque;
import com.carrotsearch.hppc.cursors.ObjectCursor;
import com.carrotsearch.hppc.procedures.ObjectProcedure;
import com.carrotsearch.hppc.predicates.ObjectPredicate;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectArrayDequeBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        // Pool of deques to avoid unbounded growth during mutating benchmarks.
        ObjectArrayDeque<String>[] dequePool;
        int poolSize = 8;
        int poolIndex = 0;

        // A secondary deque used as a container argument.
        ObjectArrayDeque<String> otherDeque;

        // Sample data.
        String element;
        String[] varargElements;
        ObjectProcedure<String> noopProcedure;
        ObjectPredicate<String> alwaysTruePredicate;
        ObjectPredicate<String> alwaysFalsePredicate;

        @SuppressWarnings("unchecked")
        @Setup(Level.Trial)
        public void setUp() {
            // Initialize pool.
            dequePool = new ObjectArrayDeque[poolSize];
            for (int i = 0; i < poolSize; i++) {
                ObjectArrayDeque<String> d = new ObjectArrayDeque<>(128);
                // Pre‑fill with some elements to avoid immediate resizing.
                for (int j = 0; j < 64; j++) {
                    d.addLast("e" + i + "_" + j);
                }
                dequePool[i] = d;
            }

            // Initialize otherDeque used as a container argument.
            otherDeque = new ObjectArrayDeque<>(128);
            for (int i = 0; i < 64; i++) {
                otherDeque.addLast("o" + i);
            }

            element = "elem";
            varargElements = new String[] { "a", "b", "c" };

            noopProcedure = new ObjectProcedure<String>() {
                @Override public void apply(String value) { /* no‑op */ }
            };
            alwaysTruePredicate = new ObjectPredicate<String>() {
                @Override public boolean apply(String value) { return true; }
            };
            alwaysFalsePredicate = new ObjectPredicate<String>() {
                @Override public boolean apply(String value) { return false; }
            };
        }

        /** Retrieve the next deque from the pool in a round‑robin fashion. */
        ObjectArrayDeque<String> nextDeque() {
            int idx = poolIndex;
            poolIndex = (poolIndex + 1) % poolSize;
            return dequePool[idx];
        }
    }

    @Benchmark
    public String addFirst(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addFirst(s.element);
        return s.element;
    }

    @Benchmark
    public String addLast(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return s.element;
    }

    @Benchmark
    public String addFirstVararg(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addFirst(s.varargElements);
        return s.varargElements[0];
    }

    @Benchmark
    public String addLastVararg(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.varargElements);
        return s.varargElements[0];
    }

    @Benchmark
    public String addFirstContainer(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addFirst(s.otherDeque);
        return d.getFirst();
    }

    @Benchmark
    public String addLastContainer(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.otherDeque);
        return d.getLast();
    }

    @Benchmark
    public String removeFirst(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.removeFirst();
    }

    @Benchmark
    public String removeLast(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.removeLast();
    }

    @Benchmark
    public String getFirst(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.getFirst();
    }

    @Benchmark
    public String getLast(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.getLast();
    }

    @Benchmark
    public int removeFirstByValue(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.removeFirst(s.element);
    }

    @Benchmark
    public int removeLastByValue(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.removeLast(s.element);
    }

    @Benchmark
    public int removeAllByValue(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        d.addLast(s.element);
        return d.removeAll(s.element);
    }

    @Benchmark
    public int bufferIndexOf(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.bufferIndexOf(s.element);
    }

    @Benchmark
    public int lastBufferIndexOf(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.lastBufferIndexOf(s.element);
    }

    @Benchmark
    public boolean contains(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.addLast(s.element);
        return d.contains(s.element);
    }

    @Benchmark
    public int size(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        return d.size();
    }

    @Benchmark
    public boolean isEmpty(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        return d.isEmpty();
    }

    @Benchmark
    public boolean clear(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.clear();
        return d.isEmpty();
    }

    @Benchmark
    public int release(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.release();
        return d.buffer.length;
    }

    @Benchmark
    public int ensureCapacity(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.ensureCapacity(256);
        return d.buffer.length;
    }

    @Benchmark
    public int toArrayLength(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        return d.toArray(new String[0]).length;
    }

    @Benchmark
    public boolean iteratorHasNext(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        Iterator<ObjectCursor<String>> it = d.iterator();
        return it.hasNext();
    }

    @Benchmark
    public boolean descendingIteratorHasNext(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        Iterator<ObjectCursor<String>> it = d.descendingIterator();
        return it.hasNext();
    }

    @Benchmark
    public int forEachProcedure(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.forEach(s.noopProcedure);
        return d.size();
    }

    @Benchmark
    public int forEachPredicate(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.forEach(s.alwaysTruePredicate);
        return d.size();
    }

    @Benchmark
    public int descendingForEachProcedure(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.descendingForEach(s.noopProcedure);
        return d.size();
    }

    @Benchmark
    public int descendingForEachPredicate(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.descendingForEach(s.alwaysTruePredicate);
        return d.size();
    }

    @Benchmark
    public int removeAllPredicate(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        d.removeAll(s.alwaysFalsePredicate);
        return d.size();
    }

    @Benchmark
    public int hashCode(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        return d.hashCode();
    }

    @Benchmark
    public boolean equalsOther(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        return d.equals(s.otherDeque);
    }

    @Benchmark
    public int cloneSize(BenchmarkState s) {
        ObjectArrayDeque<String> d = s.nextDeque();
        ObjectArrayDeque<String> cloned = d.clone();
        return cloned.size();
    }
}

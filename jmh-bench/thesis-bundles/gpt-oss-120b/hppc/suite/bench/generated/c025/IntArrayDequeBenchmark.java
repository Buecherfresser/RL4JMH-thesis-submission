package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntArrayDeque;
import com.carrotsearch.hppc.cursors.IntCursor;
import java.util.Iterator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntArrayDequeBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        IntArrayDeque deque;
        IntArrayDeque copyDeque;
        int element = -1;
        int presentValue = 500;
        int absentValue = -1;

        @Setup(Level.Trial)
        public void setUp() {
            int initialSize = 1_000_000;
            deque = new IntArrayDeque(initialSize);
            for (int i = 0; i < initialSize; i++) {
                deque.addLast(i);
            }
            copyDeque = new IntArrayDeque(initialSize);
            for (int i = 0; i < initialSize; i++) {
                copyDeque.addLast(i);
            }
        }
    }

    @Benchmark
    public int addFirst(BenchmarkState s) {
        IntArrayDeque d = s.deque.clone();
        d.addFirst(s.element);
        return d.getFirst();
    }

    @Benchmark
    public int addLast(BenchmarkState s) {
        IntArrayDeque d = s.deque.clone();
        d.addLast(s.element);
        return d.getLast();
    }

    @Benchmark
    public int removeFirst(BenchmarkState s) {
        IntArrayDeque d = s.deque.clone();
        // Ensure there is at least one element to remove
        if (d.isEmpty()) {
            d.addFirst(0);
        }
        return d.removeFirst();
    }

    @Benchmark
    public int removeLast(BenchmarkState s) {
        IntArrayDeque d = s.deque.clone();
        if (d.isEmpty()) {
            d.addLast(0);
        }
        return d.removeLast();
    }

    @Benchmark
    public int size(BenchmarkState s) {
        return s.deque.size();
    }

    @Benchmark
    public int getFirst(BenchmarkState s) {
        return s.deque.getFirst();
    }

    @Benchmark
    public int getLast(BenchmarkState s) {
        return s.deque.getLast();
    }

    @Benchmark
    public boolean containsPresent(BenchmarkState s) {
        return s.deque.contains(s.presentValue);
    }

    @Benchmark
    public boolean containsAbsent(BenchmarkState s) {
        return s.deque.contains(s.absentValue);
    }

    @Benchmark
    public int hashCode(BenchmarkState s) {
        return s.deque.hashCode();
    }

    @Benchmark
    public boolean equalsCopy(BenchmarkState s) {
        return s.deque.equals(s.copyDeque);
    }

    @Benchmark
    public int[] toArray(BenchmarkState s) {
        return s.deque.toArray();
    }

    @Benchmark
    public long ramBytesUsed(BenchmarkState s) {
        return s.deque.ramBytesUsed();
    }

    @Benchmark
    public void iterate(BenchmarkState s, Blackhole bh) {
        Iterator<IntCursor> it = s.deque.iterator();
        while (it.hasNext()) {
            bh.consume(it.next().value);
        }
    }

    @Benchmark
    public void descendingIterate(BenchmarkState s, Blackhole bh) {
        Iterator<IntCursor> it = s.deque.descendingIterator();
        while (it.hasNext()) {
            bh.consume(it.next().value);
        }
    }
}

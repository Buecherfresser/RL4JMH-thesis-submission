package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.IntArrayDeque;
import com.carrotsearch.hppc.Containers;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class IntArrayDequeBenchmark {

    // We don't need a shared state field since we create a fresh instance
    // in each benchmark method for AverageTime mode, isolating the timing.

    @Benchmark
    public void testAddFirstSingle(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        deque.addFirst(10);
        bh.consume(deque);
    }

    @Benchmark
    public void testAddLastSingle(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        deque.addLast(20);
        bh.consume(deque);
    }

    @Benchmark
    public void testAddFirstMultiple(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        deque.addFirst(1);
        deque.addFirst(2);
        bh.consume(deque);
    }

    @Benchmark
    public void testRemoveFirst(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        // Populate minimally to ensure removeFirst doesn't fail immediately
        try {
            deque.addLast(100);
        } catch (Exception e) {
            // Ignore potential exceptions during setup if the implementation is strict
        }
        bh.consume(deque.removeFirst());
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        try {
            deque.addLast(100);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(deque.removeLast());
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        try {
            deque.addLast(50);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(deque.contains(50));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        bh.consume(deque.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        try {
            deque.addLast(1);
        } catch (Exception e) {
            // Ignore
        }
        deque.clear();
        bh.consume(deque);
    }

    @Benchmark
    public void testRemoveAllInt(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        try {
            deque.addLast(10);
            deque.addLast(20);
            deque.addLast(10);
        } catch (Exception e) {
            // Ignore
        }
        // Remove all elements equal to 10
        bh.consume(deque.removeAll(10));
    }

    @Benchmark
    public void testRemoveAllPredicate(Blackhole bh) {
        IntArrayDeque deque = new IntArrayDeque();
        try {
            deque.addLast(10);
            deque.addLast(20);
            deque.addLast(10);
        } catch (Exception e) {
            // Ignore
        }
        // Remove all even numbers (assuming 10 and 20 are present)
        bh.consume(deque.removeAll(n -> n % 2 == 0));
    }
}

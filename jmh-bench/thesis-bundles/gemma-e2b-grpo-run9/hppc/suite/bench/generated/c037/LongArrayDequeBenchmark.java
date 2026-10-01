package bench.generated.c037;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import com.carrotsearch.hppc.LongArrayDeque;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongArrayDequeBenchmark {

    // State field for the LongArrayDeque instance.
    // Since LongArrayDeque is mutable and resizing occurs, we rely on JMH's
    // trial isolation or the fact that the benchmark measures the operation
    // on a fresh instance if we create it inside the benchmark method.
    private LongArrayDeque deque;

    @Setup
    public void setup() {
        // Initialize a small deque for general testing.
        // We use the default constructor which initializes with a small expected size.
        this.deque = new LongArrayDeque(10);
    }

    @Benchmark
    public void testGetFirst(Blackhole bh) {
        // Read operation
        bh.consume(deque.getFirst());
    }

    @Benchmark
    public void testGetLast(Blackhole bh) {
        // Read operation
        bh.consume(deque.getLast());
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        // Read operation (requires some elements to be present, but testing the path)
        // Since the deque is empty initially, this will likely return false quickly.
        bh.consume(deque.contains(123L));
    }

    @Benchmark
    public void testRemoveFirst(Blackhole bh) {
        // Mutating operation
        bh.consume(deque.removeFirst());
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        // Mutating operation
        bh.consume(deque.removeLast());
    }

    @Benchmark
    public void testAddFirstSingle(Blackhole bh) {
        // Mutating operation
        deque.addFirst(100L);
        bh.consume(deque.getFirst());
    }

    @Benchmark
    public void testAddLastSingle(Blackhole bh) {
        // Mutating operation
        deque.addLast(200L);
        bh.consume(deque.getLast());
    }

    @Benchmark
    public void testAddFirstVarargs(Blackhole bh) {
        // Mutating operation (varargs)
        deque.addFirst(1L, 2L, 3L);
        bh.consume(deque.getFirst());
    }

    @Benchmark
    public void testAddLastVarargs(Blackhole bh) {
        // Mutating operation (varargs)
        deque.addLast(4L, 5L);
        bh.consume(deque.getLast());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Mutating operation
        deque.clear();
        bh.consume(deque.size());
    }

    @Benchmark
    public void testToArray(Blackhole bh) {
        // Read-only operation (requires some elements to be present)
        // Populate the deque slightly to ensure toArray doesn't fail on empty state
        deque.addLast(1L);
        long[] result = deque.toArray(new long[10]);
        bh.consume(result);
    }
}

package bench.generated.c049;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectArrayDeque;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectArrayDequeBenchmark {

    // State field for the subject under test.
    // Using Integer as a concrete type for benchmarking.
    private ObjectArrayDeque<Integer> deque;

    @Setup
    public void setup() {
        // Initialize a fresh deque for each benchmark run.
        // Using the default constructor which initializes with DEFAULT_EXPECTED_ELEMENTS.
        this.deque = new ObjectArrayDeque<>();
    }

    @Benchmark
    public void addFirst(Blackhole bh) {
        // Test adding a single element
        deque.addFirst(100);
        bh.consume(deque);
    }

    @Benchmark
    public void addLast(Blackhole bh) {
        // Test adding a single element
        deque.addLast(200);
        bh.consume(deque);
    }

    @Benchmark
    public void addFirstMultiple(Blackhole bh) {
        // Test adding multiple elements (varargs)
        deque.addFirst(1);
        deque.addFirst(2);
        bh.consume(deque);
    }

    @Benchmark
    public void removeFirst(Blackhole bh) {
        // Test removal of the head element
        deque.addFirst(1);
        // We ignore the returned index since we only measure the operation time
        deque.removeFirst();
        bh.consume(deque);
    }

    @Benchmark
    public void removeLast(Blackhole bh) {
        // Test removal of the tail element
        deque.addLast(1);
        deque.removeLast();
        bh.consume(deque);
    }

    @Benchmark
    public void contains(Blackhole bh) {
        // Test lookup on an empty deque (should be fast)
        bh.consume(deque.contains(999));
    }

    @Benchmark
    public void containsFound(Blackhole bh) {
        // Populate the deque slightly to test non-empty lookup
        deque.addLast(10);
        bh.consume(deque.contains(10));
    }

    @Benchmark
    public void clear(Blackhole bh) {
        // Test clearing the deque
        deque.addLast(1);
        deque.clear();
        bh.consume(deque);
    }

    @Benchmark
    public void release(Blackhole bh) {
        // Test releasing internal buffers
        deque.addLast(1);
        deque.release();
        bh.consume(deque);
    }

    @Benchmark
    public void clone(Blackhole bh) {
        // Test cloning the deque
        deque.addLast(1);
        ObjectArrayDeque<Integer> cloned = deque.clone();
        bh.consume(cloned);
    }
}

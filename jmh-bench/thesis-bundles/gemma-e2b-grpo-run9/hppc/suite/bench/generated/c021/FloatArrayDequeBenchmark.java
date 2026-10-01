package bench.generated.c021;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.FloatArrayDeque;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatArrayDequeBenchmark {

    // State field for the subject under test.
    // Since FloatArrayDeque is mutable, we rely on JMH's isolation between benchmark methods
    // or the fact that the state is reset between iterations/measurements.
    private FloatArrayDeque deque;

    @Setup
    public void setup() {
        // Initialize a fresh deque for each benchmark run.
        // Using the default constructor which initializes with DEFAULT_EXPECTED_ELEMENTS.
        this.deque = new FloatArrayDeque();
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size()
        bh.consume(deque.size());
    }

    @Benchmark
    public void testIsEmpty(Blackhole bh) {
        // Test isEmpty()
        bh.consume(deque.isEmpty());
    }

    @Benchmark
    public void testAddLast(Blackhole bh) {
        // Test addLast(float)
        deque.addLast(1.0f);
        bh.consume(deque);
    }

    @Benchmark
    public void testRemoveFirst(Blackhole bh) {
        // Test removeFirst()
        // We must ensure the deque is not empty for this test to avoid assertion failure,
        // though JMH might run this on an empty state if setup fails.
        try {
            deque.addLast(1.0f);
        } catch (Exception e) {
            // Ignore setup failure if it happens in a specific run
        }
        bh.consume(deque.removeFirst());
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        // Test contains(float)
        // Since the deque starts empty, this should return false.
        bh.consume(deque.contains(99.9f));
    }

    @Benchmark
    public void testAddFirst(Blackhole bh) {
        // Test addFirst(float)
        deque.addFirst(2.0f);
        bh.consume(deque);
    }

    @Benchmark
    public void testRemoveLast(Blackhole bh) {
        // Test removeLast()
        try {
            deque.addLast(1.0f);
        } catch (Exception e) {
            // Ignore setup failure
        }
        bh.consume(deque.removeLast());
    }

    @Benchmark
    public void testRemoveFirstByValue(Blackhole bh) {
        // Test removeFirst(float) - requires adding an element first
        try {
            deque.addLast(1.0f);
        } catch (Exception e) {
            // Ignore setup failure
        }
        // Remove the element we just added (1.0f)
        bh.consume(deque.removeFirst(1.0f));
    }
}

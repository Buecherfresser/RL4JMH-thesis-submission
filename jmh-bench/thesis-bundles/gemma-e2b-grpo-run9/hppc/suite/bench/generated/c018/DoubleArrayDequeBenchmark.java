package bench.generated.c018;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.DoubleArrayDeque;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class DoubleArrayDequeBenchmark {

    // Since DoubleArrayDeque is mutable and we want to test operations in isolation,
    // we instantiate it inside the benchmark methods for mutating operations.
    // This avoids state pollution between benchmark runs.

    @Benchmark
    public void testAddFirst(Blackhole bh) {
        // Test adding a single element
        DoubleArrayDeque deque = new DoubleArrayDeque();
        deque.addFirst(1.0);
        bh.consume(deque);
    }

    @Benchmark
    public void testAddFirstMultiple(Blackhole bh) {
        // Test adding multiple elements
        DoubleArrayDeque deque = new DoubleArrayDeque();
        deque.addFirst(1.0, 2.0, 3.0);
        bh.consume(deque);
    }

    @Benchmark
    public void testRemoveFirst(Blackhole bh) {
        // Test removing the first element (requires non-empty state)
        DoubleArrayDeque deque = new DoubleArrayDeque();
        deque.addFirst(1.0);
        
        double result = deque.removeFirst();
        bh.consume(result);
    }

    @Benchmark
    public void testContains(Blackhole bh) {
        // Test contains on an empty deque
        DoubleArrayDeque dequeEmpty = new DoubleArrayDeque();
        bh.consume(dequeEmpty.contains(1.0));

        // Test contains on a populated deque
        DoubleArrayDeque dequePopulated = new DoubleArrayDeque();
        dequePopulated.addFirst(1.0);
        bh.consume(dequePopulated.contains(1.0));
        bh.consume(dequePopulated.contains(2.0));
    }

    @Benchmark
    public void testSize(Blackhole bh) {
        // Test size on an empty deque
        DoubleArrayDeque dequeEmpty = new DoubleArrayDeque();
        bh.consume(dequeEmpty.size());

        // Test size on a populated deque
        DoubleArrayDeque dequePopulated = new DoubleArrayDeque();
        dequePopulated.addFirst(1.0);
        bh.consume(dequePopulated.size());
    }

    @Benchmark
    public void testClear(Blackhole bh) {
        // Test clear operation
        DoubleArrayDeque deque = new DoubleArrayDeque();
        deque.addFirst(1.0);
        deque.addFirst(2.0);
        
        deque.clear();
        bh.consume(deque);
    }

    @Benchmark
    public void testClone(Blackhole bh) {
        // Test clone operation
        DoubleArrayDeque original = new DoubleArrayDeque();
        original.addFirst(1.0);
        
        DoubleArrayDeque cloned = original.clone();
        bh.consume(cloned);
    }

    @Benchmark
    public void testToArray(Blackhole bh) {
        // Test toArray operation (requires populated state)
        DoubleArrayDeque deque = new DoubleArrayDeque();
        deque.addFirst(1.0);
        deque.addFirst(2.0);
        
        try {
            double[] array = deque.toArray(new double[0]);
            bh.consume(array);
        } catch (Exception e) {
            // Ignore exceptions if array creation fails in specific environments
        }
    }

    @Benchmark
    public void testRamUsage(Blackhole bh) {
        // Test memory usage estimation
        DoubleArrayDeque deque = new DoubleArrayDeque();
        bh.consume(deque.ramBytesUsed());
    }

    @Benchmark
    public void testFromStatic(Blackhole bh) {
        // Test static factory method
        DoubleArrayDeque deque = DoubleArrayDeque.from(1.0, 2.0);
        bh.consume(deque);
    }
}

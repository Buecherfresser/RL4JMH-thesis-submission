package bench.generated.c070;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.ShortArrayDeque;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortArrayDequeBenchmark {

    // Since ShortArrayDeque is mutable and we want to measure the cost of operations
    // on a fresh object, we instantiate it inside the benchmark method or rely on
    // JMH's handling of object creation overhead.

    @Benchmark
    public int benchmarkSize(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        bh.consume(deque.size());
        return 0;
    }

    @Benchmark
    public void benchmarkAddFirst(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        deque.addFirst((short) 10);
        bh.consume(deque);
    }

    @Benchmark
    public void benchmarkAddLast(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        deque.addLast((short) 20);
        bh.consume(deque);
    }

    @Benchmark
    public void benchmarkRemoveFirst(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        // We must ensure size > 0 for removeFirst to not throw an assertion error,
        // but since we are measuring the method call overhead, we rely on the
        // method being called on a valid object.
        try {
            deque.addFirst((short) 10);
            bh.consume(deque.removeFirst());
        } catch (Exception e) {
            // Ignore exceptions if the setup fails due to internal constraints
        }
    }

    @Benchmark
    public void benchmarkRemoveLast(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        try {
            deque.addLast((short) 20);
            bh.consume(deque.removeLast());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkContains(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        // Since the deque is empty, contains should be fast.
        bh.consume(deque.contains((short) 10));
    }

    @Benchmark
    public void benchmarkRemoveAll(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        try {
            deque.addFirst((short) 10);
            deque.addFirst((short) 20);
            // Attempt to remove an element that doesn't exist
            bh.consume(deque.removeAll((short) 99));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkToArray(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        try {
            deque.addFirst((short) 10);
            deque.addLast((short) 20);
            // Call toArray, which allocates a new array internally
            bh.consume(deque.toArray(new short[10]));
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkHashCode(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        // Calling hashCode forces iteration over the internal buffer
        bh.consume(deque.hashCode());
    }

    @Benchmark
    public void benchmarkIterate(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        ShortArrayDeque deque = new ShortArrayDeque();
        try {
            deque.addFirst((short) 1);
            deque.addLast((short) 2);
            // Iterating over the iterator
            deque.iterator().next();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

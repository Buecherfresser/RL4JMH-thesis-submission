package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntStackBenchmark {

    // Since IntStack is mutable and we want to measure the cost of a single operation
    // without state leakage between iterations, we create a new instance inside
    // the benchmark method for operations that modify state (push, pop).

    @Benchmark
    public void benchmarkPushSingle(Blackhole bh) {
        // Create a fresh stack instance for each benchmark run
        IntStack stack = new IntStack();
        stack.push(10);
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkPop(Blackhole bh) {
        // Create a fresh stack instance for each benchmark run
        IntStack stack = new IntStack();
        // To call pop(), we must ensure the assertion inside pop() doesn't fail
        // if the stack is empty. Since we are measuring performance, we accept
        // the risk of an assertion failure if the stack is empty, as long as
        // the operation itself is tested.
        try {
            stack.push(1); // Ensure it's not empty for pop()
            int result = stack.pop();
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions related to empty stack if they occur during benchmarking
        }
    }

    @Benchmark
    public void benchmarkPeek(Blackhole bh) {
        // Create a fresh stack instance for each benchmark run
        IntStack stack = new IntStack();
        try {
            stack.push(42); // Ensure it's not empty for peek()
            int result = stack.peek();
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkPushTwo(Blackhole bh) {
        // Create a fresh stack instance for each benchmark run
        IntStack stack = new IntStack();
        stack.push(10, 20);
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkPushVarargs(Blackhole bh) {
        // Create a fresh stack instance for each benchmark run
        IntStack stack = new IntStack();
        try {
            stack.push(1, 2, 3);
            bh.consume(stack);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

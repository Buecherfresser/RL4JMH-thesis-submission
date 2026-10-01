package bench.generated.c020;

import com.carrotsearch.hppc.DoubleStack;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleStackBenchmark {

    // State field: The subject instance
    private DoubleStack stack;

    // Setup method: Initialize the stack with a reasonable capacity
    @Setup
    public void setup() {
        // Initialize the stack with a capacity large enough for initial tests
        stack = new DoubleStack(1024);
    }

    @Benchmark
    public void pushSingleElement(Blackhole bh) {
        stack.push(1.0);
        bh.consume(stack);
    }

    @Benchmark
    public void pushTwoElements(Blackhole bh) {
        stack.push(1.0, 2.0);
        bh.consume(stack);
    }

    @Benchmark
    public void pushFourElements(Blackhole bh) {
        stack.push(1.0, 2.0, 3.0, 4.0);
        bh.consume(stack);
    }

    @Benchmark
    public void pushArrayRange(Blackhole bh) {
        double[] elements = new double[10];
        Arrays.fill(elements, 99.0);
        stack.push(elements, 0, 10);
        bh.consume(stack);
    }

    @Benchmark
    public void pushVarargs(Blackhole bh) {
        stack.push(1.0, 2.0, 3.0, 4.0);
        bh.consume(stack);
    }

    @Benchmark
    public void popElement(Blackhole bh) {
        // Ensure stack is populated before popping (pushing 1.0 for simplicity)
        stack.push(1.0);
        double popped = stack.pop();
        bh.consume(popped);
    }

    @Benchmark
    public void peekElement(Blackhole bh) {
        // Ensure stack is populated before peeking
        stack.push(1.0);
        double peeked = stack.peek();
        bh.consume(peeked);
    }

    @Benchmark
    public void discardSingle(Blackhole bh) {
        // Ensure stack has at least 2 elements
        stack.push(1.0);
        stack.push(2.0);
        stack.discard();
        bh.consume(stack);
    }

    @Benchmark
    public void discardMultiple(Blackhole bh) {
        // Ensure stack has at least 3 elements
        stack.push(1.0);
        stack.push(2.0);
        stack.push(3.0);
        stack.discard(2);
        bh.consume(stack);
    }

    @Benchmark
    public void pushAllArray(Blackhole bh) {
        // Simulate pushing an array of 100 elements
        double[] largeData = new double[100];
        Arrays.fill(largeData, 5.5);
        stack.push(largeData, 0, 100);
        bh.consume(stack);
    }
}

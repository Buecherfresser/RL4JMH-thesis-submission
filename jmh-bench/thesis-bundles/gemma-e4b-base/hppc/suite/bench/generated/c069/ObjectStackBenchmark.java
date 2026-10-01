package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ObjectStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectStackBenchmark {

    private static final int INPUT_SIZE = 1000;
    private String[] inputElements;

    // State fields for benchmarking
    private ObjectStack<String> stack;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Generate fixed input data once per trial
        inputElements = new String[INPUT_SIZE];
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < INPUT_SIZE; i++) {
            inputElements[i] = "Element_" + random.nextInt();
        }
    }

    // Helper method to ensure a fresh stack for each invocation
    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Reinitialize the stack for each benchmark run to ensure isolation
        stack = new ObjectStack<String>();
    }

    @Benchmark
    public void pushSingleElement(Blackhole bh) {
        // Push one element
        stack.push(inputElements[0]);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushTwoElements(Blackhole bh) {
        // Push two elements
        stack.push(inputElements[0], inputElements[1]);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushThreeElements(Blackhole bh) {
        // Push three elements
        stack.push(inputElements[0], inputElements[1], inputElements[2]);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushFourElements(Blackhole bh) {
        // Push four elements
        stack.push(inputElements[0], inputElements[1], inputElements[2], inputElements[3]);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushArrayRange(Blackhole bh) {
        // Push a range of array elements
        int len = 10;
        stack.push(inputElements, 0, len);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushAllFromContainer(Blackhole bh) {
        // Simulate the bulk push operation using the array input.
        stack.push(inputElements, 0, inputElements.length);
        bh.consume(stack.size());
    }

    @Benchmark
    public void popElement(Blackhole bh) {
        // Setup: Ensure stack has elements
        for (int i = 0; i < 10; i++) {
            stack.push(inputElements[i]);
        }
        // Benchmark: Pop
        String popped = stack.pop();
        bh.consume(popped);
    }

    @Benchmark
    public void peekTopElement(Blackhole bh) {
        // Setup: Ensure stack has elements
        stack.push(inputElements[0]);
        // Benchmark: Peek
        String peeked = stack.peek();
        bh.consume(peeked);
    }

    @Benchmark
    public void discardCountElements(Blackhole bh) {
        // Setup: Ensure stack has enough elements
        for (int i = 0; i < 20; i++) {
            stack.push(inputElements[i]);
        }
        // Benchmark: Discard 5 elements
        stack.discard(5);
        bh.consume(stack.size());
    }

    @Benchmark
    public void discardTopElement(Blackhole bh) {
        // Setup: Ensure stack has elements
        stack.push(inputElements[0]);
        // Benchmark: Discard top element
        stack.discard();
        bh.consume(stack.size());
    }

    @Benchmark
    public void staticFromVarargs(Blackhole bh) {
        // Test the static factory method
        ObjectStack<String> newStack = ObjectStack.from(inputElements[0], inputElements[1], inputElements[2]);
        bh.consume(newStack.size());
    }

    @Benchmark
    public void cloneStack(Blackhole bh) {
        // Setup: Populate stack
        for (int i = 0; i < 10; i++) {
            stack.push(inputElements[i]);
        }
        // Benchmark: Clone
        ObjectStack<String> clonedStack = stack.clone();
        bh.consume(clonedStack.size());
    }
}

package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.FloatStack;
import com.carrotsearch.hppc.FloatContainer;
import com.carrotsearch.hppc.cursors.FloatCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatStackBenchmark {

    private FloatStack stack;
    private float[] inputFloats;
    private FloatStack sourceStack;
    private List<FloatCursor> cursorList;
    private int initialCapacity = 100;

    @Setup(Level.Trial)
    public void setupTrial() {
        // Initialize fixed input data once per trial
        inputFloats = new float[initialCapacity];
        for (int i = 0; i < initialCapacity; i++) {
            inputFloats[i] = (float) i * 0.1f;
        }

        // Initialize source stack for pushAll tests
        sourceStack = new FloatStack(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            sourceStack.push((float) i);
        }

        // Initialize cursor list for pushAll(Iterable) tests
        cursorList = new ArrayList<>(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            // Fix: FloatCursor requires no arguments based on compilation error
            FloatCursor cursor = new FloatCursor();
            cursorList.add(cursor);
        }
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        // Reset the stack to a known state before each invocation
        stack = new FloatStack(initialCapacity);
    }

    // --- Push Operations ---

    @Benchmark
    public void pushOneFloat(Blackhole bh) {
        stack.push(1.0f);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushTwoFloats(Blackhole bh) {
        stack.push(1.0f, 2.0f);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushThreeFloats(Blackhole bh) {
        stack.push(1.0f, 2.0f, 3.0f);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushFourFloats(Blackhole bh) {
        stack.push(1.0f, 2.0f, 3.0f, 4.0f);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushArrayRange(Blackhole bh) {
        // Push a range of elements from the pre-built input array
        stack.push(inputFloats, 0, 10);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushVarargs(Blackhole bh) {
        // Use a small fixed array for varargs test
        float[] elements = {1.1f, 2.2f, 3.3f};
        stack.push(elements);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushAllFromContainer(Blackhole bh) {
        // Push all elements from the pre-built source stack
        int count = stack.pushAll(sourceStack);
        bh.consume(count);
    }

    @Benchmark
    public void pushAllFromIterable(Blackhole bh) {
        // Push all elements from the pre-built cursor list
        int count = stack.pushAll(cursorList);
        bh.consume(count);
    }

    // --- Stack Manipulation Operations ---

    @Benchmark
    public void discardCount(Blackhole bh) {
        // Ensure stack has enough elements before discarding
        stack.push(1.0f);
        stack.push(2.0f);
        stack.discard(1);
        bh.consume(stack.size());
    }

    @Benchmark
    public void discardTop(Blackhole bh) {
        // Ensure stack has elements before discarding
        stack.push(1.0f);
        stack.discard();
        bh.consume(stack.size());
    }

    @Benchmark
    public void popElement(Blackhole bh) {
        // Ensure stack has elements before popping
        stack.push(1.0f);
        float result = stack.pop();
        bh.consume(result);
    }

    @Benchmark
    public void peekTop(Blackhole bh) {
        // Ensure stack has elements before peeking
        stack.push(1.0f);
        float result = stack.peek();
        bh.consume(result);
    }

    // --- Static Factory Method ---

    @Benchmark
    public FloatStack fromVarargs() {
        // Test the static factory method
        float[] elements = {10.0f, 20.0f};
        return FloatStack.from(elements);
    }
}

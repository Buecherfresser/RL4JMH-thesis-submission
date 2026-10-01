package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharStack;
import com.carrotsearch.hppc.CharContainer;
import com.carrotsearch.hppc.cursors.CharCursor;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharStackBenchmark {

    private CharStack stack;
    private char[] inputChars;
    private CharStack sourceContainer; // Changed type from CharContainer to CharStack
    private List<CharCursor> cursorList;
    private static final int INPUT_SIZE = 100;

    @Setup(Level.Trial)
    public void setupTrial() {
        // 1. Input array for push(char[] elements, int start, int len)
        inputChars = new char[INPUT_SIZE];
        for (int i = 0; i < INPUT_SIZE; i++) {
            inputChars[i] = (char) ('a' + (i % 26));
        }

        // 2. Source container for pushAll(CharContainer container)
        // Since CharStack implements CharContainer, we use CharStack here to access push(char)
        sourceContainer = new CharStack(INPUT_SIZE);
        for (int i = 0; i < INPUT_SIZE; i++) {
            sourceContainer.push((char) ('A' + (i % 26)));
        }

        // 3. List of cursors for pushAll(Iterable<? extends CharCursor> iterable)
        cursorList = new ArrayList<>(INPUT_SIZE);
        for (int i = 0; i < INPUT_SIZE; i++) {
            CharCursor cursor = new CharCursor();
            cursorList.add(cursor);
        }
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Reset the stack for each invocation to ensure consistent state
        stack = new CharStack();
    }

    // --- Push Operations ---

    @Benchmark
    public void push_singleChar(Blackhole bh) {
        stack.push('z');
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_twoChars(Blackhole bh) {
        stack.push('z', 'y');
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_threeChars(Blackhole bh) {
        stack.push('z', 'y', 'x');
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_fourChars(Blackhole bh) {
        stack.push('z', 'y', 'x', 'w');
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_arrayRange(Blackhole bh) {
        // Push a range of the pre-built input array
        stack.push(inputChars, 0, 10);
        bh.consume(stack.size());
    }

    @Benchmark
    public void push_varargs(Blackhole bh) {
        // Use a small fixed array for varargs simulation
        char[] elements = new char[]{'a', 'b', 'c'};
        stack.push(elements);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushAll_container(Blackhole bh) {
        // Push all elements from the pre-built source container
        int count = stack.pushAll(sourceContainer);
        bh.consume(count);
    }

    @Benchmark
    public void pushAll_iterable(Blackhole bh) {
        // Push all elements from the pre-built cursor list
        int count = stack.pushAll(cursorList);
        bh.consume(count);
    }

    // --- Stack Manipulation Operations ---

    @Benchmark
    public void discard_count(Blackhole bh) {
        // Discard a fixed number of elements
        stack.discard(10);
        bh.consume(stack.size());
    }

    @Benchmark
    public void discard_top(Blackhole bh) {
        // Discard the top element
        stack.discard();
        bh.consume(stack.size());
    }

    @Benchmark
    public void pop_top(Blackhole bh) {
        // Pop the top element
        char result = stack.pop();
        bh.consume(result);
    }

    @Benchmark
    public void peek_top(Blackhole bh) {
        // Peek at the top element
        char result = stack.peek();
        bh.consume(result);
    }

    // --- Static Factory Method ---

    @Benchmark
    public CharStack from_varargs() {
        // Test the static factory method
        char[] elements = new char[]{'f', 'o', 'o'};
        return CharStack.from(elements);
    }
}

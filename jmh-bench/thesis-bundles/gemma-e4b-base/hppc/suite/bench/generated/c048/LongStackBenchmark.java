package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.LongStack;
import com.carrotsearch.hppc.LongContainer;
import com.carrotsearch.hppc.cursors.LongCursor;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongStackBenchmark {

    // --- State Fields ---

    // For push operations (mutating, growing state)
    private LongStack stackForPush;
    private long[] pushArray;
    private static final int PUSH_ARRAY_SIZE = 1000;

    // For pop/peek/discard operations (read/mutate, need controlled state)
    private LongStack stackForPopPeekDiscard;
    private static final int INITIAL_STACK_SIZE = 5000;

    // For pushAll operations (Iterable)
    private List<LongCursor> cursorListForPushAll;

    // --- Setup Methods ---

    @Setup(Level.Trial)
    public void setupTrial() {
        // 1. Setup for push operations (growing stack)
        stackForPush = new LongStack(100); // Start small to potentially trigger resizing
        pushArray = new long[PUSH_ARRAY_SIZE];
        for (int i = 0; i < PUSH_ARRAY_SIZE; i++) {
            pushArray[i] = i;
        }

        // 2. Setup for pop/peek/discard operations (pre-populated stack)
        stackForPopPeekDiscard = new LongStack(INITIAL_STACK_SIZE);
        for (int i = 0; i < INITIAL_STACK_SIZE; i++) {
            stackForPopPeekDiscard.push(i);
        }

        // 3. Setup for pushAll(Iterable<LongCursor>)
        cursorListForPushAll = new ArrayList<>(INITIAL_STACK_SIZE / 2);
        for (int i = 0; i < INITIAL_STACK_SIZE / 2; i++) {
            // Fix: LongCursor requires no arguments based on compilation error
            LongCursor cursor = new LongCursor();
            cursorListForPushAll.add(cursor);
        }
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Reset the stack for operations that require a known, consistent state
        // (e.g., pop, peek, discard) to prevent timing skew due to state accumulation.
        // We clone the pre-populated stack state for each invocation.
        stackForPopPeekDiscard = (LongStack) stackForPopPeekDiscard.clone();
    }

    // --- Benchmarks ---

    // 1. push(long e1)
    @Benchmark
    public void pushSingleElement(Blackhole bh) {
        stackForPush.push(1L);
        bh.consume(stackForPush.size());
    }

    // 2. push(long e1, long e2)
    @Benchmark
    public void pushTwoElements(Blackhole bh) {
        stackForPush.push(1L, 2L);
        bh.consume(stackForPush.size());
    }

    // 3. push(long e1, long e2, long e3)
    @Benchmark
    public void pushThreeElements(Blackhole bh) {
        stackForPush.push(1L, 2L, 3L);
        bh.consume(stackForPush.size());
    }

    // 4. push(long e1, long e2, long e3, long e4)
    @Benchmark
    public void pushFourElements(Blackhole bh) {
        stackForPush.push(1L, 2L, 3L, 4L);
        bh.consume(stackForPush.size());
    }

    // 5. push(long[] elements, int start, int len)
    @Benchmark
    public void pushArrayRange(Blackhole bh) {
        // Push a subset of the pre-built array
        stackForPush.push(pushArray, 0, 100);
        bh.consume(stackForPush.size());
    }

    // 6. pushAll(LongContainer container) - REMOVED due to abstract LongContainer
    // @Benchmark
    // public void pushAllFromContainer(Blackhole bh) {
    //     // This benchmark was removed as LongContainer is abstract and cannot be instantiated.
    // }

    // 7. pushAll(Iterable<LongCursor> iterable)
    @Benchmark
    public void pushAllFromIterable(Blackhole bh) {
        int added = stackForPush.pushAll(cursorListForPushAll);
        bh.consume(added);
    }

    // 8. discard(int count)
    @Benchmark
    public void discardCount(Blackhole bh) {
        // Discard a small number of elements from the pre-populated stack
        stackForPopPeekDiscard.discard(10);
        bh.consume(stackForPopPeekDiscard.size());
    }

    // 9. discard()
    @Benchmark
    public void discardTop(Blackhole bh) {
        stackForPopPeekDiscard.discard();
        bh.consume(stackForPopPeekDiscard.size());
    }

    // 10. pop()
    @Benchmark
    public long popElement(Blackhole bh) {
        long result = stackForPopPeekDiscard.pop();
        bh.consume(result);
        return result;
    }

    // 11. peek()
    @Benchmark
    public long peekElement(Blackhole bh) {
        long result = stackForPopPeekDiscard.peek();
        bh.consume(result);
        return result;
    }

    // 12. Static factory method: from(long... elements)
    @Benchmark
    public LongStack fromStaticFactory(Blackhole bh) {
        // Use a small, fixed set of elements for the factory call
        long[] elements = {1L, 2L, 3L, 4L, 5L};
        LongStack stack = LongStack.from(elements);
        bh.consume(stack.size());
        return stack;
    }
}

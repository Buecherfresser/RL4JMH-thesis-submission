package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.IntStack;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.cursors.IntCursor;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class IntStackBenchmark {

    private IntStack stack;
    private int[] initialData;
    private IntArrayList initialContainer;
    private static final int INITIAL_SIZE = 100;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Setup initial data array
        initialData = new int[INITIAL_SIZE];
        Random random = new Random(42);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            initialData[i] = random.nextInt();
        }

        // 2. Setup initial stack instance
        stack = new IntStack(INITIAL_SIZE);
        
        // 3. Populate the stack to a known state
        for (int i = 0; i < INITIAL_SIZE; i++) {
            stack.push(initialData[i]);
        }

        // 4. Setup a companion container (IntArrayList implements IntContainer)
        initialContainer = new IntArrayList(INITIAL_SIZE);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            initialContainer.add(initialData[i]);
        }
    }

    /**
     * Resets the stack to the initial state (filled with INITIAL_SIZE elements)
     * before each invocation to ensure consistent measurement.
     */
    @Setup(Level.Invocation)
    public void setupInvocation() {
        // Clear and refill the stack
        stack.clear();
        for (int i = 0; i < INITIAL_SIZE; i++) {
            stack.push(initialData[i]);
        }
    }

    // --- Push Operations ---

    @Benchmark
    public void pushSingleElement(Blackhole bh) {
        // State is reset by setupInvocation()
        stack.push(1);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushMultipleElements(Blackhole bh) {
        // State is reset by setupInvocation()
        stack.push(1, 2); // Using 2 elements for a smaller, representative test
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushArrayRange(Blackhole bh) {
        // State is reset by setupInvocation()
        stack.push(initialData, 0, 10);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushAllFromContainer(Blackhole bh) {
        // State is reset by setupInvocation()
        // We use the pre-filled initialContainer
        stack.pushAll(initialContainer);
        bh.consume(stack.size());
    }

    // --- Pop/Peek Operations ---

    @Benchmark
    public void popElement(Blackhole bh) {
        // State is reset by setupInvocation()
        int popped = stack.pop();
        bh.consume(popped);
        bh.consume(stack.size());
    }

    @Benchmark
    public void peekElement(Blackhole bh) {
        // State is reset by setupInvocation()
        int peeked = stack.peek();
        bh.consume(peeked);
    }

    // --- Discard Operations ---

    @Benchmark
    public void discardSingleElement(Blackhole bh) {
        // State is reset by setupInvocation()
        stack.discard();
        bh.consume(stack.size());
    }

    @Benchmark
    public void discardMultipleElements(Blackhole bh) {
        // State is reset by setupInvocation()
        stack.discard(5);
        bh.consume(stack.size());
    }
}

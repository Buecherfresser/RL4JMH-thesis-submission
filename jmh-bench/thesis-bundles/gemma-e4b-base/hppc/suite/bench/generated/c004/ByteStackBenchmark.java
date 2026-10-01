package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ByteStack;
import com.carrotsearch.hppc.ByteContainer;
import com.carrotsearch.hppc.cursors.ByteCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteStackBenchmark {

    private ByteStack[] stackPool;
    private int poolIndex = 0;
    private static final int POOL_SIZE = 10;
    private static final int BULK_PUSH_SIZE = 100;

    @Setup
    public void setup() {
        stackPool = new ByteStack[POOL_SIZE];
        for (int i = 0; i < POOL_SIZE; i++) {
            // Initialize stacks with a reasonable capacity
            stackPool[i] = new ByteStack(100);
        }
    }

    /**
     * Gets a fresh stack instance from the pool and resets its state.
     * Since ByteStack extends ByteArrayList, we assume a simple reset mechanism
     * or rely on the pool initialization if the operation is destructive.
     * For safety, we clear the stack before use.
     */
    private ByteStack getFreshStack() {
        ByteStack stack = stackPool[poolIndex];
        poolIndex = (poolIndex + 1) % POOL_SIZE;
        stack.clear();
        return stack;
    }

    // --- Push Operations ---

    @Benchmark
    public void pushOneByte(Blackhole bh) {
        ByteStack stack = getFreshStack();
        stack.push((byte) 0xAA);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushTwoBytes(Blackhole bh) {
        ByteStack stack = getFreshStack();
        stack.push((byte) 0xAA, (byte) 0xBB);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushThreeBytes(Blackhole bh) {
        ByteStack stack = getFreshStack();
        stack.push((byte) 0xAA, (byte) 0xBB, (byte) 0xCC);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushFourBytes(Blackhole bh) {
        ByteStack stack = getFreshStack();
        stack.push((byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushBulkArray(Blackhole bh) {
        ByteStack stack = getFreshStack();
        byte[] elements = new byte[BULK_PUSH_SIZE];
        for (int i = 0; i < BULK_PUSH_SIZE; i++) {
            elements[i] = (byte) (i % 256);
        }
        stack.push(elements, 0, BULK_PUSH_SIZE);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushVararg(Blackhole bh) {
        ByteStack stack = getFreshStack();
        byte[] elements = new byte[10];
        for (int i = 0; i < 10; i++) {
            elements[i] = (byte) i;
        }
        // Using the vararg signature
        stack.push(elements);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushAllFromContainer(Blackhole bh) {
        // Setup: Create a source container
        ByteStack sourceStack = new ByteStack(50);
        for (int i = 0; i < 50; i++) {
            sourceStack.push((byte) i);
        }

        // Benchmark: Push all from source
        ByteStack targetStack = getFreshStack();
        int added = targetStack.pushAll(sourceStack);
        bh.consume(added);
    }

    // --- Stack Operations ---

    @Benchmark
    public void popTopElement(Blackhole bh) {
        ByteStack stack = getFreshStack();
        // Pre-fill stack to ensure pop is valid
        for (int i = 0; i < 10; i++) {
            stack.push((byte) i);
        }
        byte result = stack.pop();
        bh.consume(result);
    }

    @Benchmark
    public void peekTopElement(Blackhole bh) {
        ByteStack stack = getFreshStack();
        // Pre-fill stack to ensure peek is valid
        for (int i = 0; i < 10; i++) {
            stack.push((byte) i);
        }
        byte result = stack.peek();
        bh.consume(result);
    }

    @Benchmark
    public void discardTopElement(Blackhole bh) {
        ByteStack stack = getFreshStack();
        // Pre-fill stack to ensure discard is valid
        for (int i = 0; i < 10; i++) {
            stack.push((byte) i);
        }
        stack.discard();
        bh.consume(stack.size());
    }

    @Benchmark
    public void discardMultipleElements(Blackhole bh) {
        ByteStack stack = getFreshStack();
        // Pre-fill stack to ensure discard is valid
        for (int i = 0; i < 20; i++) {
            stack.push((byte) i);
        }
        int count = 5;
        stack.discard(count);
        bh.consume(stack.size());
    }

    // --- Static Factory ---

    @Benchmark
    public void fromStaticFactory(Blackhole bh) {
        byte[] elements = new byte[10];
        for (int i = 0; i < 10; i++) {
            elements[i] = (byte) i;
        }
        // Using the vararg signature for the static factory
        ByteStack stack = ByteStack.from(elements);
        bh.consume(stack.size());
    }
}

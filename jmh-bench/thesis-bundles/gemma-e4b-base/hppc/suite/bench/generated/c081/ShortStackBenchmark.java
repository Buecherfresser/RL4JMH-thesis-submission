package bench.generated.c081;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ShortStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortStackBenchmark {

    private ShortStack stack;
    private short[] inputShorts;
    private ShortStack container;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize a fresh stack for each benchmark run
        stack = new ShortStack();

        // Setup input data for batch operations
        int inputSize = 100;
        inputShorts = new short[inputSize];
        for (int i = 0; i < inputSize; i++) {
            inputShorts[i] = (short) i;
        }

        // Setup a companion container for pushAll tests
        // We use ShortStack here to ensure the push(short) method is available for setup
        container = new ShortStack(inputShorts.length);
        for (short s : inputShorts) {
            container.push(s);
        }
    }

    @Benchmark
    public void pushSingleElement(Blackhole bh) {
        stack.push((short) 1);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushTwoElements(Blackhole bh) {
        stack.push((short) 1, (short) 2);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushFourElements(Blackhole bh) {
        stack.push((short) 1, (short) 2, (short) 3, (short) 4);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushVarargElements(Blackhole bh) {
        short[] elements = new short[]{1, 2, 3, 4, 5};
        stack.push(elements);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushBatchElements(Blackhole bh) {
        int len = 10;
        stack.push(inputShorts, 0, len);
        bh.consume(stack.size());
    }

    @Benchmark
    public void pushAllFromContainer(Blackhole bh) {
        // Use the pre-filled container
        int added = stack.pushAll(container);
        bh.consume(added);
    }

    @Benchmark
    public void popTopElement(Blackhole bh) {
        // Ensure stack is not empty before popping
        stack.push((short) 1);
        short popped = stack.pop();
        bh.consume(popped);
    }

    @Benchmark
    public void peekTopElement(Blackhole bh) {
        // Ensure stack is not empty before peeking
        stack.push((short) 1);
        short peeked = stack.peek();
        bh.consume(peeked);
    }

    @Benchmark
    public void discardTopElement(Blackhole bh) {
        // Ensure stack is not empty before discarding
        stack.push((short) 1);
        stack.discard();
        bh.consume(stack.size());
    }

    @Benchmark
    public void discardMultipleElements(Blackhole bh) {
        // Ensure stack has enough elements
        for (int i = 0; i < 5; i++) {
            stack.push((short) 1);
        }
        stack.discard(3);
        bh.consume(stack.size());
    }
}

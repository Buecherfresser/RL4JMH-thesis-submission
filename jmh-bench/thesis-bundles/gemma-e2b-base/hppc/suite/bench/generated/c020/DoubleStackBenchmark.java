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

    private DoubleStack stack;
    private final int INITIAL_SIZE = 1000;
    private double[] initialData;

    @Setup
    public void setup() {
        // Initialize the stack with a fixed number of elements
        stack = new DoubleStack(INITIAL_SIZE);
        initialData = new double[INITIAL_SIZE];
        for (int i = 0; i < INITIAL_SIZE; i++) {
            initialData[i] = i * 1.1;
            stack.push(initialData[i]);
        }
    }

    @Benchmark
    public void pushSingleElement(Blackhole bh) {
        stack.push(99.9);
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
    public void pushArray(Blackhole bh) {
        double[] elements = {5.0, 6.0, 7.0, 8.0};
        stack.push(elements, 0, elements.length);
        bh.consume(stack);
    }

    @Benchmark
    public void popElement(Blackhole bh) {
        double popped = stack.pop();
        bh.consume(popped);
    }

    @Benchmark
    public void peekElement(Blackhole bh) {
        double peeked = stack.peek();
        bh.consume(peeked);
    }

    @Benchmark
    public void discardSingle(Blackhole bh) {
        stack.discard();
        bh.consume(stack);
    }

    @Benchmark
    public void discardMultiple(Blackhole bh) {
        stack.discard(10);
        bh.consume(stack);
    }

    @Benchmark
    public void fromStatic(Blackhole bh) {
        DoubleStack newStack = DoubleStack.from(10.0, 20.0, 30.0);
        bh.consume(newStack);
    }
}

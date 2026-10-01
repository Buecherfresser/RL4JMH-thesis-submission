package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.FloatStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FloatStackBenchmark {

    private FloatStack stack;
    private float[] testElements;

    @Setup
    public void setup() {
        // Setup a moderately sized stack for testing
        int initialCapacity = 1000;
        stack = new FloatStack(initialCapacity);

        // Create a set of test floats
        testElements = new float[initialCapacity];
        for (int i = 0; i < initialCapacity; i++) {
            testElements[i] = i * 0.1f;
        }
    }

    @Benchmark
    public void pushSingleElement(Blackhole bh) {
        stack.push(1.0f);
        bh.consume(stack);
    }

    @Benchmark
    public void pushTwoElements(Blackhole bh) {
        stack.push(2.0f, 3.0f);
        bh.consume(stack);
    }

    @Benchmark
    public void pushThreeElements(Blackhole bh) {
        stack.push(4.0f, 5.0f, 6.0f);
        bh.consume(stack);
    }

    @Benchmark
    public void pushFourElements(Blackhole bh) {
        stack.push(7.0f, 8.0f, 9.0f, 10.0f);
        bh.consume(stack);
    }

    @Benchmark
    public void pushArray(Blackhole bh) {
        float[] elements = {11.0f, 12.0f, 13.0f, 14.0f};
        stack.push(elements, 0, elements.length);
        bh.consume(stack);
    }

    @Benchmark
    public void pushVarargs(Blackhole bh) {
        stack.push(15.0f, 16.0f, 17.0f);
        bh.consume(stack);
    }

    @Benchmark
    public void pop(Blackhole bh) {
        float poppedValue = stack.pop();
        bh.consume(poppedValue);
    }

    @Benchmark
    public void peek(Blackhole bh) {
        float peekedValue = stack.peek();
        bh.consume(peekedValue);
    }

    @Benchmark
    public void discardTop(Blackhole bh) {
        stack.discard();
        bh.consume(stack);
    }

    @Benchmark
    public void discardCount(Blackhole bh) {
        stack.discard(5);
        bh.consume(stack);
    }

    @Benchmark
    public void staticFactoryFrom(Blackhole bh) {
        FloatStack newStack = FloatStack.from(1.0f, 2.0f, 3.0f);
        bh.consume(newStack);
    }
}

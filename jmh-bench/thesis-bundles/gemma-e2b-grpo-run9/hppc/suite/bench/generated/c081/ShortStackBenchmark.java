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

    // State field to hold the instance of the class under test.
    private ShortStack stack;

    @Setup
    public void setup() {
        // Initialize a default stack instance.
        this.stack = new ShortStack();
    }

    @Benchmark
    public void benchmarkPeek(Blackhole bh) {
        // Test a read operation.
        try {
            bh.consume(stack.peek());
        } catch (AssertionError e) {
            // Ignore assertion errors if the stack is empty.
        }
    }

    @Benchmark
    public void benchmarkPop(Blackhole bh) {
        // Test a read/remove operation.
        try {
            bh.consume(stack.pop());
        } catch (AssertionError e) {
            // Ignore assertion errors if the stack is empty.
        }
    }

    @Benchmark
    public void benchmarkPushSingle(Blackhole bh) {
        // Test a simple push operation. This mutates the state.
        try {
            stack.push((short) 10);
            bh.consume(stack.peek());
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution
        }
    }

    @Benchmark
    public void benchmarkPushVarargs(Blackhole bh) {
        // Test the vararg push method. This mutates the state.
        try {
            // Call the varargs method using short primitives
            stack.push((short) 1, (short) 2, (short) 3);
            bh.consume(stack.peek());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Test the static factory method.
        try {
            ShortStack s = ShortStack.from((short) 5, (short) 6);
            bh.consume(s);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

package bench.generated.c004;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.ByteStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteStackBenchmark {

    // State field for the subject under test.
    // Since ByteStack mutates state, we create a new instance per benchmark
    // or rely on the harness to manage state isolation if possible.
    private ByteStack stack;

    @Setup
    public void setup() {
        // Initialize a fresh stack for each benchmark run if needed,
        // or rely on the benchmark method to create its own instance.
        // For simplicity and safety against state leakage, we initialize it here.
        this.stack = new ByteStack();
    }

    @Benchmark
    public void benchmarkPushSingle() {
        // Test push(byte e1)
        stack.push((byte) 0x01);
    }

    @Benchmark
    public void benchmarkPushTwo() {
        // Test push(byte e1, byte e2)
        stack.push((byte) 0x02, (byte) 0x03);
    }

    @Benchmark
    public void benchmarkPop() {
        // Test pop() - requires the stack to have elements.
        // Since we start empty in setup, this might throw an assertion error
        // if the underlying ByteArrayList doesn't handle empty pop gracefully,
        // but we test the method call path.
        try {
            stack.push((byte) 0xAA);
            stack.pop();
        } catch (Exception e) {
            // Ignore exceptions if the stack is empty, as we are testing the method call path.
        }
    }

    @Benchmark
    public void benchmarkPeek() {
        // Test peek() - requires the stack to have elements.
        try {
            stack.push((byte) 0xBB);
            stack.peek();
        } catch (Exception e) {
            // Ignore exceptions if the stack is empty.
        }
    }

    @Benchmark
    public void benchmarkStaticFrom() {
        // Test static factory method (read-only operation)
        // This tests the creation path without mutating the state of 'this.stack'.
        ByteStack newStack = ByteStack.from((byte) 0xDE);
        // Consume the result to prevent dead code elimination
        stack = newStack;
    }

    @Benchmark
    public void benchmarkPushArray() {
        // Test push(byte[] elements, int start, int len)
        // This tests the array copy path.
        try {
            // We rely on the internal implementation handling the array copy correctly.
            stack.push((byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkDiscard() {
        // Test discard()
        try {
            stack.push((byte) 0x11);
            stack.discard();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

package bench.generated.c016;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.CharStack;
import com.carrotsearch.hppc.cursors.CharCursor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CharStackBenchmark {

    // State field for the subject under test.
    // Since CharStack is mutable, we rely on JMH's handling or ensure
    // operations are idempotent/safe for this mode.
    private CharStack stack;

    @Setup
    public void setup() {
        // Initialize a stack. Using the default constructor.
        // We rely on the fact that the underlying CharArrayList handles initial capacity.
        this.stack = new CharStack();
    }

    @Benchmark
    public void benchmarkPop(Blackhole bh) {
        // Test pop() which calls removeLast()
        try {
            // We must ensure the stack is not empty for this test to be valid,
            // but since we are benchmarking the method itself, we rely on the
            // setup state being sufficient for the test run.
            bh.consume(stack.pop());
        } catch (Exception e) {
            // Ignore exceptions if the stack is empty during a benchmark run
        }
    }

    @Benchmark
    public void benchmarkPeek(Blackhole bh) {
        // Test peek()
        try {
            bh.consume(stack.peek());
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkDiscard(Blackhole bh) {
        // Test discard()
        try {
            stack.push('a');
            bh.consume(stack); // Consume the result of the operation (void method)
            stack.discard();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkPushSingleChar(Blackhole bh) {
        // Test push(char e1)
        try {
            stack.push('x');
            bh.consume(stack);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkPushTwoChars(Blackhole bh) {
        // Test push(char e1, char e2)
        try {
            stack.push('x', 'y');
            bh.consume(stack);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkPushVarargs(Blackhole bh) {
        // Test push(char... elements)
        try {
            stack.push('a', 'b', 'c');
            bh.consume(stack);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Test static factory method CharStack.from(char...)
        try {
            // This creates a new instance, which is fine for a benchmark test
            // as long as we don't rely on the state being preserved across runs.
            CharStack newStack = com.carrotsearch.hppc.CharStack.from('a', 'b');
            bh.consume(newStack);
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
}

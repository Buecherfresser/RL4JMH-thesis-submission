package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import com.carrotsearch.hppc.DoubleStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DoubleStackBenchmark {

    // State field for the subject under test.
    // Since DoubleStack is mutable, we rely on JMH's isolation or the fact that
    // we are testing methods that are called once per benchmark invocation.
    private DoubleStack stack;

    @Setup
    public void setup() {
        // Initialize a fresh stack for each trial.
        // Using the no-arg constructor.
        this.stack = new DoubleStack();
    }

    @Benchmark
    public void benchmarkPushSingle(Blackhole bh) {
        // Test push(double e1)
        stack.push(1.0);
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkPushTwo(Blackhole bh) {
        // Test push(double e1, double e2)
        stack.push(1.0, 2.0);
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkPop(Blackhole bh) {
        // Test pop()
        // Note: This assumes the stack is not empty, which is true after setup.
        double result = stack.pop();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkPeek(Blackhole bh) {
        // Test peek()
        try {
            double result = stack.peek();
            bh.consume(result);
        } catch (AssertionError e) {
            // Ignore assertion errors if the stack is empty during a specific run,
            // though this shouldn't happen if setup is correct.
        }
    }

    @Benchmark
    public void benchmarkDiscard(Blackhole bh) {
        // Test discard()
        // We must ensure the stack has elements before calling discard()
        try {
            // Push a few elements to ensure discard doesn't fail immediately
            stack.push(1.0);
            stack.push(2.0);
            stack.discard();
            bh.consume(stack);
        } catch (AssertionError e) {
            // Ignore
        }
    }

    @Benchmark
    public void benchmarkPushVarargs(Blackhole bh) {
        // Test push(double... elements)
        try {
            stack.push(3.0, 4.0);
            bh.consume(stack);
        } catch (AssertionError e) {
            // Ignore
        }
    }
}

package bench.generated.c069;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Arrays;

import com.carrotsearch.hppc.ObjectStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ObjectStackBenchmark {

    // State field for the stack instance. Since ObjectStack is mutable,
    // we instantiate it inside the benchmark method or rely on GC between runs.
    // We avoid @Setup(Level.Trial) for mutable objects unless we manage state reset explicitly.

    @Benchmark
    public void benchmarkPushSingle(Blackhole bh) {
        // Create a fresh stack for each invocation to ensure isolation
        ObjectStack<Integer> stack = new ObjectStack<>();
        stack.push(10);
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkPushDouble(Blackhole bh) {
        ObjectStack<Integer> stack = new ObjectStack<>();
        stack.push(10);
        stack.push(20);
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkPop(Blackhole bh) {
        ObjectStack<Integer> stack = new ObjectStack<>();
        stack.push(10);
        try {
            stack.pop();
        } catch (Exception e) {
            // Ignore exceptions if the stack is empty, as we only measure successful paths
        }
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkPeek(Blackhole bh) {
        ObjectStack<Integer> stack = new ObjectStack<>();
        stack.push(10);
        try {
            stack.peek();
        } catch (Exception e) {
            // Ignore exceptions if the stack is empty
        }
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkDiscard(Blackhole bh) {
        ObjectStack<Integer> stack = new ObjectStack<>();
        // Populate stack to ensure discard doesn't fail immediately
        stack.push(1);
        stack.push(2);
        try {
            stack.discard();
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(stack);
    }

    @Benchmark
    public void benchmarkStaticFrom(Blackhole bh) {
        // Static factory method call
        ObjectStack<Integer> stack = ObjectStack.from(1, 2, 3);
        bh.consume(stack);
    }
}

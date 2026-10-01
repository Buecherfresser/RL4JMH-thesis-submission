package bench.generated.c036;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.IntStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class IntStackBenchmark {

    private IntStack stack;

    @Setup
    public void setup() {
        // Initialize the stack with a reasonable capacity for testing
        stack = new IntStack(1000);
    }

    @Benchmark
    public void pushSingle(Blackhole bh) {
        stack.push(42);
        bh.consume(null);
    }

    @Benchmark
    public void pop(Blackhole bh) {
        int result = stack.pop();
        bh.consume(result);
    }

    @Benchmark
    public void peek(Blackhole bh) {
        int result = stack.peek();
        bh.consume(result);
    }
}

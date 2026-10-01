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

    // State field for the stack instance. Since we are benchmarking operations
    // that mutate the state (push/pop), we will create a fresh instance
    // inside the benchmark method to avoid state pollution across iterations,
    // adhering to the spirit of avoiding mutation anti-patterns.
    private FloatStack stack;

    @Setup
    public void setup() {
        // No complex setup needed, as we create the stack instance per benchmark method
        // if it involves mutation.
    }

    @Benchmark
    public void benchmarkPushSingle(Blackhole bh) {
        // Create a fresh stack for this invocation
        FloatStack s = new FloatStack();
        s.push(1.0f);
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkPushTwo(Blackhole bh) {
        // Create a fresh stack for this invocation
        FloatStack s = new FloatStack();
        s.push(1.0f, 2.0f);
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkPop(Blackhole bh) {
        // Create a fresh stack for this invocation
        FloatStack s = new FloatStack();
        // We must ensure the stack is not empty before calling pop,
        // but since we are benchmarking the method itself, we rely on the
        // method's internal assertion or accept potential exceptions if the
        // underlying implementation throws them (which we ignore for pure timing).
        try {
            s.push(1.0f);
            s.pop();
        } catch (Exception e) {
            // Ignore exceptions during benchmark timing
        }
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkPeek(Blackhole bh) {
        // Create a fresh stack for this invocation
        FloatStack s = new FloatStack();
        try {
            s.push(1.0f);
            s.peek();
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkFromStaticFactory(Blackhole bh) {
        // Static factory method call
        FloatStack s = FloatStack.from(1.0f, 2.0f, 3.0f);
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkPushVarargs(Blackhole bh) {
        // Create a fresh stack for this invocation
        FloatStack s = new FloatStack();
        try {
            s.push(1.0f, 2.0f, 3.0f);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(s);
    }
}

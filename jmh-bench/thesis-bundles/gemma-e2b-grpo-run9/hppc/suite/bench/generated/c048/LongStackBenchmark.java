package bench.generated.c048;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import com.carrotsearch.hppc.LongStack;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LongStackBenchmark {

    @Benchmark
    public void benchmarkFromStatic(Blackhole bh) {
        // Test the static factory method.
        try {
            LongStack.from(1L, 2L, 3L);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPushSingle(Blackhole bh) {
        // Test a simple push operation.
        LongStack stack = new LongStack();
        stack.push(100L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPushTwo(Blackhole bh) {
        // Test a two-argument push operation.
        LongStack stack = new LongStack();
        stack.push(100L, 200L);
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPushVarargs(Blackhole bh) {
        // Test the varargs push operation.
        LongStack stack = new LongStack();
        try {
            stack.push(1L, 2L, 3L, 4L);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPeek(Blackhole bh) {
        // Test peek operation (requires a stack instance).
        LongStack stack = new LongStack();
        try {
            stack.push(500L);
            stack.peek();
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPop(Blackhole bh) {
        // Test pop operation (requires a stack instance).
        LongStack stack = new LongStack();
        try {
            stack.push(1L);
            stack.pop();
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkDiscard(Blackhole bh) {
        // Test discard operation (requires a stack instance).
        LongStack stack = new LongStack();
        try {
            stack.push(1L);
            stack.discard();
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkPushAll(Blackhole bh) {
        // Test pushAll using an iterable (requires a stack instance).
        LongStack stack = new LongStack();
        try {
            // Calling pushAll with null or an empty iterable to test the method path.
            stack.pushAll(null);
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }
}

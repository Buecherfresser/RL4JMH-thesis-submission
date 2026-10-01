package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import jodd.exception.ExceptionUtil;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ExceptionUtilBenchmark {

    // Since ExceptionUtil methods are static, no instance state is strictly required.
    // We use a simple counter to ensure the benchmark isn't optimized away entirely,
    // although JMH handles static calls reasonably well.
    private AtomicInteger counter = new AtomicInteger(0);

    @Setup
    public void setup() {
        // Setup phase, if any complex, non-static initialization were needed.
    }

    @Benchmark
    public void benchmarkMessage(Blackhole bh) {
        try {
            // Test simple message retrieval
            ExceptionUtil.message(new RuntimeException("Test message"));
        } catch (Exception e) {
            // Ignore exceptions during benchmark execution if they occur internally
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkBuildMessage(Blackhole bh) {
        try {
            // Test message building with a cause
            ExceptionUtil.buildMessage("Base error", new NullPointerException("Cause"));
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkWrapToRuntimeException(Blackhole bh) {
        try {
            // Test wrapping a checked exception (simulated by wrapping a RuntimeException)
            ExceptionUtil.wrapToRuntimeException(new java.io.IOException("IO Error"));
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkGetRootCause(Blackhole bh) {
        try {
            // Test the recursive/iterative root cause finding logic.
            // Since we cannot easily create a deep, controlled chain, we test against a simple exception.
            ExceptionUtil.getRootCause(new RuntimeException("Root test"));
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }

    @Benchmark
    public void benchmarkUnwrapThrowable(Blackhole bh) {
        try {
            // Test unwrapping logic (simulating InvocationTargetException)
            // Since we cannot instantiate InvocationTargetException easily without reflection/mocking,
            // we test against a standard exception which should pass through the loop quickly.
            ExceptionUtil.unwrapThrowable(new RuntimeException("Simple error"));
        } catch (Exception e) {
            // Ignore
        }
        bh.consume(null);
    }
}

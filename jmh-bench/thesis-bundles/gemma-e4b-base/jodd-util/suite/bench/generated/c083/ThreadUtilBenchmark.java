package bench.generated.c083;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.ThreadUtil;
import java.lang.Thread;
import java.lang.Object;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ThreadUtilBenchmark {

    private Object syncObject;
    private Thread testThread;

    @Setup
    public void setup() {
        // Setup shared object for wait/notify operations
        this.syncObject = new Object();

        // Setup a thread for join operations
        this.testThread = new Thread(() -> {
            // Keep the thread alive briefly so join operations have something to wait for
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        this.testThread.start();
    }

    @TearDown
    public void tearDown() {
        // Clean up the thread
        if (testThread != null) {
            testThread.interrupt();
            try {
                testThread.join(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // --- Sleep Benchmarks ---

    @Benchmark
    public void sleep_ms(Blackhole bh) {
        // Use a minimal sleep time to keep the benchmark duration reasonable
        ThreadUtil.sleep(1);
        bh.consume(null);
    }

    @Benchmark
    public void sleep_noArgs(Blackhole bh) {
        ThreadUtil.sleep();
        bh.consume(null);
    }

    // --- Synchronization Benchmarks (Wait/Notify) ---

    @Benchmark
    public void wait_singleArg(Blackhole bh) {
        // The method handles synchronization internally
        ThreadUtil.wait(syncObject);
        bh.consume(null);
    }

    @Benchmark
    public void wait_withTimeout(Blackhole bh) {
        // The method handles synchronization internally
        ThreadUtil.wait(syncObject, 1);
        bh.consume(null);
    }

    @Benchmark
    public void notify_singleArg(Blackhole bh) {
        // The method handles synchronization internally
        ThreadUtil.notify(syncObject);
        bh.consume(null);
    }

    @Benchmark
    public void notifyAll_singleArg(Blackhole bh) {
        // The method handles synchronization internally
        ThreadUtil.notifyAll(syncObject);
        bh.consume(null);
    }

    // --- Join Benchmarks ---

    @Benchmark
    public void join_singleArg(Blackhole bh) {
        // Note: This will block the benchmark thread until testThread finishes (or times out)
        ThreadUtil.join(testThread);
        bh.consume(null);
    }

    @Benchmark
    public void join_withTimeout(Blackhole bh) {
        // Note: This will block the benchmark thread until testThread finishes or times out
        ThreadUtil.join(testThread, 1);
        bh.consume(null);
    }

    @Benchmark
    public void join_withTimeoutAndNanos(Blackhole bh) {
        // Note: This will block the benchmark thread until testThread finishes or times out
        ThreadUtil.join(testThread, 1, 0);
        bh.consume(null);
    }
}

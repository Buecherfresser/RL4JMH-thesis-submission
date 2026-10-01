package bench.generated.c083;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.util.ThreadUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ThreadUtilBenchmark {

    // State for synchronization methods
    private Object syncObject;
    private Thread syncThread;

    // State for join methods
    private Thread joinThread;

    // State for sleep methods
    private long sleepMs;

    @Setup
    public void setup() {
        // Setup for synchronization tests
        syncObject = new Object();
        syncThread = new Thread(() -> {}); // Dummy thread
        
        // Setup for join tests
        joinThread = new Thread(() -> {}); // Dummy thread

        // Setup for sleep tests
        sleepMs = 10; // Small sleep time for testing
    }

    // --- Sleep Benchmarks ---

    @Benchmark
    public void sleepMs(Blackhole bh) {
        ThreadUtil.sleep(sleepMs);
        bh.consume(null);
    }

    @Benchmark
    public void sleepForever(Blackhole bh) {
        ThreadUtil.sleep();
        bh.consume(null);
    }

    // --- Synchronization Benchmarks ---

    @Benchmark
    public void wait(Blackhole bh) {
        ThreadUtil.wait(syncObject);
        bh.consume(null);
    }

    @Benchmark
    public void waitWithTimeout(Blackhole bh) {
        long timeout = 100;
        ThreadUtil.wait(syncObject, timeout);
        bh.consume(null);
    }

    @Benchmark
    public void notify(Blackhole bh) {
        ThreadUtil.notify(syncObject);
        bh.consume(null);
    }

    @Benchmark
    public void notifyAll(Blackhole bh) {
        ThreadUtil.notifyAll(syncObject);
        bh.consume(null);
    }

    // --- Join Benchmarks ---

    @Benchmark
    public void join(Blackhole bh) {
        // Use a dummy thread for the join operation
        ThreadUtil.join(joinThread);
        bh.consume(null);
    }

    @Benchmark
    public void joinWithMillis(Blackhole bh) {
        long millis = 50;
        ThreadUtil.join(joinThread, millis);
        bh.consume(null);
    }

    @Benchmark
    public void joinWithMillisAndNanos(Blackhole bh) {
        long millis = 100;
        int nanos = 500_000;
        ThreadUtil.join(joinThread, millis, nanos);
        bh.consume(null);
    }
}

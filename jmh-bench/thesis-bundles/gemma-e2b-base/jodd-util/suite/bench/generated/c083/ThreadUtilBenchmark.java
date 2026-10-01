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

    // State for synchronization tests
    private Object syncObject;
    private Thread targetThread;

    // State for sleep tests
    private long sleepDurationMs;

    // State for join tests
    private Thread joinTargetThread;
    private long joinTimeoutMs;
    private long joinMillis;
    private int joinNanos;

    @Setup
    public void setup() {
        // Setup synchronization object
        syncObject = new Object();

        // Setup a dummy thread for join operations
        joinTargetThread = new Thread(() -> {});
    }

    // --- Sleep Benchmarks ---

    @Benchmark
    public void sleep_ms(Blackhole bh) {
        ThreadUtil.sleep(sleepDurationMs);
        bh.consume(null);
    }

    @Benchmark
    public void sleep_forever(Blackhole bh) {
        ThreadUtil.sleep();
        bh.consume(null);
    }

    // --- Wait/Notify Benchmarks ---

    @Benchmark
    public void wait_noArg(Blackhole bh) {
        ThreadUtil.wait(syncObject);
        bh.consume(null);
    }

    @Benchmark
    public void wait_withTimeout(Blackhole bh) {
        ThreadUtil.wait(syncObject, 100);
        bh.consume(null);
    }

    @Benchmark
    public void notify_single(Blackhole bh) {
        ThreadUtil.notify(syncObject);
        bh.consume(null);
    }

    @Benchmark
    public void notify_all(Blackhole bh) {
        ThreadUtil.notifyAll(syncObject);
        bh.consume(null);
    }

    // --- Join Benchmarks ---

    @Benchmark
    public void join_noArg(Blackhole bh) {
        // Joining the thread created in setup (which is currently idle)
        ThreadUtil.join(joinTargetThread);
        bh.consume(null);
    }

    @Benchmark
    public void join_withMillis(Blackhole bh) {
        ThreadUtil.join(joinTargetThread, joinMillis);
        bh.consume(null);
    }

    @Benchmark
    public void join_withMillisAndNanos(Blackhole bh) {
        ThreadUtil.join(joinTargetThread, joinMillis, joinNanos);
        bh.consume(null);
    }
}

package bench.generated.c083;

import jodd.util.ThreadUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ThreadUtilBenchmark {

    private Object lock;
    private Thread completedThread;

    @Setup(Level.Trial)
    public void setup() throws InterruptedException {
        lock = new Object();
        completedThread = new Thread(() -> {});
        completedThread.start();
        completedThread.join();
    }

    @Benchmark
    public void sleepZero(Blackhole bh) {
        ThreadUtil.sleep(0);
        bh.consume(lock);
    }

    @Benchmark
    public void notifyLock(Blackhole bh) {
        ThreadUtil.notify(lock);
        bh.consume(lock);
    }

    @Benchmark
    public void notifyAllLock(Blackhole bh) {
        ThreadUtil.notifyAll(lock);
        bh.consume(lock);
    }

    @Benchmark
    public void joinCompleted(Blackhole bh) {
        ThreadUtil.join(completedThread);
        bh.consume(completedThread);
    }

    @Benchmark
    public void joinCompletedMillis(Blackhole bh) {
        ThreadUtil.join(completedThread, 0);
        bh.consume(completedThread);
    }

    @Benchmark
    public void joinCompletedNanos(Blackhole bh) {
        ThreadUtil.join(completedThread, 0, 0);
        bh.consume(completedThread);
    }
}

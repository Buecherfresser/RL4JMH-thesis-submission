package bench.generated.c083;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.ThreadUtil;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ThreadUtilBenchmark {

    private Object lockObj;
    private Thread terminatedThread;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() throws Exception {
        lockObj = new Object();
        terminatedThread = new Thread(() -> {
            // no-op
        });
        terminatedThread.start();
        terminatedThread.join(); // ensure it is terminated before benchmarks run
    }

    @Benchmark
    public void benchmarkSleepZero(Blackhole bh) {
        ThreadUtil.sleep(0L);
        bh.consume(1);
    }

    @Benchmark
    public void benchmarkNotify(Blackhole bh) {
        ThreadUtil.notify(lockObj);
        bh.consume(1);
    }

    @Benchmark
    public void benchmarkNotifyAll(Blackhole bh) {
        ThreadUtil.notifyAll(lockObj);
        bh.consume(1);
    }

    @Benchmark
    public void benchmarkJoin(Blackhole bh) {
        ThreadUtil.join(terminatedThread);
        bh.consume(1);
    }

    @Benchmark
    public void benchmarkJoinTimeout(Blackhole bh) {
        ThreadUtil.join(terminatedThread, 0L);
        bh.consume(1);
    }

    @Benchmark
    public void benchmarkJoinTimeoutNanos(Blackhole bh) {
        ThreadUtil.join(terminatedThread, 0L, 0);
        bh.consume(1);
    }
}

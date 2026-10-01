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

    // Since ThreadUtil methods are static, we don't need an instance field.

    @Benchmark
    public void sleep(Blackhole bh) {
        ThreadUtil.sleep(10);
        bh.consume(null);
    }

    @Benchmark
    public void sleepLong(Blackhole bh) {
        ThreadUtil.sleep(1000);
        bh.consume(null);
    }

    @Benchmark
    public void wait(Blackhole bh) {
        // Use a dummy object for synchronization, as the method requires an Object argument.
        ThreadUtil.wait(new Object());
        bh.consume(null);
    }

    @Benchmark
    public void waitTimeout(Blackhole bh) {
        // Use a dummy object and a short timeout.
        ThreadUtil.wait(new Object(), 10);
        bh.consume(null);
    }

    @Benchmark
    public void notify(Blackhole bh) {
        // Use a dummy object.
        ThreadUtil.notify(new Object());
        bh.consume(null);
    }

    @Benchmark
    public void notifyAll(Blackhole bh) {
        // Use a dummy object.
        ThreadUtil.notifyAll(new Object());
        bh.consume(null);
    }

    // Benchmarking join methods requires a Thread object.
    // Since we cannot reliably create and manage real threads for JMH benchmarks
    // without introducing complex setup/teardown that violates the spirit of
    // simple static utility benchmarking, we benchmark the static calls.

    @Benchmark
    public void join(Blackhole bh) {
        // Call the static method with null or a dummy object.
        ThreadUtil.join(null);
        bh.consume(null);
    }

    @Benchmark
    public void joinMillis(Blackhole bh) {
        ThreadUtil.join(null, 100);
        bh.consume(null);
    }

    @Benchmark
    public void joinMillisNanos(Blackhole bh) {
        ThreadUtil.join(null, 100, 1000000);
        bh.consume(null);
    }
}

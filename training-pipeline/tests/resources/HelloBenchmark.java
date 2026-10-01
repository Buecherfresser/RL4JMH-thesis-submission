package jmhgen.testbench;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * A tiny, self-contained JMH benchmark used to exercise the runner end-to-end.
 * Iteration counts are intentionally minimal so the integration test stays fast.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 2, time = 1)
@Fork(1)
public class HelloBenchmark {

    @Benchmark
    public void sumLoop(Blackhole blackhole) {
        int sum = 0;
        for (int i = 0; i < 128; i++) {
            sum += i;
        }
        blackhole.consume(sum);
    }
}

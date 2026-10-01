package bench.generated.c017;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.fastfilter.utils.StringUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringUtilsBenchmark {

    // Since StringUtils methods are static and read-only, no instance state is required.

    @Benchmark
    public void benchmarkGetHex(Blackhole bh) {
        // Call the static method. The result is consumed by Blackhole.
        int result = StringUtils.getHex('a');
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkGetHexZero(Blackhole bh) {
        // Call the static method with a different character.
        int result = StringUtils.getHex('0');
        bh.consume(result);
    }
}

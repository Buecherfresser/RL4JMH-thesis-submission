package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.sevenz.SevenZFileOptions;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SevenZFileOptionsBenchmark {

    // State field to hold the options object, initialized once per benchmark run
    private SevenZFileOptions options;

    @Setup
    public void setup() {
        // Initialize the options object. Since it is immutable after creation,
        // we can reuse it across benchmarks.
        this.options = SevenZFileOptions.DEFAULT;
    }

    @Benchmark
    public void getDefaultOptions(Blackhole bh) {
        // Test reading a simple property
        bh.consume(this.options.getMaxMemoryLimitInKb());
    }

    @Benchmark
    public void getTryToRecoverBrokenArchives(Blackhole bh) {
        // Test reading a boolean property
        bh.consume(this.options.getTryToRecoverBrokenArchives());
    }

    @Benchmark
    public void getUseDefaultNameForUnnamedEntries(Blackhole bh) {
        // Test reading a boolean property
        bh.consume(this.options.getUseDefaultNameForUnnamedEntries());
    }

    @Benchmark
    public void builderBuild(Blackhole bh) {
        // Test the builder pattern (which creates a new instance)
        SevenZFileOptions.Builder builder = SevenZFileOptions.builder();
        bh.consume(builder.build());
    }
}

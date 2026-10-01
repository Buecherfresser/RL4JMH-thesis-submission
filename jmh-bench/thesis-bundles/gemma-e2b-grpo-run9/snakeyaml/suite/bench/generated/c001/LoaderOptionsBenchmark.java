package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.LoaderOptions;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LoaderOptionsBenchmark {

    // Since LoaderOptions is mutable and we are testing configuration changes,
    // we instantiate it inside the benchmark method to ensure isolation
    // and test the setter logic directly.

    @Benchmark
    public void testSetAllowDuplicateKeys(Blackhole bh) {
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        bh.consume(options);
    }

    @Benchmark
    public void testSetWarnOnDuplicateKeys(Blackhole bh) {
        LoaderOptions options = new LoaderOptions();
        options.setWarnOnDuplicateKeys(true);
        bh.consume(options);
    }

    @Benchmark
    public void testSetNestingDepthLimit(Blackhole bh) {
        LoaderOptions options = new LoaderOptions();
        options.setNestingDepthLimit(100);
        bh.consume(options);
    }

    @Benchmark
    public void testGetTagInspector(Blackhole bh) {
        // Test a read-only method call
        LoaderOptions options = new LoaderOptions();
        // We don't need to modify it, just call a method that returns a final field
        bh.consume(options.getTagInspector());
    }
}

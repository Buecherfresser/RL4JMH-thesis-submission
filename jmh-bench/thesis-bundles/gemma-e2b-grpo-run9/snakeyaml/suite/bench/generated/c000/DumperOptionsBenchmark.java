package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Map;
import java.util.TimeZone;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.YAMLException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumperOptionsBenchmark {

    // We don't need @State fields if we create a new DumperOptions instance
    // in every benchmark method, as we are testing method call overhead,
    // not state persistence across iterations.

    @Benchmark
    public void testGetDefaultScalarStyle(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        DumperOptions options = new DumperOptions();
        bh.consume(options.getDefaultScalarStyle());
    }

    @Benchmark
    public void testSetIndent(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        DumperOptions options = new DumperOptions();
        try {
            options.setIndent(4);
        } catch (YAMLException e) {
            // Ignore exceptions for simple timing benchmarks if they are expected to fail
        }
        bh.consume(options);
    }

    @Benchmark
    public void testSetAllowUnicode(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        DumperOptions options = new DumperOptions();
        options.setAllowUnicode(false);
        bh.consume(options);
    }

    @Benchmark
    public void testGetVersion(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        DumperOptions options = new DumperOptions();
        bh.consume(options.getVersion());
    }

    @Benchmark
    public void testSetExplicitStart(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        DumperOptions options = new DumperOptions();
        try {
            options.setExplicitStart(true);
        } catch (YAMLException e) {
            // Ignore exceptions
        }
        bh.consume(options);
    }

    @Benchmark
    public void testGetLineBreak(Blackhole bh) {
        // Create a fresh instance for each benchmark run
        DumperOptions options = new DumperOptions();
        bh.consume(options.getLineBreak());
    }
}

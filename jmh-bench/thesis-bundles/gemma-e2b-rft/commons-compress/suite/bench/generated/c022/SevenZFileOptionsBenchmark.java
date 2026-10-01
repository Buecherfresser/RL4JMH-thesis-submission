package bench.generated.c022;

import org.apache.commons.compress.archivers.sevenz.SevenZFileOptions;
import org.apache.commons.compress.archivers.sevenz.SevenZFileOptions.Builder;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SevenZFileOptionsBenchmark {

    private SevenZFileOptions defaultOptions;
    private SevenZFileOptions customMemoryOptions;
    private SevenZFileOptions customRecoveryOptions;
    private SevenZFileOptions allCustomOptions;

    // Removed 'final' to allow initialization in @Setup
    private byte[] testData;

    @Setup
    public void setup() {
        // 1. Prepare a representative payload (Input data)
        // Using a moderately sized array to ensure the setup isn't trivial.
        this.testData = new byte[1024 * 1024]; // 1MB of data

        // 2. Initialize default options
        this.defaultOptions = SevenZFileOptions.DEFAULT;

        // 3. Initialize custom options
        this.customMemoryOptions = SevenZFileOptions.builder()
                .withMaxMemoryLimitInKb(4096) // Example custom limit
                .build();

        this.customRecoveryOptions = SevenZFileOptions.builder()
                .withTryToRecoverBrokenArchives(true)
                .build();

        this.allCustomOptions = SevenZFileOptions.builder()
                .withMaxMemoryLimitInKb(8192)
                .withTryToRecoverBrokenArchives(true)
                .withUseDefaultNameForUnnamedEntries(false)
                .build();
    }

    @Benchmark
    public void getDefaultOptions(Blackhole bh) {
        SevenZFileOptions options = defaultOptions;
        bh.consume(options);
    }

    @Benchmark
    public void getCustomMemoryOptions(Blackhole bh) {
        SevenZFileOptions options = customMemoryOptions;
        bh.consume(options);
    }

    @Benchmark
    public void getCustomRecoveryOptions(Blackhole bh) {
        SevenZFileOptions options = customRecoveryOptions;
        bh.consume(options);
    }

    @Benchmark
    public void getAllCustomOptions(Blackhole bh) {
        SevenZFileOptions options = allCustomOptions;
        bh.consume(options);
    }

    @Benchmark
    public void getMaxMemoryLimitInKb(Blackhole bh) {
        int limit = defaultOptions.getMaxMemoryLimitInKb();
        bh.consume(limit);
    }

    @Benchmark
    public void getTryToRecoverBrokenArchives(Blackhole bh) {
        boolean tryRecover = defaultOptions.getTryToRecoverBrokenArchives();
        bh.consume(tryRecover);
    }

    @Benchmark
    public void getUseDefaultNameForUnnamedEntries(Blackhole bh) {
        boolean useDefaultName = defaultOptions.getUseDefaultNameForUnnamedEntries();
        bh.consume(useDefaultName);
    }
}

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

    // State fields to hold the different configurations we will test
    private SevenZFileOptions defaultOptions;
    private SevenZFileOptions highMemoryOptions;
    private SevenZFileOptions recoveryEnabledOptions;
    private SevenZFileOptions allOptions;

    @Setup
    public void setup() {
        // 1. Default options
        this.defaultOptions = SevenZFileOptions.DEFAULT;

        // 2. Options with custom memory limit (e.g., 1024 KiB)
        this.highMemoryOptions = SevenZFileOptions.builder()
                .withMaxMemoryLimitInKb(1024)
                .build();

        // 3. Options with recovery enabled
        this.recoveryEnabledOptions = SevenZFileOptions.builder()
                .withTryToRecoverBrokenArchives(true)
                .build();

        // 4. Options with both custom settings
        this.allOptions = SevenZFileOptions.builder()
                .withMaxMemoryLimitInKb(512)
                .withTryToRecoverBrokenArchives(true)
                .build();
    }

    /**
     * Benchmarks accessing the default options configuration.
     */
    @Benchmark
    public void testDefaultOptions(Blackhole bh) {
        int memoryLimit = defaultOptions.getMaxMemoryLimitInKb();
        bh.consume(memoryLimit);
        boolean recovery = defaultOptions.getTryToRecoverBrokenArchives();
        bh.consume(recovery);
        boolean unnamed = defaultOptions.getUseDefaultNameForUnnamedEntries();
        bh.consume(unnamed);
    }

    /**
     * Benchmarks accessing options configured with a high memory limit.
     */
    @Benchmark
    public void testHighMemoryOptions(Blackhole bh) {
        int memoryLimit = highMemoryOptions.getMaxMemoryLimitInKb();
        bh.consume(memoryLimit);
        boolean recovery = highMemoryOptions.getTryToRecoverBrokenArchives();
        bh.consume(recovery);
        boolean unnamed = highMemoryOptions.getUseDefaultNameForUnnamedEntries();
        bh.consume(unnamed);
    }

    /**
     * Benchmarks accessing options where recovery is enabled.
     */
    @Benchmark
    public void testRecoveryEnabledOptions(Blackhole bh) {
        int memoryLimit = recoveryEnabledOptions.getMaxMemoryLimitInKb();
        bh.consume(memoryLimit);
        boolean recovery = recoveryEnabledOptions.getTryToRecoverBrokenArchives();
        bh.consume(recovery);
        boolean unnamed = recoveryEnabledOptions.getUseDefaultNameForUnnamedEntries();
        bh.consume(unnamed);
    }

    /**
     * Benchmarks accessing options configured with both custom settings.
     */
    @Benchmark
    public void testAllOptions(Blackhole bh) {
        int memoryLimit = allOptions.getMaxMemoryLimitInKb();
        bh.consume(memoryLimit);
        boolean recovery = allOptions.getTryToRecoverBrokenArchives();
        bh.consume(recovery);
        boolean unnamed = allOptions.getUseDefaultNameForUnnamedEntries();
        bh.consume(unnamed);
    }
}

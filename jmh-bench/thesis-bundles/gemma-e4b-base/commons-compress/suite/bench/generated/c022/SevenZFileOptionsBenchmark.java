package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.sevenz.SevenZFileOptions;
import org.apache.commons.compress.archivers.sevenz.SevenZFileOptions.Builder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SevenZFileOptionsBenchmark {

    private SevenZFileOptions defaultOptions;
    private SevenZFileOptions customOptions;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Default options
        defaultOptions = SevenZFileOptions.DEFAULT;

        // 2. Custom options (e.g., setting memory limit and recovery flag)
        Builder builder = SevenZFileOptions.builder()
                .withMaxMemoryLimitInKb(1024)
                .withTryToRecoverBrokenArchives(true);
        customOptions = builder.build();
    }

    @Benchmark
    public void benchmarkAccessingDefaultOptions(Blackhole bh) {
        bh.consume(defaultOptions);
    }

    @Benchmark
    public void benchmarkBuildingDefaultOptions(Blackhole bh) {
        // Test the construction path using the builder without modifications
        SevenZFileOptions options = SevenZFileOptions.builder().build();
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkBuildingCustomOptions(Blackhole bh) {
        // Test the construction path with modifications
        Builder builder = SevenZFileOptions.builder()
                .withMaxMemoryLimitInKb(512)
                .withUseDefaultNameForUnnamedEntries(true);
        SevenZFileOptions options = builder.build();
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSettingMaxMemoryLimit(Blackhole bh) {
        // Test the setter method on the builder
        Builder builder = SevenZFileOptions.builder().withMaxMemoryLimitInKb(2048);
        bh.consume(builder);
    }

    @Benchmark
    public void benchmarkSettingRecoveryFlag(Blackhole bh) {
        // Test the setter method on the builder
        Builder builder = SevenZFileOptions.builder().withTryToRecoverBrokenArchives(true);
        bh.consume(builder);
    }

    @Benchmark
    public void benchmarkSettingUnnamedEntryName(Blackhole bh) {
        // Test the setter method on the builder
        Builder builder = SevenZFileOptions.builder().withUseDefaultNameForUnnamedEntries(false);
        bh.consume(builder);
    }

    @Benchmark
    public void benchmarkAccessingMaxMemoryLimit(Blackhole bh) {
        // Test getter
        bh.consume(customOptions.getMaxMemoryLimitInKb());
    }

    @Benchmark
    public void benchmarkAccessingRecoveryFlag(Blackhole bh) {
        // Test getter
        bh.consume(customOptions.getTryToRecoverBrokenArchives());
    }

    @Benchmark
    public void benchmarkAccessingUnnamedEntryName(Blackhole bh) {
        // Test getter
        bh.consume(customOptions.getUseDefaultNameForUnnamedEntries());
    }
}

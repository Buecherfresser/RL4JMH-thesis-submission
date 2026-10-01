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

    private int customMemoryLimit;
    private boolean customUseDefaultName;
    private boolean customTryRecover;
    private SevenZFileOptions defaultOptions;
    private SevenZFileOptions customOptions;

    @Setup(Level.Trial)
    public void setUp() {
        customMemoryLimit = 2048;
        customUseDefaultName = true;
        customTryRecover = true;
        defaultOptions = SevenZFileOptions.DEFAULT;
        customOptions = SevenZFileOptions.builder()
                .withMaxMemoryLimitInKb(customMemoryLimit)
                .withUseDefaultNameForUnnamedEntries(customUseDefaultName)
                .withTryToRecoverBrokenArchives(customTryRecover)
                .build();
    }

    @Benchmark
    public SevenZFileOptions benchmarkDefaultOptions() {
        return SevenZFileOptions.DEFAULT;
    }

    @Benchmark
    public SevenZFileOptions benchmarkBuilderBuildDefault() {
        return SevenZFileOptions.builder().build();
    }

    @Benchmark
    public SevenZFileOptions benchmarkBuilderCustom() {
        return SevenZFileOptions.builder()
                .withMaxMemoryLimitInKb(customMemoryLimit)
                .withUseDefaultNameForUnnamedEntries(customUseDefaultName)
                .withTryToRecoverBrokenArchives(customTryRecover)
                .build();
    }

    @Benchmark
    public int benchmarkGetMaxMemoryLimitDefault() {
        return defaultOptions.getMaxMemoryLimitInKb();
    }

    @Benchmark
    public boolean benchmarkGetUseDefaultNameDefault() {
        return defaultOptions.getUseDefaultNameForUnnamedEntries();
    }

    @Benchmark
    public boolean benchmarkGetTryRecoverDefault() {
        return defaultOptions.getTryToRecoverBrokenArchives();
    }

    @Benchmark
    public int benchmarkGetMaxMemoryLimitCustom() {
        return customOptions.getMaxMemoryLimitInKb();
    }

    @Benchmark
    public boolean benchmarkGetUseDefaultNameCustom() {
        return customOptions.getUseDefaultNameForUnnamedEntries();
    }

    @Benchmark
    public boolean benchmarkGetTryRecoverCustom() {
        return customOptions.getTryToRecoverBrokenArchives();
    }
}

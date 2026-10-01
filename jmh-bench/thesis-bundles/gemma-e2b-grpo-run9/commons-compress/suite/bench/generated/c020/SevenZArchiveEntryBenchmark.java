package bench.generated.c020;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SevenZArchiveEntryBenchmark {

    private SevenZArchiveEntry entry;

    @Setup
    public void setup() {
        // Initialize a single instance of the entry.
        // Since the constructor is parameterless, this is safe.
        this.entry = new SevenZArchiveEntry();
    }

    @Benchmark
    public String benchmarkGetName(Blackhole bh) {
        // Test a simple getter that returns a field (name).
        // Since the instance is not mutated, this is safe.
        return this.entry.getName();
    }

    @Benchmark
    public Long benchmarkGetSize(Blackhole bh) {
        // Test a simple getter that returns a field (size).
        return this.entry.getSize();
    }

    @Benchmark
    public Boolean benchmarkIsDirectory(Blackhole bh) {
        // Test a simple boolean getter.
        return this.entry.isDirectory();
    }

    @Benchmark
    public Boolean benchmarkHasStream(Blackhole bh) {
        // Test a simple boolean getter.
        return this.entry.hasStream();
    }

    @Benchmark
    public void benchmarkSetName(Blackhole bh) {
        // Test a setter that modifies internal state.
        // We don't consume the return value as it's void.
        this.entry.setName("TestName");
    }

    @Benchmark
    public void benchmarkSetSize(Blackhole bh) {
        // Test another setter that modifies internal state.
        this.entry.setSize(1024L);
    }

    @Benchmark
    public void benchmarkSetHasStream(Blackhole bh) {
        // Test another setter that modifies internal state.
        this.entry.setHasStream(true);
    }
}

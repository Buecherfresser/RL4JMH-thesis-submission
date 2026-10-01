package bench.generated.c005;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.arj.ArjArchiveEntry;
import java.util.Date;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArjArchiveEntryBenchmark {

    private ArjArchiveEntry entry;

    /**
     * Setup method to create a default ArjArchiveEntry instance.
     */
    @Setup
    public void setup() {
        // Create a default entry.
        this.entry = new ArjArchiveEntry();
    }

    @Benchmark
    public void benchmarkGetName(Blackhole bh) {
        String name = entry.getName();
        bh.consume(name);
    }

    @Benchmark
    public void benchmarkGetSize(Blackhole bh) {
        long size = entry.getSize();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkGetHostOs(Blackhole bh) {
        int os = entry.getHostOs();
        bh.consume(os);
    }

    @Benchmark
    public void benchmarkGetMode(Blackhole bh) {
        int mode = entry.getMode();
        bh.consume(mode);
    }

    @Benchmark
    public void benchmarkIsDirectory(Blackhole bh) {
        boolean isDir = entry.isDirectory();
        bh.consume(isDir);
    }

    @Benchmark
    public void benchmarkIsHostOsUnix(Blackhole bh) {
        boolean isUnix = entry.isHostOsUnix();
        bh.consume(isUnix);
    }

    @Benchmark
    public void benchmarkGetLastModifiedDate(Blackhole bh) {
        Date date = entry.getLastModifiedDate();
        bh.consume(date);
    }
}

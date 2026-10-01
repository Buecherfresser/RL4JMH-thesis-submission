package bench.generated.c005;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.arj.ArjArchiveEntry;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArjArchiveEntryBenchmark {

    private ArjArchiveEntry entry;

    @Setup(Level.Trial)
    public void setUp() {
        entry = new ArjArchiveEntry();
    }

    @Benchmark
    public int benchmarkGetHostOs() {
        return entry.getHostOs();
    }

    @Benchmark
    public Date benchmarkGetLastModifiedDate() {
        return entry.getLastModifiedDate();
    }

    @Benchmark
    public int benchmarkGetMode() {
        return entry.getMode();
    }

    @Benchmark
    public String benchmarkGetName() {
        return entry.getName();
    }

    @Benchmark
    public long benchmarkGetSize() {
        return entry.getSize();
    }

    @Benchmark
    public int benchmarkGetUnixMode() {
        return entry.getUnixMode();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return entry.hashCode();
    }

    @Benchmark
    public boolean benchmarkIsDirectory() {
        return entry.isDirectory();
    }

    @Benchmark
    public boolean benchmarkIsHostOsUnix() {
        return entry.isHostOsUnix();
    }

    @Benchmark
    public boolean benchmarkEquals(Blackhole bh) {
        // Compare the entry with a new default instance
        ArjArchiveEntry other = new ArjArchiveEntry();
        boolean result = entry.equals(other);
        bh.consume(result);
        return result;
    }
}

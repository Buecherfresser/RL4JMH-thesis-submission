package bench.generated.c005;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.arj.ArjArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipUtil;

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
public class ArjArchiveEntryBenchmark {

    private ArjArchiveEntry entry;

    /**
     * Setup method to create a representative ArjArchiveEntry instance.
     */
    @Setup
    public void setup() {
        // Create a representative entry instance.
        entry = new ArjArchiveEntry();
    }

    @Benchmark
    public void testGetHostOs(Blackhole bh) {
        int os = entry.getHostOs();
        bh.consume(os);
    }

    @Benchmark
    public void testIsHostOsUnix(Blackhole bh) {
        boolean isUnix = entry.isHostOsUnix();
        bh.consume(isUnix);
    }

    @Benchmark
    public void testGetName(Blackhole bh) {
        String name = entry.getName();
        bh.consume(name);
    }

    @Benchmark
    public void testGetSize(Blackhole bh) {
        long size = entry.getSize();
        bh.consume(size);
    }

    @Benchmark
    public void testGetMode(Blackhole bh) {
        int mode = entry.getMode();
        bh.consume(mode);
    }

    @Benchmark
    public void testGetLastModifiedDate(Blackhole bh) {
        Date date = entry.getLastModifiedDate();
        bh.consume(date);
    }

    @Benchmark
    public void testGetUnixMode(Blackhole bh) {
        int unixMode = entry.getUnixMode();
        bh.consume(unixMode);
    }
}

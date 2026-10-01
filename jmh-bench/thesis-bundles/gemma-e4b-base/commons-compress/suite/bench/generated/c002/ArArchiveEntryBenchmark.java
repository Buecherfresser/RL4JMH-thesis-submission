package bench.generated.c002;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import java.io.File;
import java.nio.file.Path;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveEntryBenchmark {

    private ArArchiveEntry entry1;
    private ArArchiveEntry entry2;

    @Setup(Level.Trial)
    public void setup() {
        // Setup entry 1: standard entry
        String name1 = "test_file_1.txt";
        long length1 = 1024 * 5;
        int userId1 = 1000;
        int groupId1 = 2000;
        int mode1 = 33188; // 0100644
        long lastModified1 = 1678886400L; // March 15, 2023

        entry1 = new ArArchiveEntry(name1, length1, userId1, groupId1, mode1, lastModified1);

        // Setup entry 2: different entry for equality checks
        String name2 = "another_file.dat";
        long length2 = 512;
        int userId2 = 1;
        int groupId2 = 1;
        int mode2 = 33188;
        long lastModified2 = 1678886400L;

        entry2 = new ArArchiveEntry(name2, length2, userId2, groupId2, mode2, lastModified2);
    }

    @Benchmark
    public void benchmarkGetName(Blackhole bh) {
        String name = entry1.getName();
        bh.consume(name);
    }

    @Benchmark
    public void benchmarkGetLength(Blackhole bh) {
        long length = entry1.getLength();
        bh.consume(length);
    }

    @Benchmark
    public void benchmarkGetSize(Blackhole bh) {
        long size = entry1.getSize();
        bh.consume(size);
    }

    @Benchmark
    public void benchmarkGetGroupId(Blackhole bh) {
        int groupId = entry1.getGroupId();
        bh.consume(groupId);
    }

    @Benchmark
    public void benchmarkGetLastModified(Blackhole bh) {
        long lastModified = entry1.getLastModified();
        bh.consume(lastModified);
    }

    @Benchmark
    public void benchmarkGetMode(Blackhole bh) {
        int mode = entry1.getMode();
        bh.consume(mode);
    }

    @Benchmark
    public void benchmarkGetUserId(Blackhole bh) {
        int userId = entry1.getUserId();
        bh.consume(userId);
    }

    @Benchmark
    public void benchmarkGetLastModifiedDate(Blackhole bh) {
        Date date = entry1.getLastModifiedDate();
        bh.consume(date);
    }

    @Benchmark
    public void benchmarkEqualsSameObject(Blackhole bh) {
        boolean result = entry1.equals(entry1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEqualsDifferentName(Blackhole bh) {
        // Test inequality based on name
        boolean result = entry1.equals(entry2);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEqualsNull(Blackhole bh) {
        boolean result = entry1.equals(null);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHashCode(Blackhole bh) {
        int hash = entry1.hashCode();
        bh.consume(hash);
    }

    @Benchmark
    public void benchmarkIsDirectory(Blackhole bh) {
        boolean isDir = entry1.isDirectory();
        bh.consume(isDir);
    }
}

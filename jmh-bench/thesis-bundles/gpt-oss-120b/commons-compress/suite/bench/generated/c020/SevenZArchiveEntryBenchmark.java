package bench.generated.c020;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import java.util.Date;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import org.apache.commons.io.file.attribute.FileTimes;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class SevenZArchiveEntryBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        SevenZArchiveEntry entry;
        String name = "test.txt";
        long size = 12_345L;
        Date date = new Date(1_609_459_200_000L); // 2021-01-01T00:00:00Z
        FileTime fileTime;
        int crc = 0xDEADBEEF;
        boolean isDirectory = false;
        boolean isAntiItem = false;
        boolean hasStream = true;
        int windowsAttributes = 0x20;
        boolean hasAccessDate = true;
        boolean hasCreationDate = true;
        boolean hasLastModifiedDate = true;
        boolean hasWindowsAttributes = true;
        boolean hasCrc = true;
        long ntfsTime;

        @Setup
        public void setup() {
            entry = new SevenZArchiveEntry();
            fileTime = FileTime.fromMillis(date.getTime());

            entry.setName(name);
            entry.setSize(size);
            entry.setLastModifiedDate(date);
            entry.setCreationDate(date);
            entry.setAccessDate(date);
            entry.setHasCrc(hasCrc);
            entry.setCrc(crc);
            entry.setHasCreationDate(hasCreationDate);
            entry.setHasLastModifiedDate(hasLastModifiedDate);
            entry.setHasAccessDate(hasAccessDate);
            entry.setHasWindowsAttributes(hasWindowsAttributes);
            entry.setWindowsAttributes(windowsAttributes);
            entry.setHasStream(hasStream);
            entry.setDirectory(isDirectory);
            entry.setAntiItem(isAntiItem);
            entry.setContentMethods(Collections.emptyList());

            ntfsTime = FileTimes.toNtfsTime(date);
        }
    }

    // --- Getter benchmarks (returning the value) ---

    @Benchmark
    public String benchmarkGetName(BenchmarkState s) {
        return s.entry.getName();
    }

    @Benchmark
    public long benchmarkGetSize(BenchmarkState s) {
        return s.entry.getSize();
    }

    @Benchmark
    public Date benchmarkGetLastModifiedDate(BenchmarkState s) {
        return s.entry.getLastModifiedDate();
    }

    @Benchmark
    public FileTime benchmarkGetLastModifiedTime(BenchmarkState s) {
        return s.entry.getLastModifiedTime();
    }

    @Benchmark
    public Date benchmarkGetCreationDate(BenchmarkState s) {
        return s.entry.getCreationDate();
    }

    @Benchmark
    public FileTime benchmarkGetCreationTime(BenchmarkState s) {
        return s.entry.getCreationTime();
    }

    @Benchmark
    public Date benchmarkGetAccessDate(BenchmarkState s) {
        return s.entry.getAccessDate();
    }

    @Benchmark
    public FileTime benchmarkGetAccessTime(BenchmarkState s) {
        return s.entry.getAccessTime();
    }

    @Benchmark
    public boolean benchmarkGetHasCrc(BenchmarkState s) {
        return s.entry.getHasCrc();
    }

    @Benchmark
    public int benchmarkGetCrc(BenchmarkState s) {
        return s.entry.getCrc();
    }

    @Benchmark
    public long benchmarkGetCrcValue(BenchmarkState s) {
        return s.entry.getCrcValue();
    }

    @Benchmark
    public boolean benchmarkGetHasCreationDate(BenchmarkState s) {
        return s.entry.getHasCreationDate();
    }

    @Benchmark
    public boolean benchmarkGetHasLastModifiedDate(BenchmarkState s) {
        return s.entry.getHasLastModifiedDate();
    }

    @Benchmark
    public boolean benchmarkGetHasAccessDate(BenchmarkState s) {
        return s.entry.getHasAccessDate();
    }

    @Benchmark
    public boolean benchmarkGetHasWindowsAttributes(BenchmarkState s) {
        return s.entry.getHasWindowsAttributes();
    }

    @Benchmark
    public boolean benchmarkHasStream(BenchmarkState s) {
        return s.entry.hasStream();
    }

    @Benchmark
    public boolean benchmarkIsDirectory(BenchmarkState s) {
        return s.entry.isDirectory();
    }

    @Benchmark
    public boolean benchmarkIsAntiItem(BenchmarkState s) {
        return s.entry.isAntiItem();
    }

    @Benchmark
    public boolean benchmarkIsEmptyStream(BenchmarkState s) {
        return s.entry.isEmptyStream();
    }

    @Benchmark
    public int benchmarkGetWindowsAttributes(BenchmarkState s) {
        return s.entry.getWindowsAttributes();
    }

    @Benchmark
    public java.lang.Iterable<?> benchmarkGetContentMethods(BenchmarkState s) {
        return s.entry.getContentMethods();
    }

    @Benchmark
    public long benchmarkJavaTimeToNtfsTime(BenchmarkState s) {
        return SevenZArchiveEntry.javaTimeToNtfsTime(s.date);
    }

    @Benchmark
    public Date benchmarkNtfsTimeToJavaTime(BenchmarkState s) {
        return SevenZArchiveEntry.ntfsTimeToJavaTime(s.ntfsTime);
    }

    // --- Setter benchmarks (void, consume entry) ---

    @Benchmark
    public void benchmarkSetName(BenchmarkState s, Blackhole bh) {
        s.entry.setName(s.name);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetSize(BenchmarkState s, Blackhole bh) {
        s.entry.setSize(s.size);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetLastModifiedDateDate(BenchmarkState s, Blackhole bh) {
        s.entry.setLastModifiedDate(s.date);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetLastModifiedDateLong(BenchmarkState s, Blackhole bh) {
        s.entry.setLastModifiedDate(s.ntfsTime);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetCreationDateDate(BenchmarkState s, Blackhole bh) {
        s.entry.setCreationDate(s.date);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetCreationDateLong(BenchmarkState s, Blackhole bh) {
        s.entry.setCreationDate(s.ntfsTime);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetAccessDateDate(BenchmarkState s, Blackhole bh) {
        s.entry.setAccessDate(s.date);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetAccessDateLong(BenchmarkState s, Blackhole bh) {
        s.entry.setAccessDate(s.ntfsTime);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetHasCrc(BenchmarkState s, Blackhole bh) {
        s.entry.setHasCrc(s.hasCrc);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetCrc(BenchmarkState s, Blackhole bh) {
        s.entry.setCrc(s.crc);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetHasCreationDate(BenchmarkState s, Blackhole bh) {
        s.entry.setHasCreationDate(s.hasCreationDate);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetHasLastModifiedDate(BenchmarkState s, Blackhole bh) {
        s.entry.setHasLastModifiedDate(s.hasLastModifiedDate);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetHasAccessDate(BenchmarkState s, Blackhole bh) {
        s.entry.setHasAccessDate(s.hasAccessDate);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetHasWindowsAttributes(BenchmarkState s, Blackhole bh) {
        s.entry.setHasWindowsAttributes(s.hasWindowsAttributes);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetWindowsAttributes(BenchmarkState s, Blackhole bh) {
        s.entry.setWindowsAttributes(s.windowsAttributes);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetHasStream(BenchmarkState s, Blackhole bh) {
        s.entry.setHasStream(s.hasStream);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetDirectory(BenchmarkState s, Blackhole bh) {
        s.entry.setDirectory(s.isDirectory);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetAntiItem(BenchmarkState s, Blackhole bh) {
        s.entry.setAntiItem(s.isAntiItem);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetContentMethods(BenchmarkState s, Blackhole bh) {
        s.entry.setContentMethods(Collections.emptyList());
        bh.consume(s.entry);
    }
}

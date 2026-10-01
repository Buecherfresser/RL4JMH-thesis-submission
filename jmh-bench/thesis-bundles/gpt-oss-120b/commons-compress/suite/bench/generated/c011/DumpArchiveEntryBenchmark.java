package bench.generated.c011;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Set;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants.SEGMENT_TYPE;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry.TYPE;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry.PERMISSION;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveEntryBenchmark {

    private DumpArchiveEntry manualEntry;
    private DumpArchiveEntry defaultEntry;
    private int sparseIndex = 0;

    @Setup(Level.Trial)
    public void setUp() {
        // Manual entry for getters that do not depend on header parsing
        manualEntry = new DumpArchiveEntry("test/path/file.txt", "file.txt");
        manualEntry.setType(DumpArchiveEntry.TYPE.FILE);
        manualEntry.setMode(0644);
        manualEntry.setSize(12345L);
        manualEntry.setUserId(1000);
        manualEntry.setGroupId(1000);
        manualEntry.setGeneration(1);
        manualEntry.setDeleted(false);
        manualEntry.setAccessTime(new Date());
        manualEntry.setLastModifiedDate(new Date());
        manualEntry.setCreationTime(new Date());

        // Default entry to exercise header‑related getters (all fields default to zero)
        defaultEntry = new DumpArchiveEntry();
    }

    // -------------------------------------------------------------------------
    // Getter benchmarks on manually constructed entry
    // -------------------------------------------------------------------------

    @Benchmark
    public String benchmarkGetName() {
        return manualEntry.getName();
    }

    @Benchmark
    public String benchmarkGetSimpleName() {
        return manualEntry.getSimpleName();
    }

    @Benchmark
    public long benchmarkGetSize() {
        return manualEntry.getSize();
    }

    @Benchmark
    public Date benchmarkGetLastModifiedDate() {
        return manualEntry.getLastModifiedDate();
    }

    @Benchmark
    public Date benchmarkGetAccessTime() {
        return manualEntry.getAccessTime();
    }

    @Benchmark
    public Date benchmarkGetCreationTime() {
        return manualEntry.getCreationTime();
    }

    @Benchmark
    public int benchmarkGetUserId() {
        return manualEntry.getUserId();
    }

    @Benchmark
    public int benchmarkGetGroupId() {
        return manualEntry.getGroupId();
    }

    @Benchmark
    public int benchmarkGetGeneration() {
        return manualEntry.getGeneration();
    }

    @Benchmark
    public TYPE benchmarkGetType() {
        return manualEntry.getType();
    }

    @Benchmark
    public int benchmarkGetMode() {
        return manualEntry.getMode();
    }

    @Benchmark
    public Set<PERMISSION> benchmarkGetPermissions() {
        return manualEntry.getPermissions();
    }

    @Benchmark
    public boolean benchmarkIsDirectory() {
        return manualEntry.isDirectory();
    }

    @Benchmark
    public boolean benchmarkIsFile() {
        return manualEntry.isFile();
    }

    @Benchmark
    public boolean benchmarkIsBlkDev() {
        return manualEntry.isBlkDev();
    }

    @Benchmark
    public boolean benchmarkIsChrDev() {
        return manualEntry.isChrDev();
    }

    @Benchmark
    public boolean benchmarkIsFifo() {
        return manualEntry.isFifo();
    }

    @Benchmark
    public boolean benchmarkIsSocket() {
        return manualEntry.isSocket();
    }

    @Benchmark
    public boolean benchmarkIsDeleted() {
        return manualEntry.isDeleted();
    }

    @Benchmark
    public long benchmarkGetOffset() {
        return manualEntry.getOffset();
    }

    @Benchmark
    public int benchmarkGetNlink() {
        return manualEntry.getNlink();
    }

    // -------------------------------------------------------------------------
    // Header‑related getter benchmarks on a default‑constructed entry
    // -------------------------------------------------------------------------

    @Benchmark
    public int benchmarkGetHeaderCount() {
        return defaultEntry.getHeaderCount();
    }

    @Benchmark
    public int benchmarkGetHeaderHoles() {
        return defaultEntry.getHeaderHoles();
    }

    @Benchmark
    public SEGMENT_TYPE benchmarkGetHeaderType() {
        return defaultEntry.getHeaderType();
    }

    @Benchmark
    public int benchmarkGetIno() {
        return defaultEntry.getIno();
    }

    @Benchmark
    public boolean benchmarkIsSparseRecord() {
        return defaultEntry.isSparseRecord(sparseIndex);
    }

    @Benchmark
    public String benchmarkToString() {
        return defaultEntry.toString();
    }
}

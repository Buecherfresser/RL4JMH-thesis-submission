package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.nio.file.attribute.FileTime;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveEntryBenchmark {

    private TarArchiveEntry readOnlyEntry;
    private byte[] headerBuffer;

    @Setup
    public void setUp() {
        readOnlyEntry = new TarArchiveEntry("test/file.txt");
        readOnlyEntry.setSize(12345L);
        readOnlyEntry.setUserId(1000);
        readOnlyEntry.setGroupId(1000);
        readOnlyEntry.setUserName("user");
        readOnlyEntry.setGroupName("group");
        headerBuffer = new byte[512];
    }

    @Benchmark
    public String getName() {
        return readOnlyEntry.getName();
    }

    @Benchmark
    public long getSize() {
        return readOnlyEntry.getSize();
    }

    @Benchmark
    public int getMode() {
        return readOnlyEntry.getMode();
    }

    @Benchmark
    public long getUserId() {
        return readOnlyEntry.getLongUserId();
    }

    @Benchmark
    public long getGroupId() {
        return readOnlyEntry.getLongGroupId();
    }

    @Benchmark
    public String getUserName() {
        return readOnlyEntry.getUserName();
    }

    @Benchmark
    public String getGroupName() {
        return readOnlyEntry.getGroupName();
    }

    @Benchmark
    public byte getLinkFlag() {
        return readOnlyEntry.getLinkFlag();
    }

    @Benchmark
    public String getLinkName() {
        return readOnlyEntry.getLinkName();
    }

    @Benchmark
    public FileTime getLastModifiedTime() {
        return readOnlyEntry.getLastModifiedTime();
    }

    @Benchmark
    public FileTime getLastAccessTime() {
        return readOnlyEntry.getLastAccessTime();
    }

    @Benchmark
    public FileTime getCreationTime() {
        return readOnlyEntry.getCreationTime();
    }

    @Benchmark
    public FileTime getStatusChangeTime() {
        return readOnlyEntry.getStatusChangeTime();
    }

    @Benchmark
    public boolean isDirectory() {
        return readOnlyEntry.isDirectory();
    }

    @Benchmark
    public boolean isFile() {
        return readOnlyEntry.isFile();
    }

    @Benchmark
    public boolean isSymbolicLink() {
        return readOnlyEntry.isSymbolicLink();
    }

    @Benchmark
    public boolean isLink() {
        return readOnlyEntry.isLink();
    }

    @Benchmark
    public boolean isFIFO() {
        return readOnlyEntry.isFIFO();
    }

    @Benchmark
    public boolean isBlockDevice() {
        return readOnlyEntry.isBlockDevice();
    }

    @Benchmark
    public boolean isCharacterDevice() {
        return readOnlyEntry.isCharacterDevice();
    }

    @Benchmark
    public boolean isCheckSumOK() {
        return readOnlyEntry.isCheckSumOK();
    }

    @Benchmark
    public boolean isSparse() {
        return readOnlyEntry.isSparse();
    }

    @Benchmark
    public long getRealSize() {
        return readOnlyEntry.getRealSize();
    }

    @Benchmark
    public Map<String, String> getExtraPaxHeaders() {
        return readOnlyEntry.getExtraPaxHeaders();
    }

    @Benchmark
    public void getExtraPaxHeader(Blackhole bh) {
        String v = readOnlyEntry.getExtraPaxHeader("nonexistent");
        bh.consume(v);
    }

    @Benchmark
    public void writeEntryHeader(Blackhole bh) {
        readOnlyEntry.writeEntryHeader(headerBuffer);
        bh.consume(headerBuffer);
    }

    @State(Scope.Benchmark)
    public static class MutableEntryState {
        TarArchiveEntry entry;

        @Setup(Level.Invocation)
        public void setUp() {
            entry = new TarArchiveEntry("mutable.txt");
        }
    }

    @Benchmark
    public void addPaxHeader(MutableEntryState s, Blackhole bh) {
        s.entry.addPaxHeader("key", "value");
        bh.consume(s.entry.getExtraPaxHeader("key"));
    }

    @State(Scope.Benchmark)
    public static class EntryWithHeaderState {
        TarArchiveEntry entry;

        @Setup(Level.Invocation)
        public void setUp() {
            entry = new TarArchiveEntry("clear.txt");
            entry.addPaxHeader("k", "v");
        }
    }

    @Benchmark
    public void clearExtraPaxHeaders(EntryWithHeaderState s, Blackhole bh) {
        s.entry.clearExtraPaxHeaders();
        bh.consume(s.entry.getExtraPaxHeaders().size());
    }
}

package bench.generated.c002;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ArArchiveEntryBenchmark {

    private String nameShort;
    private String nameLong;
    private long length;
    private int userId;
    private int groupId;
    private int mode;
    private long lastModified;

    private ArArchiveEntry entryShort;
    private ArArchiveEntry entryFull;
    private ArArchiveEntry entryDifferent;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() {
        nameShort = "test.txt";
        nameLong = "verylongfilename.txt";
        length = 12345L;
        userId = 1000;
        groupId = 1000;
        mode = 33188; // default mode
        lastModified = System.currentTimeMillis() / 1000L;

        entryShort = new ArArchiveEntry(nameShort, length);
        entryFull = new ArArchiveEntry(nameShort, length, userId, groupId, mode, lastModified);
        entryDifferent = new ArArchiveEntry(nameLong, length + 1);
    }

    @Benchmark
    public String benchmarkGetName() {
        return entryShort.getName();
    }

    @Benchmark
    public long benchmarkGetLength() {
        return entryShort.getLength();
    }

    @Benchmark
    public int benchmarkGetUserId() {
        return entryFull.getUserId();
    }

    @Benchmark
    public int benchmarkGetGroupId() {
        return entryFull.getGroupId();
    }

    @Benchmark
    public int benchmarkGetMode() {
        return entryFull.getMode();
    }

    @Benchmark
    public long benchmarkGetLastModified() {
        return entryFull.getLastModified();
    }

    @Benchmark
    public Date benchmarkGetLastModifiedDate() {
        return entryFull.getLastModifiedDate();
    }

    @Benchmark
    public long benchmarkGetSize() {
        return entryShort.getSize();
    }

    @Benchmark
    public boolean benchmarkIsDirectory() {
        return entryShort.isDirectory();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return entryShort.hashCode();
    }

    @Benchmark
    public boolean benchmarkEqualsSame() {
        return entryShort.equals(entryShort);
    }

    @Benchmark
    public boolean benchmarkEqualsDifferent() {
        return entryShort.equals(entryDifferent);
    }

    @Benchmark
    public ArArchiveEntry benchmarkConstructorSimple() {
        return new ArArchiveEntry(nameShort, length);
    }

    @Benchmark
    public ArArchiveEntry benchmarkConstructorFull() {
        return new ArArchiveEntry(nameShort, length, userId, groupId, mode, lastModified);
    }

    @Benchmark
    public String benchmarkGetNameFull() {
        return entryFull.getName();
    }

    @Benchmark
    public long benchmarkGetLengthFull() {
        return entryFull.getLength();
    }

    @Benchmark
    public long benchmarkGetLastModifiedFull() {
        return entryFull.getLastModified();
    }

    @Benchmark
    public Date benchmarkGetLastModifiedDateFull() {
        return entryFull.getLastModifiedDate();
    }

    @Benchmark
    public void benchmarkConsumeAllGetters(Blackhole bh) {
        bh.consume(entryFull.getName());
        bh.consume(entryFull.getLength());
        bh.consume(entryFull.getUserId());
        bh.consume(entryFull.getGroupId());
        bh.consume(entryFull.getMode());
        bh.consume(entryFull.getLastModified());
        bh.consume(entryFull.getLastModifiedDate());
        bh.consume(entryFull.getSize());
        bh.consume(entryFull.isDirectory());
        bh.consume(entryFull.hashCode());
        bh.consume(entryFull.equals(entryShort));
    }
}

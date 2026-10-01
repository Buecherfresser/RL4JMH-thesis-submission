package bench.generated.c034;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import org.apache.commons.compress.archivers.zip.GeneralPurposeBit;
import org.apache.commons.compress.archivers.zip.ZipShort;
import java.nio.file.attribute.FileTime;
import java.util.zip.ZipEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ZipArchiveEntryBenchmark {

    private ZipArchiveEntry entry;

    @Setup(Level.Trial)
    public void setUp() {
        entry = new ZipArchiveEntry("test.txt");
        entry.setSize(12345L);
        entry.setMethod(ZipEntry.DEFLATED);
        entry.setUnixMode(0644);
        entry.setCommentSource(ZipArchiveEntry.CommentSource.COMMENT);
        entry.setNameSource(ZipArchiveEntry.NameSource.NAME);
        X5455_ExtendedTimestamp ts = new X5455_ExtendedTimestamp();
        ts.setModifyFileTime(FileTime.fromMillis(1000L));
        entry.addExtraField(ts);
    }

    // ---------- Read‑only benchmarks ----------
    @Benchmark
    public String benchmarkGetName() {
        return entry.getName();
    }

    @Benchmark
    public long benchmarkGetSize() {
        return entry.getSize();
    }

    @Benchmark
    public int benchmarkGetMethod() {
        return entry.getMethod();
    }

    @Benchmark
    public boolean benchmarkIsDirectory() {
        return entry.isDirectory();
    }

    @Benchmark
    public int benchmarkGetUnixMode() {
        return entry.getUnixMode();
    }

    @Benchmark
    public ZipArchiveEntry.CommentSource benchmarkGetCommentSource() {
        return entry.getCommentSource();
    }

    @Benchmark
    public ZipArchiveEntry.NameSource benchmarkGetNameSource() {
        return entry.getNameSource();
    }

    @Benchmark
    public long benchmarkGetExternalAttributes() {
        return entry.getExternalAttributes();
    }

    @Benchmark
    public int benchmarkGetInternalAttributes() {
        return entry.getInternalAttributes();
    }

    @Benchmark
    public byte[] benchmarkGetCentralDirectoryExtra() {
        return entry.getCentralDirectoryExtra();
    }

    @Benchmark
    public byte[] benchmarkGetLocalFileDataExtra() {
        return entry.getLocalFileDataExtra();
    }

    @Benchmark
    public ZipExtraField[] benchmarkGetExtraFields() {
        return entry.getExtraFields();
    }

    @Benchmark
    public void benchmarkGetExtraField(Blackhole bh) {
        ZipExtraField f = entry.getExtraField(new ZipShort(0x5455));
        bh.consume(f);
    }

    @Benchmark
    public ZipExtraField[] benchmarkGetExtraFieldsStrict() throws Exception {
        return entry.getExtraFields(ZipArchiveEntry.ExtraFieldParsingMode.STRICT_FOR_KNOW_EXTRA_FIELDS);
    }

    // ---------- Mutating benchmarks ----------
    @State(Scope.Thread)
    public static class FreshEntryState {
        ZipArchiveEntry entry;

        @Setup(Level.Invocation)
        public void setUp() {
            entry = new ZipArchiveEntry("test.txt");
        }
    }

    @Benchmark
    public void benchmarkSetMethod(FreshEntryState s, Blackhole bh) {
        s.entry.setMethod(ZipEntry.DEFLATED);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetSize(FreshEntryState s, Blackhole bh) {
        s.entry.setSize(54321L);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetAlignment(FreshEntryState s, Blackhole bh) {
        s.entry.setAlignment(0);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetUnixMode(FreshEntryState s, Blackhole bh) {
        s.entry.setUnixMode(0755);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetCommentSource(FreshEntryState s, Blackhole bh) {
        s.entry.setCommentSource(ZipArchiveEntry.CommentSource.UNICODE_EXTRA_FIELD);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetNameSource(FreshEntryState s, Blackhole bh) {
        s.entry.setNameSource(ZipArchiveEntry.NameSource.UNICODE_EXTRA_FIELD);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetGeneralPurposeBit(FreshEntryState s, Blackhole bh) {
        s.entry.setGeneralPurposeBit(new GeneralPurposeBit());
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetExternalAttributes(FreshEntryState s, Blackhole bh) {
        s.entry.setExternalAttributes(0x1234L);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetInternalAttributes(FreshEntryState s, Blackhole bh) {
        s.entry.setInternalAttributes(0xABCD);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetRawFlag(FreshEntryState s, Blackhole bh) {
        s.entry.setRawFlag(0xFF);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetTimeLong(FreshEntryState s, Blackhole bh) {
        s.entry.setTime(123456789L);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetTimeFileTime(FreshEntryState s, Blackhole bh) {
        s.entry.setTime(FileTime.fromMillis(123456789L));
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetVersionMadeBy(FreshEntryState s, Blackhole bh) {
        s.entry.setVersionMadeBy(45);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetVersionRequired(FreshEntryState s, Blackhole bh) {
        s.entry.setVersionRequired(20);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkSetDiskNumberStart(FreshEntryState s, Blackhole bh) {
        s.entry.setDiskNumberStart(2L);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkAddExtraField(FreshEntryState s, Blackhole bh) {
        X5455_ExtendedTimestamp ts = new X5455_ExtendedTimestamp();
        ts.setModifyFileTime(FileTime.fromMillis(2000L));
        s.entry.addExtraField(ts);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkAddAsFirstExtraField(FreshEntryState s, Blackhole bh) {
        X5455_ExtendedTimestamp ts = new X5455_ExtendedTimestamp();
        ts.setModifyFileTime(FileTime.fromMillis(3000L));
        s.entry.addAsFirstExtraField(ts);
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkRemoveExtraField(FreshEntryState s, Blackhole bh) {
        X5455_ExtendedTimestamp ts = new X5455_ExtendedTimestamp();
        ts.setModifyFileTime(FileTime.fromMillis(4000L));
        s.entry.addExtraField(ts);
        s.entry.removeExtraField(ts.getHeaderId());
        bh.consume(s.entry);
    }

    @Benchmark
    public void benchmarkRemoveUnparseableExtraFieldData(FreshEntryState s, Blackhole bh) {
        s.entry.setExtra(new byte[] {0, 1, 2, 3});
        s.entry.removeUnparseableExtraFieldData();
        bh.consume(s.entry);
    }
}

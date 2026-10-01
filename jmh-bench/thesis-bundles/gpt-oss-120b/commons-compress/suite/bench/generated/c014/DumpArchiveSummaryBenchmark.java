package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveSummary;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import java.io.IOException;
import java.util.Date;
import java.lang.reflect.Constructor;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveSummaryBenchmark {

    private byte[] header;
    private ZipEncoding encoding;
    private DumpArchiveSummary summary;
    private DumpArchiveSummary otherSummary;
    private Constructor<DumpArchiveSummary> ctor;

    @Setup(Level.Trial)
    public void setUp() throws Exception {
        // Allocate a buffer large enough for the header (at least 900 bytes)
        header = new byte[1024];
        // Fill with deterministic data for a few fields
        // dumpDate (bytes 4-7)
        header[4] = 0x00;
        header[5] = 0x00;
        header[6] = 0x00;
        header[7] = 0x01; // value 1 -> 1000 ms
        // previousDumpDate (bytes 8-11)
        header[8] = 0x00;
        header[9] = 0x00;
        header[10] = 0x00;
        header[11] = 0x02; // value 2
        // volume (bytes 12-15)
        header[12] = 0x00;
        header[13] = 0x00;
        header[14] = 0x00;
        header[15] = 0x03; // value 3
        // flags (bytes 888-891)
        header[888] = 0x00;
        header[889] = 0x00;
        header[890] = 0x00;
        header[891] = (byte) 0x81; // compressed + newHeader bits set for demo

        encoding = ZipEncodingHelper.getZipEncoding("UTF-8");

        // Access the package‑private constructor via reflection
        ctor = DumpArchiveSummary.class.getDeclaredConstructor(byte[].class, ZipEncoding.class);
        ctor.setAccessible(true);

        summary = ctor.newInstance(header, encoding);
        otherSummary = ctor.newInstance(header, encoding);
    }

    // Constructor benchmark
    @Benchmark
    public DumpArchiveSummary benchmarkConstructor() throws Exception {
        return ctor.newInstance(header, encoding);
    }

    // Equality benchmark
    @Benchmark
    public boolean benchmarkEquals() {
        return summary.equals(otherSummary);
    }

    // hashCode benchmark
    @Benchmark
    public int benchmarkHashCode() {
        return summary.hashCode();
    }

    // Getter benchmarks
    @Benchmark
    public String benchmarkGetDevname() {
        return summary.getDevname();
    }

    @Benchmark
    public Date benchmarkGetDumpDate() {
        return summary.getDumpDate();
    }

    @Benchmark
    public String benchmarkGetFilesystem() {
        return summary.getFilesystem();
    }

    @Benchmark
    public int benchmarkGetFirstRecord() {
        return summary.getFirstRecord();
    }

    @Benchmark
    public int benchmarkGetFlags() {
        return summary.getFlags();
    }

    @Benchmark
    public String benchmarkGetHostname() {
        return summary.getHostname();
    }

    @Benchmark
    public String benchmarkGetLabel() {
        return summary.getLabel();
    }

    @Benchmark
    public int benchmarkGetLevel() {
        return summary.getLevel();
    }

    @Benchmark
    public int benchmarkGetNTRec() {
        return summary.getNTRec();
    }

    @Benchmark
    public Date benchmarkGetPreviousDumpDate() {
        return summary.getPreviousDumpDate();
    }

    @Benchmark
    public int benchmarkGetVolume() {
        return summary.getVolume();
    }

    // Boolean flag benchmarks
    @Benchmark
    public boolean benchmarkIsCompressed() {
        return summary.isCompressed();
    }

    @Benchmark
    public boolean benchmarkIsExtendedAttributes() {
        return summary.isExtendedAttributes();
    }

    @Benchmark
    public boolean benchmarkIsMetaDataOnly() {
        return summary.isMetaDataOnly();
    }

    @Benchmark
    public boolean benchmarkIsNewHeader() {
        return summary.isNewHeader();
    }

    @Benchmark
    public boolean benchmarkIsNewInode() {
        return summary.isNewInode();
    }

    // Setter benchmarks (consume the mutated object)
    @Benchmark
    public void benchmarkSetDevname(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setDevname("/dev/sdx1");
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetDumpDate(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setDumpDate(new Date(123456789L));
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetFilesystem(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setFilesystem("/mnt/data");
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetFirstRecord(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setFirstRecord(42);
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetFlags(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setFlags(0xA5A5);
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetHostname(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setHostname("benchmark-host");
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetLabel(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setLabel("test-label");
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetLevel(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setLevel(5);
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetNTRec(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setNTRec(16);
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetPreviousDumpDate(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setPreviousDumpDate(new Date(987654321L));
        bh.consume(s);
    }

    @Benchmark
    public void benchmarkSetVolume(Blackhole bh) throws Exception {
        DumpArchiveSummary s = ctor.newInstance(header, encoding);
        s.setVolume(7);
        bh.consume(s);
    }
}

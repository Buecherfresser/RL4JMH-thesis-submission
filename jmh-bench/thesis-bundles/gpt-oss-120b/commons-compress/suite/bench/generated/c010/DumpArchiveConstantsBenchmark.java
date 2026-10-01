package bench.generated.c010;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumpArchiveConstantsBenchmark {

    private int[] compressionCodes;
    private int[] invalidCompressionCodes;
    private int[] segmentCodes;
    private int[] invalidSegmentCodes;

    private int compIdx;
    private int invCompIdx;
    private int segIdx;
    private int invSegIdx;

    @Setup(Level.Trial)
    public void setUp() {
        compressionCodes = new int[] { -1, 0, 1, 2 };
        invalidCompressionCodes = new int[] { 3, -2, 99 };
        segmentCodes = new int[] { 1, 2, 3, 4, 5, 6 };
        invalidSegmentCodes = new int[] { 0, 7, -1 };
        compIdx = 0;
        invCompIdx = 0;
        segIdx = 0;
        invSegIdx = 0;
    }

    @Benchmark
    public DumpArchiveConstants.COMPRESSION_TYPE benchmarkCompressionFindValid() {
        int code = compressionCodes[compIdx];
        compIdx = (compIdx + 1) % compressionCodes.length;
        return DumpArchiveConstants.COMPRESSION_TYPE.find(code);
    }

    @Benchmark
    public DumpArchiveConstants.COMPRESSION_TYPE benchmarkCompressionFindInvalid() {
        int code = invalidCompressionCodes[invCompIdx];
        invCompIdx = (invCompIdx + 1) % invalidCompressionCodes.length;
        return DumpArchiveConstants.COMPRESSION_TYPE.find(code);
    }

    @Benchmark
    public DumpArchiveConstants.SEGMENT_TYPE benchmarkSegmentFindValid() {
        int code = segmentCodes[segIdx];
        segIdx = (segIdx + 1) % segmentCodes.length;
        return DumpArchiveConstants.SEGMENT_TYPE.find(code);
    }

    @Benchmark
    public DumpArchiveConstants.SEGMENT_TYPE benchmarkSegmentFindInvalid() {
        int code = invalidSegmentCodes[invSegIdx];
        invSegIdx = (invSegIdx + 1) % invalidSegmentCodes.length;
        return DumpArchiveConstants.SEGMENT_TYPE.find(code);
    }

    @Benchmark
    public int benchmarkTpSize() {
        return DumpArchiveConstants.TP_SIZE;
    }

    @Benchmark
    public int benchmarkNtRec() {
        return DumpArchiveConstants.NTREC;
    }

    @Benchmark
    public int benchmarkHighDensityNtRec() {
        return DumpArchiveConstants.HIGH_DENSITY_NTREC;
    }

    @Benchmark
    public int benchmarkOfsMagic() {
        return DumpArchiveConstants.OFS_MAGIC;
    }

    @Benchmark
    public int benchmarkNfsMagic() {
        return DumpArchiveConstants.NFS_MAGIC;
    }

    @Benchmark
    public int benchmarkFsUfs2Magic() {
        return DumpArchiveConstants.FS_UFS2_MAGIC;
    }

    @Benchmark
    public int benchmarkChecksum() {
        return DumpArchiveConstants.CHECKSUM;
    }

    @Benchmark
    public int benchmarkLblSize() {
        return DumpArchiveConstants.LBLSIZE;
    }

    @Benchmark
    public int benchmarkNameLen() {
        return DumpArchiveConstants.NAMELEN;
    }
}

package bench.generated.c031;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.zip.ExtraFieldUtils;
import org.apache.commons.compress.archivers.zip.ExtraFieldUtils.UnparseableExtraField;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.compress.archivers.zip.ZipShort;
import org.apache.commons.compress.archivers.zip.AsiExtraField;
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import java.util.zip.ZipException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ExtraFieldUtilsBenchmark {

    private ZipShort dummyHeader;
    private ZipExtraField asiField;
    private byte[] emptyData;
    private ZipExtraField[] fieldsArray;
    private byte[] simpleExtraFieldBytes;

    @Setup
    public void setup() {
        // Use a dummy header id (0) which is not registered; createExtraField will return UnrecognizedExtraField
        dummyHeader = new ZipShort(0);

        // Instance of AsiExtraField for fill tests
        asiField = new AsiExtraField();

        // Empty payload for fillExtraField (no data)
        emptyData = new byte[0];

        // Array of two different extra fields for merge tests
        fieldsArray = new ZipExtraField[] {
                new AsiExtraField(),
                new X5455_ExtendedTimestamp()
        };

        // Build a minimal extra field byte array: header (2 bytes) + length (2 bytes, zero)
        simpleExtraFieldBytes = new byte[4];
        byte[] headerBytes = dummyHeader.getBytes();
        System.arraycopy(headerBytes, 0, simpleExtraFieldBytes, 0, 2);
        // length bytes are already zero
    }

    @Benchmark
    public ZipExtraField benchmarkCreateExtraField() {
        return ExtraFieldUtils.createExtraField(dummyHeader);
    }

    @Benchmark
    public ZipExtraField benchmarkCreateExtraFieldNoDefault() {
        return ExtraFieldUtils.createExtraFieldNoDefault(dummyHeader);
    }

    @Benchmark
    public ZipExtraField benchmarkFillExtraField() throws ZipException {
        return ExtraFieldUtils.fillExtraField(asiField, emptyData, 0, 0, true);
    }

    @Benchmark
    public byte[] benchmarkMergeCentralDirectoryData() {
        return ExtraFieldUtils.mergeCentralDirectoryData(fieldsArray);
    }

    @Benchmark
    public byte[] benchmarkMergeLocalFileDataData() {
        return ExtraFieldUtils.mergeLocalFileDataData(fieldsArray);
    }

    @Benchmark
    public ZipExtraField[] benchmarkParse() throws ZipException {
        return ExtraFieldUtils.parse(simpleExtraFieldBytes);
    }

    @Benchmark
    public ZipExtraField[] benchmarkParseWithSkip() throws ZipException {
        return ExtraFieldUtils.parse(simpleExtraFieldBytes, true, UnparseableExtraField.SKIP);
    }
}

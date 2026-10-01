package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.LinkOption;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioConstants;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveEntryBenchmark {

    private byte[] testData;
    private String entryName;
    private CpioArchiveEntry newFormatEntry;
    private CpioArchiveEntry oldFormatEntry;

    // Constants for testing
    private static final short FORMAT_NEW = CpioConstants.FORMAT_NEW;
    private static final short FORMAT_OLD_BINARY = CpioConstants.FORMAT_OLD_BINARY;

    @Setup
    public void setup() {
        // 1. Prepare a fixed payload (data for the file entry)
        testData = "This is a test file content for CPIO entry benchmarking.".getBytes(StandardCharsets.UTF_8);
        entryName = "test_file.txt";
        long dataSize = testData.length;

        // 2. Create an entry for the NEW format using format, name, and size
        newFormatEntry = new CpioArchiveEntry(FORMAT_NEW, entryName, dataSize);

        // 3. Create an entry for the OLD_BINARY format using format, name, and size
        oldFormatEntry = new CpioArchiveEntry(FORMAT_OLD_BINARY, entryName, dataSize);
    }

    @Benchmark
    public void newFormat_GetSize(Blackhole bh) {
        bh.consume(newFormatEntry.getSize());
    }

    @Benchmark
    public void newFormat_GetAlignmentBoundary(Blackhole bh) {
        bh.consume(newFormatEntry.getAlignmentBoundary());
    }

    @Benchmark
    public void newFormat_GetDeviceMaj(Blackhole bh) {
        bh.consume(newFormatEntry.getDeviceMaj());
    }

    @Benchmark
    public void newFormat_GetDeviceMin(Blackhole bh) {
        bh.consume(newFormatEntry.getDeviceMin());
    }

    @Benchmark
    public void newFormat_GetChksum(Blackhole bh) {
        bh.consume(newFormatEntry.getChksum());
    }

    @Benchmark
    public void newFormat_GetMode(Blackhole bh) {
        bh.consume(newFormatEntry.getMode());
    }

    @Benchmark
    public void newFormat_GetName(Blackhole bh) {
        bh.consume(newFormatEntry.getName());
    }

    @Benchmark
    public void newFormat_GetFileSize(Blackhole bh) {
        bh.consume(newFormatEntry.getSize());
    }

    @Benchmark
    public void newFormat_GetTime(Blackhole bh) {
        bh.consume(newFormatEntry.getTime());
    }

    @Benchmark
    public void newFormat_GetUID(Blackhole bh) {
        bh.consume(newFormatEntry.getUID());
    }

    @Benchmark
    public void newFormat_IsRegularFile(Blackhole bh) {
        bh.consume(newFormatEntry.isRegularFile());
    }

    @Benchmark
    public void newFormat_IsDirectory(Blackhole bh) {
        bh.consume(newFormatEntry.isDirectory());
    }

    @Benchmark
    public void newFormat_IsSymbolicLink(Blackhole bh) {
        bh.consume(newFormatEntry.isSymbolicLink());
    }

    @Benchmark
    public void oldFormat_GetDevice(Blackhole bh) {
        // Use the pre-built old entry
        bh.consume(oldFormatEntry.getDevice());
    }

    @Benchmark
    public void oldFormat_GetRemoteDevice(Blackhole bh) {
        // Use the pre-built old entry
        bh.consume(oldFormatEntry.getRemoteDevice());
    }
}

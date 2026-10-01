package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioConstants;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveEntryBenchmark {

    // Entries for different formats
    private CpioArchiveEntry newEntry;
    private CpioArchiveEntry oldAsciiEntry;
    private CpioArchiveEntry oldBinaryEntry;
    private CpioArchiveEntry newEntryCopy; // for equals benchmark

    private final Charset charset = StandardCharsets.UTF_8;
    private final String name = "testEntry";

    @Setup(Level.Trial)
    public void setUp() {
        // New format entry
        newEntry = new CpioArchiveEntry(CpioConstants.FORMAT_NEW, name, 1234L);
        newEntry.setMode(CpioConstants.C_ISREG);
        newEntry.setGID(1000L);
        newEntry.setUID(1000L);
        newEntry.setInode(1L);
        newEntry.setDeviceMaj(0L);
        newEntry.setDeviceMin(0L);
        newEntry.setRemoteDeviceMaj(0L);
        newEntry.setRemoteDeviceMin(0L);
        newEntry.setNumberOfLinks(1L);
        newEntry.setTime(1609459200L); // 2021-01-01

        // Old ASCII format entry
        oldAsciiEntry = new CpioArchiveEntry(CpioConstants.FORMAT_OLD_ASCII, name, 567L);
        oldAsciiEntry.setMode(CpioConstants.C_ISREG);
        oldAsciiEntry.setDevice(42L); // old format device
        oldAsciiEntry.setRemoteDevice(99L);
        oldAsciiEntry.setTime(1609459200L);

        // Old Binary format entry
        oldBinaryEntry = new CpioArchiveEntry(CpioConstants.FORMAT_OLD_BINARY, name, 789L);
        oldBinaryEntry.setMode(CpioConstants.C_ISREG);
        oldBinaryEntry.setDevice(24L);
        oldBinaryEntry.setRemoteDevice(77L);
        oldBinaryEntry.setTime(1609459200L);

        // Copy for equals benchmark (same name)
        newEntryCopy = new CpioArchiveEntry(CpioConstants.FORMAT_NEW, name, 1234L);
        newEntryCopy.setMode(CpioConstants.C_ISREG);
    }

    // --------------------- Getter benchmarks ---------------------

    @Benchmark
    public int benchmarkGetAlignmentBoundary() {
        return newEntry.getAlignmentBoundary();
    }

    @Benchmark
    public long benchmarkGetChksum() {
        return newEntry.getChksum();
    }

    @Benchmark
    public int benchmarkGetDataPadCount() {
        return newEntry.getDataPadCount();
    }

    @Benchmark
    public long benchmarkGetDeviceOld() {
        return oldBinaryEntry.getDevice();
    }

    @Benchmark
    public long benchmarkGetDeviceMaj() {
        return newEntry.getDeviceMaj();
    }

    @Benchmark
    public long benchmarkGetDeviceMin() {
        return newEntry.getDeviceMin();
    }

    @Benchmark
    public short benchmarkGetFormat() {
        return newEntry.getFormat();
    }

    @Benchmark
    public long benchmarkGetGID() {
        return newEntry.getGID();
    }

    @Benchmark
    public int benchmarkGetHeaderPadCountCharset() {
        return newEntry.getHeaderPadCount(charset);
    }

    @Benchmark
    public int benchmarkGetHeaderPadCountLong() {
        return newEntry.getHeaderPadCount(10L);
    }

    @Benchmark
    public int benchmarkGetHeaderSize() {
        return newEntry.getHeaderSize();
    }

    @Benchmark
    public long benchmarkGetInode() {
        return newEntry.getInode();
    }

    @Benchmark
    public Date benchmarkGetLastModifiedDate() {
        return newEntry.getLastModifiedDate();
    }

    @Benchmark
    public long benchmarkGetMode() {
        return newEntry.getMode();
    }

    @Benchmark
    public String benchmarkGetName() {
        return newEntry.getName();
    }

    @Benchmark
    public long benchmarkGetNumberOfLinks() {
        return newEntry.getNumberOfLinks();
    }

    @Benchmark
    public long benchmarkGetRemoteDeviceOld() {
        return oldBinaryEntry.getRemoteDevice();
    }

    @Benchmark
    public long benchmarkGetRemoteDeviceMaj() {
        return newEntry.getRemoteDeviceMaj();
    }

    @Benchmark
    public long benchmarkGetRemoteDeviceMin() {
        return newEntry.getRemoteDeviceMin();
    }

    @Benchmark
    public long benchmarkGetSize() {
        return newEntry.getSize();
    }

    @Benchmark
    public long benchmarkGetTime() {
        return newEntry.getTime();
    }

    @Benchmark
    public long benchmarkGetUID() {
        return newEntry.getUID();
    }

    @Benchmark
    public boolean benchmarkIsBlockDevice() {
        return newEntry.isBlockDevice();
    }

    @Benchmark
    public boolean benchmarkIsCharacterDevice() {
        return newEntry.isCharacterDevice();
    }

    @Benchmark
    public boolean benchmarkIsDirectory() {
        return newEntry.isDirectory();
    }

    @Benchmark
    public boolean benchmarkIsNetwork() {
        return newEntry.isNetwork();
    }

    @Benchmark
    public boolean benchmarkIsPipe() {
        return newEntry.isPipe();
    }

    @Benchmark
    public boolean benchmarkIsRegularFile() {
        return newEntry.isRegularFile();
    }

    @Benchmark
    public boolean benchmarkIsSocket() {
        return newEntry.isSocket();
    }

    @Benchmark
    public boolean benchmarkIsSymbolicLink() {
        return newEntry.isSymbolicLink();
    }

    @Benchmark
    public int benchmarkHashCode() {
        return newEntry.hashCode();
    }

    @Benchmark
    public boolean benchmarkEquals() {
        return newEntry.equals(newEntryCopy);
    }

    // --------------------- Setter benchmarks ---------------------

    @Benchmark
    public CpioArchiveEntry benchmarkSetChksum() {
        newEntry.setChksum(0xABCDL);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetDeviceOld() {
        oldBinaryEntry.setDevice(123L);
        return oldBinaryEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetDeviceMaj() {
        newEntry.setDeviceMaj(1L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetDeviceMin() {
        newEntry.setDeviceMin(2L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetGID() {
        newEntry.setGID(2000L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetInode() {
        newEntry.setInode(42L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetMode() {
        newEntry.setMode(CpioConstants.C_ISDIR);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetName() {
        newEntry.setName("newName");
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetNumberOfLinks() {
        newEntry.setNumberOfLinks(3L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetRemoteDeviceOld() {
        oldBinaryEntry.setRemoteDevice(555L);
        return oldBinaryEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetRemoteDeviceMaj() {
        newEntry.setRemoteDeviceMaj(9L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetRemoteDeviceMin() {
        newEntry.setRemoteDeviceMin(8L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetSize() {
        newEntry.setSize(2048L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetTimeLong() {
        newEntry.setTime(1610000000L);
        return newEntry;
    }

    @Benchmark
    public CpioArchiveEntry benchmarkSetUID() {
        newEntry.setUID(3000L);
        return newEntry;
    }
}

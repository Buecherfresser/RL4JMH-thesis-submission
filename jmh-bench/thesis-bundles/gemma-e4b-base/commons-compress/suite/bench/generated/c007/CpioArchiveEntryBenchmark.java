package bench.generated.c007;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveEntryBenchmark {

    private CpioArchiveEntry newFormatEntry;
    private CpioArchiveEntry oldFormatEntry;

    @Setup
    public void setup() {
        // 1. New Format Entry (Regular File, large size, complex name)
        // Using a simple constructor for setup
        newFormatEntry = new CpioArchiveEntry(CpioArchiveEntry.FORMAT_NEW);
        newFormatEntry.setName("path/to/a/very/long/file_name_with_many_components.txt");
        newFormatEntry.setSize(1234567); // 1.2 MB
        newFormatEntry.setGID(1000);
        newFormatEntry.setUID(1000);
        newFormatEntry.setMode(0x81000000L); // Regular file mode
        newFormatEntry.setChksum(0xDEADBEEFL);

        // 2. Old Format Entry (Directory, small size, simple name)
        oldFormatEntry = new CpioArchiveEntry(CpioArchiveEntry.FORMAT_OLD_ASCII);
        oldFormatEntry.setName("directory_root");
        oldFormatEntry.setSize(0); // Directory size is 0
        oldFormatEntry.setMode(0x40000000L); // Directory mode
    }

    @Benchmark
    public String testGetName(Blackhole bh) {
        String name = newFormatEntry.getName();
        bh.consume(name);
        return name;
    }

    @Benchmark
    public long testGetSize(Blackhole bh) {
        long size = newFormatEntry.getSize();
        bh.consume(size);
        return size;
    }

    @Benchmark
    public short testGetFormat(Blackhole bh) {
        short format = newFormatEntry.getFormat();
        bh.consume(format);
        return format;
    }

    @Benchmark
    public int testGetAlignmentBoundary(Blackhole bh) {
        int boundary = newFormatEntry.getAlignmentBoundary();
        bh.consume(boundary);
        return boundary;
    }

    @Benchmark
    public long testGetGID(Blackhole bh) {
        long gid = newFormatEntry.getGID();
        bh.consume(gid);
        return gid;
    }

    @Benchmark
    public long testGetInode(Blackhole bh) {
        long inode = newFormatEntry.getInode();
        bh.consume(inode);
        return inode;
    }

    @Benchmark
    public long testGetUID(Blackhole bh) {
        long uid = newFormatEntry.getUID();
        bh.consume(uid);
        return uid;
    }

    @Benchmark
    public long testGetMode(Blackhole bh) {
        long mode = newFormatEntry.getMode();
        bh.consume(mode);
        return mode;
    }

    @Benchmark
    public long testGetNumberOfLinks(Blackhole bh) {
        long links = newFormatEntry.getNumberOfLinks();
        bh.consume(links);
        return links;
    }

    @Benchmark
    public long testGetChksum(Blackhole bh) {
        // Only supported for new formats
        long chksum = newFormatEntry.getChksum();
        bh.consume(chksum);
        return chksum;
    }

    @Benchmark
    public int testGetDataPadCount(Blackhole bh) {
        // Test calculation logic for new format entry
        int padCount = newFormatEntry.getDataPadCount();
        bh.consume(padCount);
        return padCount;
    }

    @Benchmark
    public int testGetHeaderPadCountLong(Blackhole bh) {
        // Test calculation logic using name size
        // Name length is 50 characters + 1 null byte = 51 bytes
        int padCount = newFormatEntry.getHeaderPadCount(51);
        bh.consume(padCount);
        return padCount;
    }

    @Benchmark
    public int testGetHeaderPadCountCharset(Blackhole bh) {
        // Test calculation logic using Charset
        int padCount = newFormatEntry.getHeaderPadCount(StandardCharsets.UTF_8);
        bh.consume(padCount);
        return padCount;
    }

    @Benchmark
    public void testSetSize(Blackhole bh) {
        // Test setter
        newFormatEntry.setSize(99999);
        bh.consume(newFormatEntry.getSize());
    }

    @Benchmark
    public void testSetMode(Blackhole bh) {
        // Test setter
        newFormatEntry.setMode(0x82000000L);
        bh.consume(newFormatEntry.getMode());
    }

    @Benchmark
    public void testSetName(Blackhole bh) {
        // Test setter
        newFormatEntry.setName("new_name");
        bh.consume(newFormatEntry.getName());
    }
}

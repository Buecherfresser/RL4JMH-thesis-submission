package bench.generated.c024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveEntryBenchmark {

    private TarArchiveEntry entryFromHeader;
    private TarArchiveEntry entryFromManual;
    private byte[] sampleHeaderBytes;
    private String sampleName;
    private Map<String, String> samplePaxHeaders;

    @Setup
    public void setup() throws Exception {
        // 1. Setup for Header Bytes Construction
        // Create a temporary entry to generate a valid header buffer
        // FIX: setLinkFlag does not exist. Use the constructor that accepts name and linkFlag.
        TarArchiveEntry tempEntry = new TarArchiveEntry("test_file.txt", (byte) 0);
        tempEntry.setSize(1024);
        tempEntry.setMode(0100644);
        
        // Generate the header bytes
        sampleHeaderBytes = new byte[512]; // Ensure enough space
        tempEntry.writeEntryHeader(sampleHeaderBytes);
        
        // Construct the entry from the generated header bytes
        entryFromHeader = new TarArchiveEntry(sampleHeaderBytes);

        // 2. Setup for Manual Construction (Name only)
        sampleName = "path/to/my/file.data";
        entryFromManual = new TarArchiveEntry(sampleName);

        // 3. Setup for PAX Header manipulation
        samplePaxHeaders = new HashMap<>();
        samplePaxHeaders.put("path", "pax/path/entry");
        samplePaxHeaders.put("size", "51200");
        samplePaxHeaders.put("mtime", "1678886400.123456789");
    }

    @Benchmark
    public void constructFromHeaderBytes(Blackhole bh) {
        TarArchiveEntry entry = new TarArchiveEntry(sampleHeaderBytes);
        bh.consume(entry);
    }

    @Benchmark
    public void constructFromManualName(Blackhole bh) {
        TarArchiveEntry entry = new TarArchiveEntry(sampleName);
        bh.consume(entry);
    }

    @Benchmark
    public void constructFromHeaderBytesWithPax(Blackhole bh) throws Exception {
        // Use the constructor that accepts global PAX headers
        TarArchiveEntry entry = new TarArchiveEntry(samplePaxHeaders, sampleHeaderBytes, null, false);
        bh.consume(entry);
    }

    @Benchmark
    public void setSizeProperty(Blackhole bh) {
        // Mutating operation
        entryFromManual.setSize(2048);
        bh.consume(entryFromManual.getSize());
    }

    @Benchmark
    public void getNameProperty(Blackhole bh) {
        // Reading operation
        bh.consume(entryFromManual.getName());
    }

    @Benchmark
    public void getModeProperty(Blackhole bh) {
        // Reading operation
        bh.consume(entryFromManual.getMode());
    }

    @Benchmark
    public void setModeProperty(Blackhole bh) {
        // Mutating operation
        entryFromManual.setMode(0755);
        bh.consume(entryFromManual.getMode());
    }

    @Benchmark
    public void addPaxHeader(Blackhole bh) throws Exception {
        // Mutating operation
        entryFromManual.addPaxHeader("custom_key", "custom_value");
        bh.consume(entryFromManual.getExtraPaxHeader("custom_key"));
    }

    @Benchmark
    public void writeEntryHeader(Blackhole bh) throws Exception {
        // Mutating operation (writing state to buffer)
        byte[] outbuf = new byte[512];
        entryFromManual.writeEntryHeader(outbuf);
        bh.consume(outbuf);
    }

    @Benchmark
    public void getLinkFlagProperty(Blackhole bh) {
        // Reading operation
        bh.consume(entryFromManual.getLinkFlag());
    }

    @Benchmark
    public void getRealSizeProperty(Blackhole bh) {
        // Reading operation
        bh.consume(entryFromManual.getRealSize());
    }
}

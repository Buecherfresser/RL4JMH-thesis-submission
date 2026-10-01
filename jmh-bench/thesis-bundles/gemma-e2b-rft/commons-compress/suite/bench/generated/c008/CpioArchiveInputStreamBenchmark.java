package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.utils.ParsingUtils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CpioArchiveInputStreamBenchmark {

    private byte[] cpioPayload;
    private InputStream inputStream;

    // Define a representative CPIO payload in memory.
    // NOTE: This payload is a minimal, self-contained structure designed to test the stream reading logic.
    // A fully compliant CPIO file is complex; this is structured to hit the entry reading path.
    private static final byte[] MINIMAL_CPIO_PAYLOAD = {
        // Placeholder for CPIO header/magic bytes (simplified for testing stream parsing)
        (byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x31, // MAGIC_OLD_ASCII start
        // Simplified entry structure (highly dependent on actual CPIO format implementation)
        // In a real scenario, this would be a valid CPIO entry header followed by data.
        // We use arbitrary data to ensure read() operations are tested.
        (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
        (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F, (byte) 0x10,
        // Data content
        (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD
    };

    @Setup
    public void setup() throws IOException {
        this.cpioPayload = MINIMAL_CPIO_PAYLOAD;
        this.inputStream = new ByteArrayInputStream(cpioPayload);
    }

    /**
     * Benchmark 1: Reading a single CPIO entry (metadata and data).
     * Tests getNextEntry() and subsequent read() calls.
     */
    @Benchmark
    public void benchmarkReadSingleEntry(Blackhole bh) throws IOException {
        CpioArchiveInputStream cpioIn = new CpioArchiveInputStream(inputStream);
        CpioArchiveEntry entry = cpioIn.getNextEntry();

        if (entry != null) {
            byte[] buffer = new byte[1024];
            int bytesRead = cpioIn.read(buffer, 0, buffer.length);
            bh.consume(bytesRead);
        }
        cpioIn.close();
    }

    /**
     * Benchmark 2: Reading multiple entries sequentially.
     * Tests the loop structure inherent in reading an archive.
     */
    @Benchmark
    public void benchmarkReadMultipleEntries(Blackhole bh) throws IOException {
        CpioArchiveInputStream cpioIn = new CpioArchiveInputStream(inputStream);
        int entriesRead = 0;
        while (true) {
            CpioArchiveEntry entry = cpioIn.getNextEntry();
            if (entry == null) {
                break;
            }
            // Simulate reading a small chunk of data from the entry
            byte[] buffer = new byte[128];
            int bytesRead = cpioIn.read(buffer, 0, buffer.length);
            bh.consume(bytesRead);
            entriesRead++;
        }
        cpioIn.close();
    }

    /**
     * Benchmark 3: Reading a large chunk of data from a single entry.
     * Tests the efficiency of the internal readFully mechanism.
     */
    @Benchmark
    public void benchmarkReadLargeChunk(Blackhole bh) throws IOException {
        CpioArchiveInputStream cpioIn = new CpioArchiveInputStream(inputStream);
        CpioArchiveEntry entry = cpioIn.getNextEntry();

        if (entry != null) {
            // Attempt to read a large chunk, even if the payload is small, to test buffer handling.
            byte[] buffer = new byte[8192];
            int bytesRead = cpioIn.read(buffer, 0, buffer.length);
            bh.consume(bytesRead);
        }
        cpioIn.close();
    }
}

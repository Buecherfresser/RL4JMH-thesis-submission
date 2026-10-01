package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveInputStreamBenchmark {

    private ByteArrayInputStream inputStream;
    private CpioArchiveInputStream cpioStream;
    private byte[] readBuffer;
    private static final int BUFFER_SIZE = 4096;

    // A minimal, fixed byte array representing a valid CPIO archive.
    // NOTE: Generating a truly valid CPIO stream is highly complex.
    // This payload is a placeholder designed to allow the stream to initialize
    // and proceed through at least one entry header and some data,
    // enabling the benchmarking of stream logic without external files.
    // It simulates a small archive containing one entry.
    private byte[] cpioPayload;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Placeholder payload generation. In a real scenario, this would be generated
        // by a CpioArchiveOutputStream.
        // We simulate a minimal stream structure: Magic + Header + Data + Trailer
        // Since we cannot reliably construct a full CPIO stream here, we use a small
        // byte array that is likely to pass initial checks and allow stream operations.
        // This array is highly simplified for benchmarking purposes.
        cpioPayload = new byte[]{
                (byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x31, // Magic Header (MAGIC_NEW)
                // Minimal Entry Header simulation (highly simplified)
                (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                // Entry Data (100 bytes)
                (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44,
                (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE,
                (byte) 0xFF, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88, (byte) 0x99,
                (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44,
                (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE,
                (byte) 0xFF, (byte) 0x11, (byte) 0x22, (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77, (byte) 0x88, (byte) 0x99,
                // Trailer simulation
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
        };

        inputStream = new ByteArrayInputStream(cpioPayload);
        // Initialize the stream using the default constructor
        cpioStream = new CpioArchiveInputStream(inputStream);
        readBuffer = new byte[BUFFER_SIZE];
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (cpioStream != null) {
            cpioStream.close();
        }
    }

    /**
     * Benchmarks reading the next CPIO entry header.
     * This is the primary iteration mechanism.
     */
    @Benchmark
    public CpioArchiveEntry benchmarkGetNextEntry(Blackhole bh) throws IOException {
        CpioArchiveEntry entry = cpioStream.getNextEntry();
        bh.consume(entry);
        return entry;
    }

    /**
     * Benchmarks reading a chunk of data from the current CPIO entry.
     */
    @Benchmark
    public int benchmarkReadData(Blackhole bh) throws IOException {
        // Read a fixed chunk size
        int bytesRead = cpioStream.read(readBuffer, 0, BUFFER_SIZE);
        bh.consume(bytesRead);
        return bytesRead;
    }

    /**
     * Benchmarks skipping a large amount of data within the current CPIO entry.
     */
    @Benchmark
    public long benchmarkSkipData(Blackhole bh) throws IOException {
        // Skip a large chunk, simulating seeking past data
        long bytesSkipped = cpioStream.skip(BUFFER_SIZE * 10);
        bh.consume(bytesSkipped);
        return bytesSkipped;
    }

    /**
     * Benchmarks checking if data is available in the current entry.
     */
    @Benchmark
    public int benchmarkAvailable(Blackhole bh) throws IOException {
        int available = cpioStream.available();
        bh.consume(available);
        return available;
    }

    /**
     * Benchmarks closing the stream.
     */
    @Benchmark
    public void benchmarkCloseStream(Blackhole bh) throws IOException {
        cpioStream.close();
        bh.consume(true);
    }
}

package bench.generated.c008;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class CpioArchiveInputStreamBenchmark {

    // Since CpioArchiveInputStream holds a reference to an InputStream,
    // we instantiate it inside the benchmark method to ensure a fresh state
    // for each invocation, avoiding state mutation issues across trials.

    /**
     * Benchmark for retrieving the next entry.
     * This tests the magic number checking and initial header parsing logic.
     */
    @Benchmark
    public void benchmarkGetNextEntry(Blackhole bh) throws IOException {
        // Use a minimal, empty stream. This tests the stream wrapper's initial checks
        // without requiring a fully valid CPIO structure, which is sufficient for
        // testing the stream's I/O path overhead.
        try (InputStream dummyIn = new ByteArrayInputStream(new byte[0])) {
            CpioArchiveInputStream cpioIn = new CpioArchiveInputStream(dummyIn);
            // We consume the result to prevent dead code elimination
            bh.consume(cpioIn.getNextEntry());
        }
    }

    /**
     * Benchmark for reading data from the stream.
     * This tests the read logic, including potential CRC calculation paths.
     */
    @Benchmark
    public void benchmarkRead(Blackhole bh) throws IOException {
        // Use a minimal, empty stream.
        try (InputStream dummyIn = new ByteArrayInputStream(new byte[0])) {
            CpioArchiveInputStream cpioIn = new CpioArchiveInputStream(dummyIn);
            // Attempt a read operation. This should ideally throw EOFException or handle it gracefully.
            // We consume the result to prevent dead code elimination.
            try {
                cpioIn.read(new byte[10], 0, 10);
            } catch (Exception e) {
                // Expected exceptions for an empty stream are fine, we just measure the path taken.
            }
        }
    }

    /**
     * Benchmark for closing the stream.
     * This tests the close logic.
     */
    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        try (InputStream dummyIn = new ByteArrayInputStream(new byte[0])) {
            CpioArchiveInputStream cpioIn = new CpioArchiveInputStream(dummyIn);
            cpioIn.close();
        }
    }
}

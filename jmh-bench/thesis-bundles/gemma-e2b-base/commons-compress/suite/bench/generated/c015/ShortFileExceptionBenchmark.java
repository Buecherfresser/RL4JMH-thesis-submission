package bench.generated.c015;

import org.apache.commons.compress.archivers.dump.DumpArchiveException;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.dump.ShortFileException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class ShortFileExceptionBenchmark {

    // A representative, valid dump archive payload (minimal structure)
    private byte[] validArchiveData;
    // A truncated version of the payload designed to cause an EOF error
    private byte[] truncatedArchiveData;

    // The stream used to read the archive
    private ByteArrayInputStream inputStream;

    @Setup
    public void setup() throws IOException {
        // 1. Create a minimal, valid archive payload.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Simulate writing some data structure bytes
        baos.write("DUMP_HEADER".getBytes());
        baos.write(new byte[100]); // Some content
        byte[] fullData = baos.toByteArray();
        this.validArchiveData = fullData;

        // 2. Create a truncated version of the data.
        // Truncate it significantly to ensure EOF is hit during reading.
        int truncationPoint = fullData.length / 2;
        this.truncatedArchiveData = Arrays.copyOf(fullData, truncationPoint);

        // 3. Initialize the input stream for the benchmark
        this.inputStream = new ByteArrayInputStream(truncatedArchiveData);
    }

    /**
     * Benchmark method to test if reading from a truncated archive stream
     * correctly throws ShortFileException due to unexpected EOF.
     *
     * @param bh Blackhole to consume the result.
     */
    @Benchmark
    public void testShortFileExceptionOnRead(Blackhole bh) {
        try {
            // Attempt to read data using the DumpArchiveInputStream
            DumpArchiveInputStream archiveInputStream = new DumpArchiveInputStream(inputStream);

            // Attempt to read a small chunk of data. This operation should fail
            // because the input stream is truncated.
            int bytesRead = archiveInputStream.read(new byte[10]);

            // If execution reaches here, the exception was not thrown, which is a failure
            // for this specific test case.
            bh.consume(bytesRead);

        } catch (ShortFileException e) {
            // Success: The expected exception was thrown.
            bh.consume(e);
        } catch (IOException e) {
            // Catch other potential IO exceptions that might occur during stream operations
            bh.consume(e);
        }
    }
}

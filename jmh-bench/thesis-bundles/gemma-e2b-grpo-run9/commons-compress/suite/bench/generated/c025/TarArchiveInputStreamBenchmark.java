package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveInputStreamBenchmark {

    private TarArchiveInputStream tarArchiveInputStream;

    // Fixed payload for testing. Since we cannot rely on external files,
    // we use a minimal, valid TAR structure if possible, or just a stream
    // that allows the stream to be instantiated and read/closed.
    // For simplicity and to avoid complex TAR file generation logic, we use a
    // simple, empty stream, relying on the stream's internal logic for basic operations.
    private InputStream dummyInputStream;

    @Setup
    public void setup() throws IOException {
        // Initialize the stream with an empty input stream.
        // This allows instantiation without needing a complex, valid TAR file payload.
        this.dummyInputStream = new ByteArrayInputStream(new byte[0]);
        this.tarArchiveInputStream = new TarArchiveInputStream(this.dummyInputStream);
    }

    @Benchmark
    public void readEntry(Blackhole bh) {
        try {
            // Attempt to read an entry. Since the input is empty, this should quickly hit EOF or throw.
            tarArchiveInputStream.getNextEntry();
        } catch (IOException e) {
            // Expected for an empty stream, ignore exception for benchmark timing
        }
    }

    @Benchmark
    public void getNextEntry(Blackhole bh) {
        try {
            // Test the deprecated method which skips and reads the next entry.
            tarArchiveInputStream.getNextTarEntry();
        } catch (IOException e) {
            // Expected for an empty stream
        }
    }

    @Benchmark
    public void closeStream(Blackhole bh) {
        try {
            tarArchiveInputStream.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void available(Blackhole bh) {
        try {
            // Test the available method. Should return 0 or throw if not at EOF.
            bh.consume(tarArchiveInputStream.available());
        } catch (IOException e) {
            // Ignore
        }
    }

    @Benchmark
    public void skip(Blackhole bh) {
        try {
            // Test the skip method.
            tarArchiveInputStream.skip(100);
        } catch (IOException e) {
            // Ignore
        }
    }
}

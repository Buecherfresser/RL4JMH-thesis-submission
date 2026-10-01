package bench.generated.c063;

import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200CompressorInputStreamBenchmark {

    private Pack200CompressorInputStream inputStream;
    private byte[] compressedData;
    private final int dataSize = 1024 * 1024 * 4; // 4MB payload

    @Setup
    public void setup() throws IOException {
        // 1. Create a representative payload. In a real scenario, this would be a valid Pack200 compressed file.
        // Here, we use random data to simulate the size and complexity of compressed data.
        compressedData = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            compressedData[i] = (byte) (i % 256);
        }

        // 2. Wrap the data in an InputStream for the benchmark
        InputStream input = new ByteArrayInputStream(compressedData);

        // 3. Initialize the subject under test. We use the InputStream constructor.
        this.inputStream = new Pack200CompressorInputStream(input);
    }

    @Benchmark
    public void read_full(Blackhole bh) throws IOException {
        int bytesRead = inputStream.read();
        bh.consume(bytesRead);
    }

    @Benchmark
    public void read_chunk(Blackhole bh) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead = inputStream.read(buffer);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void read_specific_range(Blackhole bh) throws IOException {
        int offset = dataSize / 2;
        int length = 1024;
        byte[] buffer = new byte[length];
        int bytesRead = inputStream.read(buffer, offset, length);
        bh.consume(bytesRead);
    }

    @Benchmark
    public void skip_large_amount(Blackhole bh) throws IOException {
        long skipCount = dataSize / 4;
        long skipped = inputStream.skip(skipCount);
        bh.consume(skipped);
    }

    @Benchmark
    public void close_stream(Blackhole bh) throws IOException {
        inputStream.close();
        bh.consume(null);
    }

    @Benchmark
    public void mark_supported(Blackhole bh) {
        bh.consume(inputStream.markSupported());
    }
}

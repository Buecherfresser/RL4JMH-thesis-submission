package bench.generated.c066;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.compressors.snappy.FramedSnappyCompressorInputStream;
import org.apache.commons.compress.compressors.snappy.FramedSnappyDialect;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class FramedSnappyCompressorInputStreamBenchmark {

    private InputStream inputStream;
    private FramedSnappyCompressorInputStream stream;
    private byte[] testData;
    private final int dataSize = 1024 * 1024; // 1MB test data

    @Setup
    public void setup() throws IOException {
        // Create a large byte array to simulate compressed data
        testData = new byte[dataSize];
        // Fill with some non-zero data to ensure reading operations are meaningful
        for (int i = 0; i < dataSize; i++) {
            testData[i] = (byte) (i % 256);
        }
        
        // Wrap the data in a ByteArrayInputStream to serve as the input stream
        inputStream = new ByteArrayInputStream(testData);
        
        // Initialize the stream under test using a standard dialect and a reasonable block size
        stream = new FramedSnappyCompressorInputStream(inputStream, 4096, FramedSnappyDialect.STANDARD);
    }

    @Benchmark
    public void benchmarkReadSingleByte(Blackhole bh) throws IOException {
        int result = stream.read();
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadBulkSmall(Blackhole bh) throws IOException {
        byte[] buffer = new byte[1024];
        int result = stream.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkReadBulkLarge(Blackhole bh) throws IOException {
        byte[] buffer = new byte[dataSize];
        int result = stream.read(buffer, 0, buffer.length);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAvailable(Blackhole bh) throws IOException {
        int available = stream.available();
        bh.consume(available);
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        stream.close();
        bh.consume(null);
    }
    
    @Benchmark
    public void benchmarkGetCompressedCount(Blackhole bh) throws IOException {
        long count = stream.getCompressedCount();
        bh.consume(count);
    }
}

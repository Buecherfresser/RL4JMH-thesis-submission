package bench.generated.c088;

import org.apache.commons.compress.utils.CountingOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class CountingOutputStreamBenchmark {

    private ByteArrayOutputStream baos;
    private CountingOutputStream cos;
    private byte[] testData;
    private int dataSize;

    @Setup
    public void setup() throws IOException {
        // Setup a reasonably sized payload for testing
        dataSize = 1024 * 1024; // 1 MB
        testData = new byte[dataSize];
        // Fill data with some non-zero values to ensure actual writing occurs
        for (int i = 0; i < dataSize; i++) {
            testData[i] = (byte) (i % 256);
        }

        baos = new ByteArrayOutputStream();
        // Initialize the CountingOutputStream wrapping the ByteArrayOutputStream
        cos = new CountingOutputStream(baos);
    }

    @Benchmark
    public void writeByteArray(Blackhole bh) throws IOException {
        // Test write(byte[] b)
        cos.write(testData);
        long written = cos.getBytesWritten();
        bh.consume(written);
    }

    @Benchmark
    public void writeByteArrayPartial(Blackhole bh) throws IOException {
        // Test write(byte[] b, int off, int len)
        int offset = dataSize / 2;
        int length = dataSize / 4;
        
        cos.write(testData, offset, length);
        long written = cos.getBytesWritten();
        bh.consume(written);
    }

    @Benchmark
    public void writeSingleByte(Blackhole bh) throws IOException {
        // Test write(int b)
        int singleByte = 0xAA;
        cos.write(singleByte);
        long written = cos.getBytesWritten();
        bh.consume(written);
    }
}

package bench.generated.c088;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.CountingOutputStream;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class CountingOutputStreamBenchmark {

    private ByteArrayOutputStream baos;
    private CountingOutputStream countingOutputStream;
    private byte[] testData;
    private final int DATA_SIZE = 1024 * 1024; // 1MB payload

    @Setup
    public void setup() throws IOException {
        baos = new ByteArrayOutputStream();
        countingOutputStream = new CountingOutputStream(baos);
        
        // Create a fixed, non-final payload for testing
        testData = new byte[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            testData[i] = (byte) (i % 256);
        }
    }

    @Benchmark
    public void benchmarkWriteByteArray(Blackhole bh) throws IOException {
        // Test write(byte[] b)
        countingOutputStream.write(testData);
        
        // Consume the result to prevent dead code elimination
        bh.consume(countingOutputStream.getBytesWritten());
    }

    @Benchmark
    public void benchmarkWriteByteArrayWithOffset(Blackhole bh) throws IOException {
        // Test write(byte[] b, int off, int len)
        int offset = DATA_SIZE / 4;
        int length = DATA_SIZE / 2;
        
        countingOutputStream.write(testData, offset, length);
        
        bh.consume(countingOutputStream.getBytesWritten());
    }

    @Benchmark
    public void benchmarkWriteSingleByte(Blackhole bh) throws IOException {
        // Test write(int b)
        int singleByte = 0xAA;
        
        countingOutputStream.write(singleByte);
        
        bh.consume(countingOutputStream.getBytesWritten());
    }
}

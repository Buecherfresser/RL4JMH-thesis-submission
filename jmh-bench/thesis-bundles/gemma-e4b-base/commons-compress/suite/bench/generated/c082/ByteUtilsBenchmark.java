package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.ByteUtils.ByteConsumer;
import org.apache.commons.compress.utils.ByteUtils.ByteSupplier;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteUtilsBenchmark {

    private byte[] testBytes;
    private long testValue;
    private ByteArrayInputStream inputStream;
    private ByteArrayOutputStream outputStream;
    private ByteSupplier supplier;
    private ByteConsumer consumer;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Setup byte array input (10 bytes, enough for various tests)
        testBytes = new byte[10];
        // Fill with some representative data
        for (int i = 0; i < 10; i++) {
            testBytes[i] = (byte) (i % 256);
        }

        // 2. Setup test value
        testValue = 0xDEADBEEFCAFEF00DL;

        // 3. Setup streams
        inputStream = new ByteArrayInputStream(testBytes);
        outputStream = new ByteArrayOutputStream();

        // 4. Setup suppliers/consumers
        supplier = new ByteUtils.InputStreamByteSupplier(inputStream);
        consumer = new ByteUtils.OutputStreamByteConsumer(outputStream);
    }

    // --- Reading Benchmarks (fromLittleEndian) ---

    @Benchmark
    public long readFromByteArrayFull(Blackhole bh) {
        long result = ByteUtils.fromLittleEndian(testBytes);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long readFromByteArrayPartial(Blackhole bh) {
        // Read 4 bytes starting at offset 2
        long result = ByteUtils.fromLittleEndian(testBytes, 2, 4);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long readFromSupplier(Blackhole bh) throws IOException {
        // Read 8 bytes from the supplier
        long result = ByteUtils.fromLittleEndian(supplier, 8);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long readFromDataInput(Blackhole bh) throws IOException {
        // Use the ByteArrayInputStream (which implements InputStream)
        // We must use the InputStream overload since ByteArrayInputStream does not implement DataInput
        long result = ByteUtils.fromLittleEndian((InputStream) inputStream, 8);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public long readFromInputStreamDeprecated(Blackhole bh) throws IOException {
        // Use the ByteArrayInputStream as InputStream (Deprecated method)
        InputStream is = inputStream;
        // Read 8 bytes
        long result = ByteUtils.fromLittleEndian(is, 8);
        bh.consume(result);
        return result;
    }

    // --- Writing Benchmarks (toLittleEndian) ---

    @Benchmark
    public void writeToByteArray(Blackhole bh) {
        // Write 8 bytes into a fresh array
        byte[] targetArray = new byte[8];
        ByteUtils.toLittleEndian(targetArray, testValue, 0, 8);
        bh.consume(targetArray);
    }

    @Benchmark
    public void writeToConsumer(Blackhole bh) throws IOException {
        // Write 8 bytes to the consumer
        ByteUtils.toLittleEndian(consumer, testValue, 8);
        bh.consume(consumer);
    }

    @Benchmark
    public void writeToDataOutputDeprecated(Blackhole bh) throws IOException {
        // Use the ByteArrayOutputStream as OutputStream (since it doesn't implement DataOutput)
        OutputStream os = outputStream;
        // Write 8 bytes using the OutputStream overload
        ByteUtils.toLittleEndian(os, testValue, 8);
        bh.consume(os);
    }

    @Benchmark
    public void writeToOutputStream(Blackhole bh) throws IOException {
        // Write 8 bytes to the output stream
        ByteUtils.toLittleEndian(outputStream, testValue, 8);
        bh.consume(outputStream);
    }
}

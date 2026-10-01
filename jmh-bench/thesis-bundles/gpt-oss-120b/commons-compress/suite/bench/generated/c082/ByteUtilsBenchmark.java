package bench.generated.c082;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.ByteUtils;
import org.apache.commons.compress.utils.ByteUtils.ByteConsumer;
import org.apache.commons.compress.utils.ByteUtils.ByteSupplier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Random;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteUtilsBenchmark {

    private byte[] sourceBytes;
    private byte[] destBytes;

    private ByteArrayInputStream supplierStream;
    private ByteSupplier supplier;

    private ByteArrayOutputStream baosConsumer;
    private ByteConsumer consumer;

    private ByteArrayOutputStream baosDataOut;
    private DataOutputStream dataOut;

    @Setup(Level.Trial)
    public void setUp() {
        Random rnd = new Random(0);
        sourceBytes = new byte[8];
        rnd.nextBytes(sourceBytes);

        destBytes = new byte[8];

        supplierStream = new ByteArrayInputStream(sourceBytes);
        supplierStream.mark(sourceBytes.length);
        supplier = new ByteUtils.InputStreamByteSupplier(supplierStream);

        baosConsumer = new ByteArrayOutputStream();
        consumer = new ByteUtils.OutputStreamByteConsumer(baosConsumer);

        baosDataOut = new ByteArrayOutputStream();
        dataOut = new DataOutputStream(baosDataOut);
    }

    @Benchmark
    public long benchmarkFromLittleEndianFullArray() {
        return ByteUtils.fromLittleEndian(sourceBytes);
    }

    @Benchmark
    public long benchmarkFromLittleEndianPartialArray() {
        // offset 2, length 4
        return ByteUtils.fromLittleEndian(sourceBytes, 2, 4);
    }

    @Benchmark
    public long benchmarkFromLittleEndianByteSupplier() throws IOException {
        supplierStream.reset();
        return ByteUtils.fromLittleEndian(supplier, 8);
    }

    @Benchmark
    public long benchmarkFromLittleEndianDataInput() throws IOException {
        DataInput in = new DataInputStream(new ByteArrayInputStream(sourceBytes));
        return ByteUtils.fromLittleEndian(in, 8);
    }

    @Benchmark
    public long benchmarkFromLittleEndianInputStream() throws IOException {
        InputStream in = new ByteArrayInputStream(sourceBytes);
        return ByteUtils.fromLittleEndian(in, 8);
    }

    @Benchmark
    public byte[] benchmarkToLittleEndianArray() {
        long value = 0x0123456789ABCDEFL;
        ByteUtils.toLittleEndian(destBytes, value, 0, 8);
        return destBytes;
    }

    @Benchmark
    public byte[] benchmarkToLittleEndianByteConsumer() throws IOException {
        baosConsumer.reset();
        long value = 0x0123456789ABCDEFL;
        ByteUtils.toLittleEndian(consumer, value, 8);
        return baosConsumer.toByteArray();
    }

    @Benchmark
    public byte[] benchmarkToLittleEndianOutputStream() throws IOException {
        baosDataOut.reset();
        long value = 0x0123456789ABCDEFL;
        ByteUtils.toLittleEndian((OutputStream) dataOut, value, 8);
        return baosDataOut.toByteArray();
    }
}

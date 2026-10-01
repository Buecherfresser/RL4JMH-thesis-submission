package bench.generated.c082;

import org.apache.commons.compress.utils.ByteUtils;
import org.openjdk.jmh.annotations.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ByteUtilsBenchmark {

    private byte[] inputBytes;
    private byte[] outputBytes;
    private long value;
    private ByteArrayInputStream bais;
    private ByteArrayOutputStream baos;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ByteUtils.ByteSupplier supplier;
    private ByteUtils.ByteConsumer consumer;

    @Setup(Level.Trial)
    public void setup() {
        inputBytes = new byte[8];
        for (int i = 0; i < 8; i++) {
            inputBytes[i] = (byte) i;
        }
        outputBytes = new byte[8];
        value = 0x0123456789ABCDEFL;
        bais = new ByteArrayInputStream(inputBytes);
        bais.mark(0);
        baos = new ByteArrayOutputStream();
        dis = new DataInputStream(bais);
        dos = new DataOutputStream(baos);
        supplier = new ByteUtils.InputStreamByteSupplier(bais);
        consumer = new ByteUtils.OutputStreamByteConsumer(baos);
    }

    @Benchmark
    public long fromLittleEndianArray() {
        return ByteUtils.fromLittleEndian(inputBytes, 0, 8);
    }

    @Benchmark
    public long fromLittleEndianSupplier() throws IOException {
        bais.reset();
        return ByteUtils.fromLittleEndian(supplier, 8);
    }

    @Benchmark
    public long fromLittleEndianDataInput() throws IOException {
        bais.reset();
        return ByteUtils.fromLittleEndian((DataInput) dis, 8);
    }

    @Benchmark
    public long fromLittleEndianInputStream() throws IOException {
        bais.reset();
        return ByteUtils.fromLittleEndian(bais, 8);
    }

    @Benchmark
    public byte[] toLittleEndianArray() {
        ByteUtils.toLittleEndian(outputBytes, value, 0, 8);
        return outputBytes;
    }

    @Benchmark
    public int toLittleEndianConsumer() throws IOException {
        baos.reset();
        ByteUtils.toLittleEndian(consumer, value, 8);
        return baos.size();
    }

    @Benchmark
    public int toLittleEndianDataOutput() throws IOException {
        baos.reset();
        ByteUtils.toLittleEndian((DataOutput) dos, value, 8);
        return baos.size();
    }

    @Benchmark
    public int toLittleEndianOutputStream() throws IOException {
        baos.reset();
        ByteUtils.toLittleEndian(baos, value, 8);
        return baos.size();
    }
}

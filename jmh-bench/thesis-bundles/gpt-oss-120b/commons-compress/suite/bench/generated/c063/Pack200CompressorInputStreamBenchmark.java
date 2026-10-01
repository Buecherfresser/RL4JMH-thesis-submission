package bench.generated.c063;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorInputStream;
import org.apache.commons.compress.java.util.jar.Pack200;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200CompressorInputStreamBenchmark {

    private byte[] pack200Data;
    private final byte[] signature = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xD0, (byte) 0x0D};
    private byte[] readBuffer;

    @Setup
    public void setUp() throws IOException {
        // Create a simple JAR in memory
        ByteArrayOutputStream jarBaos = new ByteArrayOutputStream();
        try (JarOutputStream jos = new JarOutputStream(jarBaos)) {
            JarEntry entry = new JarEntry("test.txt");
            jos.putNextEntry(entry);
            jos.write("Hello Pack200".getBytes(StandardCharsets.UTF_8));
            jos.closeEntry();
        }
        byte[] jarBytes = jarBaos.toByteArray();

        // Pack the JAR using Pack200
        ByteArrayOutputStream packBaos = new ByteArrayOutputStream();
        Pack200.Packer packer = Pack200.newPacker();
        try (InputStream jarIn = new ByteArrayInputStream(jarBytes);
             JarInputStream jis = new JarInputStream(jarIn)) {
            packer.pack(jis, packBaos);
        }
        pack200Data = packBaos.toByteArray();

        // Buffer for read benchmarks
        readBuffer = new byte[1024];
    }

    private Pack200CompressorInputStream newStream() throws IOException {
        return new Pack200CompressorInputStream(new ByteArrayInputStream(pack200Data));
    }

    @Benchmark
    public boolean benchmarkMatchesSignature() {
        return Pack200CompressorInputStream.matches(signature, signature.length);
    }

    @Benchmark
    public int benchmarkAvailable() throws IOException {
        try (Pack200CompressorInputStream in = newStream()) {
            return in.available();
        }
    }

    @Benchmark
    public int benchmarkReadSingleByte() throws IOException {
        try (Pack200CompressorInputStream in = newStream()) {
            return in.read();
        }
    }

    @Benchmark
    public int benchmarkReadByteArray() throws IOException {
        try (Pack200CompressorInputStream in = newStream()) {
            return in.read(readBuffer);
        }
    }

    @Benchmark
    public int benchmarkReadByteArrayOffset() throws IOException {
        try (Pack200CompressorInputStream in = newStream()) {
            int len = readBuffer.length / 2;
            return in.read(readBuffer, 0, len);
        }
    }

    @Benchmark
    public long benchmarkSkip() throws IOException {
        try (Pack200CompressorInputStream in = newStream()) {
            return in.skip(10);
        }
    }

    @Benchmark
    public boolean benchmarkMarkSupported() throws IOException {
        try (Pack200CompressorInputStream in = newStream()) {
            return in.markSupported();
        }
    }

    @Benchmark
    public void benchmarkMark(Blackhole bh) throws IOException {
        try (Pack200CompressorInputStream in = newStream()) {
            in.mark(1024);
            bh.consume(0);
        }
    }

    @Benchmark
    public void benchmarkReset(Blackhole bh) throws IOException {
        try (Pack200CompressorInputStream in = newStream()) {
            in.reset();
            bh.consume(0);
        }
    }

    @Benchmark
    public void benchmarkClose(Blackhole bh) throws IOException {
        Pack200CompressorInputStream in = newStream();
        in.close();
        bh.consume(0);
    }
}

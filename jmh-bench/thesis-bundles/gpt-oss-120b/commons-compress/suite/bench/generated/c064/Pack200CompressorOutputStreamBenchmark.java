package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Strategy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.jar.JarOutputStream;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.nio.charset.StandardCharsets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200CompressorOutputStreamBenchmark {

    private byte[] jarBytes;
    private Map<String, String> properties;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        jarBytes = createSimpleJar();
        properties = new HashMap<>();
        properties.put("keep", "true");
    }

    private byte[] createSimpleJar() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
        try (JarOutputStream jos = new JarOutputStream(baos, manifest)) {
            JarEntry entry = new JarEntry("test.txt");
            jos.putNextEntry(entry);
            byte[] data = "Hello World".getBytes(StandardCharsets.UTF_8);
            jos.write(data);
            jos.closeEntry();
        }
        return baos.toByteArray();
    }

    @Benchmark
    public int compressDefault() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(out);
        compressor.write(jarBytes);
        compressor.finish();
        byte[] result = out.toByteArray();
        return result.length;
    }

    @Benchmark
    public int compressWithProperties() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(out, properties);
        compressor.write(jarBytes);
        compressor.finish();
        byte[] result = out.toByteArray();
        return result.length;
    }

    @Benchmark
    public int compressWithStrategy() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(out, Pack200Strategy.IN_MEMORY);
        compressor.write(jarBytes);
        compressor.finish();
        byte[] result = out.toByteArray();
        return result.length;
    }

    @Benchmark
    public int compressSingleByte() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Pack200CompressorOutputStream compressor = new Pack200CompressorOutputStream(out);
        compressor.write(jarBytes[0] & 0xFF);
        compressor.finish();
        byte[] result = out.toByteArray();
        return result.length;
    }
}

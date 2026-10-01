package bench.generated.c064;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200CompressorOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Strategy;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200CompressorOutputStreamBenchmark {

    private byte[] jarBytes;
    private Map<String, String> props;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        jarBytes = createJar();
        props = new HashMap<>();
        props.put("pack.effort", "9");
        props.put("pack.segment.limit", "-1");
    }

    private byte[] createJar() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (JarOutputStream jos = new JarOutputStream(baos)) {
            // Add a manifest
            JarEntry manifest = new JarEntry("META-INF/MANIFEST.MF");
            jos.putNextEntry(manifest);
            jos.write("Manifest-Version: 1.0\r\n".getBytes("UTF-8"));
            jos.closeEntry();

            // Add a text file
            JarEntry text = new JarEntry("test.txt");
            jos.putNextEntry(text);
            byte[] textData = "Hello, Pack200 benchmark!".getBytes("UTF-8");
            jos.write(textData);
            jos.closeEntry();

            // Add a binary file with some random data
            JarEntry bin = new JarEntry("data.bin");
            jos.putNextEntry(bin);
            byte[] binData = new byte[1024];
            for (int i = 0; i < binData.length; i++) {
                binData[i] = (byte) (i % 256);
            }
            jos.write(binData);
            jos.closeEntry();
        }
        return baos.toByteArray();
    }

    private byte[] compress(byte[] input, Pack200Strategy strategy, Map<String, String> properties) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Pack200CompressorOutputStream cos = new Pack200CompressorOutputStream(baos, strategy, properties)) {
            cos.write(input);
        }
        return baos.toByteArray();
    }

    @Benchmark
    public byte[] compressInMemory() throws IOException {
        return compress(jarBytes, Pack200Strategy.IN_MEMORY, null);
    }

    @Benchmark
    public byte[] compressTempFile() throws IOException {
        return compress(jarBytes, Pack200Strategy.TEMP_FILE, null);
    }

    @Benchmark
    public byte[] compressWithProperties() throws IOException {
        return compress(jarBytes, Pack200Strategy.IN_MEMORY, props);
    }
}

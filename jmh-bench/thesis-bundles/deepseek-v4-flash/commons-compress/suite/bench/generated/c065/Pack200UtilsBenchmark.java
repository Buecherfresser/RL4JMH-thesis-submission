package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.apache.commons.compress.compressors.pack200.Pack200Utils;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200UtilsBenchmark {

    private File inputJar;
    private File outputJar;
    private Map<String, String> props;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Create a temporary directory for the benchmark files
        Path tempDir = Files.createTempDirectory("pack200-bench");
        inputJar = tempDir.resolve("input.jar").toFile();
        outputJar = tempDir.resolve("output.jar").toFile();

        // Build a small JAR with a few entries
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(inputJar.toPath()))) {
            // Add a manifest
            JarEntry manifest = new JarEntry("META-INF/MANIFEST.MF");
            jos.putNextEntry(manifest);
            jos.write("Manifest-Version: 1.0\n".getBytes("UTF-8"));
            jos.closeEntry();

            // Add a few dummy class files
            for (int i = 0; i < 5; i++) {
                String name = "com/example/Class" + i + ".class";
                JarEntry entry = new JarEntry(name);
                jos.putNextEntry(entry);
                // Dummy bytecode (just some bytes)
                byte[] data = new byte[256];
                for (int j = 0; j < data.length; j++) {
                    data[j] = (byte) (j + i);
                }
                jos.write(data);
                jos.closeEntry();
            }
        }

        // Prepare a properties map (optional)
        props = new HashMap<>();
        props.put("pack.effort", "5");
    }

    @Benchmark
    public void normalizeToFile(Blackhole bh) throws IOException {
        Pack200Utils.normalize(inputJar, outputJar);
        bh.consume(outputJar.length());
    }

    @Benchmark
    public void normalizeToFileWithProps(Blackhole bh) throws IOException {
        Pack200Utils.normalize(inputJar, outputJar, props);
        bh.consume(outputJar.length());
    }
}

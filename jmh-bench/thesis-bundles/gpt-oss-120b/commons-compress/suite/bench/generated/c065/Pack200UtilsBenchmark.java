package bench.generated.c065;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.apache.commons.compress.compressors.pack200.Pack200Utils;
import org.apache.commons.compress.java.util.jar.Pack200;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class Pack200UtilsBenchmark {

    private File sourceJar;
    private File targetJar;
    private Map<String, String> props;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() throws IOException {
        // Create a tiny JAR with a single entry in a temporary file
        sourceJar = Files.createTempFile("bench-source", ".jar").toFile();
        try (OutputStream fos = new FileOutputStream(sourceJar);
             JarOutputStream jos = new JarOutputStream(fos)) {
            JarEntry entry = new JarEntry("test.txt");
            byte[] data = "benchmark".getBytes("UTF-8");
            entry.setSize(data.length);
            jos.putNextEntry(entry);
            jos.write(data);
            jos.closeEntry();
        }

        // Target file for normalize(to) benchmarks
        targetJar = Files.createTempFile("bench-target", ".jar").toFile();

        // Empty properties map (will be populated inside the utility)
        props = new HashMap<>();
    }

    @TearDown(org.openjdk.jmh.annotations.Level.Trial)
    public void tearDown() throws IOException {
        if (sourceJar != null && sourceJar.exists()) {
            Files.delete(sourceJar.toPath());
        }
        if (targetJar != null && targetJar.exists()) {
            Files.delete(targetJar.toPath());
        }
    }

    @Benchmark
    public void normalizeInPlace(Blackhole bh) throws IOException {
        // Normalize the source JAR in place
        Pack200Utils.normalize(sourceJar);
        // Consume the resulting file size to prevent dead‑code elimination
        bh.consume(sourceJar.length());
    }

    @Benchmark
    public void normalizeToSeparate(Blackhole bh) throws IOException {
        // Ensure target is clean before each run
        if (targetJar.exists()) {
            Files.delete(targetJar.toPath());
        }
        Files.createFile(targetJar.toPath());

        Pack200Utils.normalize(sourceJar, targetJar);
        bh.consume(targetJar.length());
    }

    @Benchmark
    public void normalizeWithProperties(Blackhole bh) throws IOException {
        // Ensure target is clean before each run
        if (targetJar.exists()) {
            Files.delete(targetJar.toPath());
        }
        Files.createFile(targetJar.toPath());

        Pack200Utils.normalize(sourceJar, targetJar, props);
        bh.consume(targetJar.length());
    }
}

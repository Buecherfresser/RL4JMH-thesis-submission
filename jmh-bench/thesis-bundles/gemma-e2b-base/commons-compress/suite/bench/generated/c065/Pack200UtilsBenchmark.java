package bench.generated.c065;

import org.apache.commons.compress.compressors.pack200.Pack200Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class Pack200UtilsBenchmark {

    private byte[] sourceJarContent;
    private byte[] destinationJarContent;
    private File sourceJarFile;
    private File destinationJarFile;
    private Map<String, String> props;

    @Setup
    public void setup() throws IOException {
        // --- Setup 1: Create a representative JAR payload ---
        // Create a small, non-empty byte array to simulate a JAR file.
        sourceJarContent = createMinimalJarContent();
        destinationJarContent = new byte[sourceJarContent.length]; // Destination size matches source for normalization tests
        props = new HashMap<>();

        // --- Setup 2: Create File objects pointing to in-memory buffers ---
        // Simulate File objects pointing to in-memory buffers.

        sourceJarFile = createTempFileFromBytes(sourceJarContent, "source_jar");
        destinationJarFile = createTempFileFromBytes(destinationJarContent, "dest_jar");
    }

    /**
     * Helper method to create a minimal byte array simulating a JAR file.
     */
    private byte[] createMinimalJarContent() {
        // Create a small, non-empty byte array to simulate a JAR file.
        return new byte[1024 * 10]; // 10KB dummy content
    }

    /**
     * Helper method to create a File object pointing to a byte array.
     */
    private File createTempFileFromBytes(byte[] content, String prefix) throws IOException {
        Path tempPath = Files.createTempFile(prefix, ".jar");
        Files.write(tempPath, content);
        return tempPath.toFile();
    }

    @Benchmark
    public void normalize_from_to(Blackhole bh) throws IOException {
        // Benchmark: normalize(from, to)
        Pack200Utils.normalize(sourceJarFile, destinationJarFile);
        bh.consume(null);
    }

    @Benchmark
    public void normalize_from_to_with_props(Blackhole bh) throws IOException {
        // Benchmark: normalize(from, to, props)
        Pack200Utils.normalize(sourceJarFile, destinationJarFile, props);
        bh.consume(null);
    }

    @Benchmark
    public void normalize_in_place(Blackhole bh) throws IOException {
        // Benchmark: normalize(jar)
        Pack200Utils.normalize(sourceJarFile);
        bh.consume(null);
    }

    @Benchmark
    public void normalize_in_place_with_props(Blackhole bh) throws IOException {
        // Benchmark: normalize(jar, props)
        Pack200Utils.normalize(sourceJarFile, props);
        bh.consume(null);
    }
}

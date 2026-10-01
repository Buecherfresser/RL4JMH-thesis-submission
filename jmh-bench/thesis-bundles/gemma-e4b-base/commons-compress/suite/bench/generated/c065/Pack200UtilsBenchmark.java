package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

    private File inputJarFile;
    private File outputJarFile;
    private Map<String, String> properties;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Create a temporary directory to hold input/output files
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "jmh_pack200_test");
        tempDir.mkdirs();

        // 2. Create a dummy JAR file (Input)
        inputJarFile = new File(tempDir, "input.jar");
        createDummyJar(inputJarFile);

        // 3. Create a temporary output file
        outputJarFile = new File(tempDir, "output.jar");

        // 4. Setup properties map
        properties = new HashMap<>();
    }

    private void createDummyJar(File jarFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(jarFile);
             JarOutputStream jos = new JarOutputStream(fos)) {

            // Add a dummy entry
            JarEntry entry = new JarEntry("test_file.txt");
            jos.putNextEntry(entry);
            jos.write("This is test content.".getBytes());
            jos.closeEntry();
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        // Clean up temporary files and directory
        File tempDir = inputJarFile.getParentFile();
        if (tempDir != null) {
            for (File file : tempDir.listFiles()) {
                file.delete();
            }
            tempDir.delete();
        }
    }

    /**
     * Benchmarks normalize(final File from, final File to, Map<String, String> props)
     */
    @Benchmark
    public void normalizeWithProps(Blackhole bh) throws IOException {
        // Ensure the output file is clean before running
        if (outputJarFile.exists()) {
            outputJarFile.delete();
        }
        
        Pack200Utils.normalize(inputJarFile, outputJarFile, properties);
        
        // Consume the result (the side effect is the creation of outputJarFile)
        bh.consume(outputJarFile.length());
    }

    /**
     * Benchmarks normalize(final File from, final File to)
     */
    @Benchmark
    public void normalizeSimple(Blackhole bh) throws IOException {
        // Ensure the output file is clean before running
        if (outputJarFile.exists()) {
            outputJarFile.delete();
        }
        
        Pack200Utils.normalize(inputJarFile, outputJarFile);
        
        // Consume the result
        bh.consume(outputJarFile.length());
    }

    /**
     * Benchmarks normalize(final File jar, final Map<String, String> props)
     */
    @Benchmark
    public void normalizeInPlaceWithProps(Blackhole bh) throws IOException {
        // Ensure the input file is clean before running (though it shouldn't change)
        // We must ensure the output file (which is the same as input) is ready for overwrite
        if (inputJarFile.exists()) {
            inputJarFile.delete();
        }
        
        // Recreate the input file state for the operation
        createDummyJar(inputJarFile);

        Pack200Utils.normalize(inputJarFile, properties);
        
        // Consume the result (the side effect is the modification of inputJarFile)
        bh.consume(inputJarFile.length());
    }

    /**
     * Benchmarks normalize(final File jar)
     */
    @Benchmark
    public void normalizeInPlaceSimple(Blackhole bh) throws IOException {
        // Ensure the input file is clean before running
        if (inputJarFile.exists()) {
            inputJarFile.delete();
        }
        
        // Recreate the input file state for the operation
        createDummyJar(inputJarFile);

        Pack200Utils.normalize(inputJarFile);
        
        // Consume the result
        bh.consume(inputJarFile.length());
    }
}

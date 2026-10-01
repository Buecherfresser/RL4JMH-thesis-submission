package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import jodd.util.PropertiesUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertiesUtilBenchmark {

    private Properties sampleProperties;
    private Map<String, String> sampleMap;
    private String samplePropertiesString;
    private File tempFile;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Setup Properties Data
        sampleProperties = new Properties();
        sampleProperties.setProperty("key1", "value1");
        sampleProperties.setProperty("key2", "value2");
        sampleProperties.setProperty("key3", "a${key1}b");
        sampleProperties.setProperty("key4", "c${key2}d");

        // 2. Setup Map Data
        sampleMap = Map.of("key1", "value1", "key2", "value2");

        // 3. Setup String Data
        samplePropertiesString = "key1=value1\nkey2=value2\nkey3=a${key1}b\nkey4=c${key2}d";

        // 4. Setup Temporary File for I/O benchmarks
        tempFile = File.createTempFile("properties_test", ".properties");
        tempFile.deleteOnExit();
    }

    // --- String I/O Benchmarks ---

    @Benchmark
    public void createFromString(Blackhole bh) throws IOException {
        Properties p = PropertiesUtil.createFromString(samplePropertiesString);
        bh.consume(p);
    }

    @Benchmark
    public void loadFromString(Blackhole bh) throws IOException {
        Properties p = new Properties();
        PropertiesUtil.loadFromString(p, samplePropertiesString);
        bh.consume(p);
    }

    // --- Subsetting Benchmarks ---

    @Benchmark
    public Properties subset_stripPrefix(Blackhole bh) {
        // Test subsetting with prefix and stripping
        Properties subset = PropertiesUtil.subset(sampleProperties, "key", true);
        return subset;
    }

    @Benchmark
    public Properties subset_noStripPrefix(Blackhole bh) {
        // Test subsetting without stripping
        Properties subset = PropertiesUtil.subset(sampleProperties, "key", false);
        return subset;
    }

    // --- Map Lookup Benchmarks ---

    @Benchmark
    public String getProperty_simple(Blackhole bh) {
        String result = PropertiesUtil.getProperty(sampleMap, "key1");
        return result;
    }

    @Benchmark
    public String getProperty_withDefault(Blackhole bh) {
        String result = PropertiesUtil.getProperty(sampleMap, "nonexistent", "default_value");
        return result;
    }

    // --- Variable Resolution Benchmarks ---

    @Benchmark
    public String resolveProperty_single(Blackhole bh) {
        // Resolve a single property that contains variables
        String result = PropertiesUtil.resolveProperty(sampleMap, "key3");
        return result;
    }

    @Benchmark
    public void resolveAllVariables(Blackhole bh) {
        // Resolve all variables in a Properties object
        Properties p = new Properties();
        p.put("key1", "value1");
        p.put("key2", "value2");
        p.put("key3", "a${key1}b");
        p.put("key4", "c${key2}d");
        
        PropertiesUtil.resolveAllVariables(p);
        bh.consume(p);
    }

    // --- File I/O Benchmarks ---

    @Benchmark
    public void writeToFile(Blackhole bh) throws IOException {
        // Write properties to the temporary file
        PropertiesUtil.writeToFile(sampleProperties, tempFile);
    }

    @Benchmark
    public Properties createFromFile(Blackhole bh) throws IOException {
        // Create properties by reading from the temporary file
        Properties p = PropertiesUtil.createFromFile(tempFile);
        return p;
    }
}

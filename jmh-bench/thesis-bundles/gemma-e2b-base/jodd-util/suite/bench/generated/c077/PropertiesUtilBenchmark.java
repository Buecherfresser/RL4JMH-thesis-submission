package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import jodd.util.PropertiesUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class PropertiesUtilBenchmark {

    private Properties properties;
    private String largeStringData;
    private File tempFile;
    private Properties testProperties;
    private Map<String, String> mapData;

    @Setup
    public void setup() throws IOException {
        // 1. Setup complex Properties object for subsetting and resolution tests
        testProperties = new Properties();
        testProperties.setProperty("app.name", "BenchmarkApp");
        testProperties.setProperty("app.version", "1.0.0");
        testProperties.setProperty("config.path", "/etc/config");
        testProperties.setProperty("config.timeout", "5000");
        testProperties.setProperty("user.name", "Alice");
        testProperties.setProperty("user.email", "alice@example.com");
        testProperties.setProperty("map[key1]", "value1");
        testProperties.setProperty("map[key2]", "value2");
        testProperties.setProperty("map.nested.key", "nestedValue");

        // 2. Setup large string data for string conversion tests
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("key").append(i).append("=value").append(i).append("\n");
        }
        largeStringData = sb.toString();

        // 3. Setup Map data for variable resolution tests
        mapData = new HashMap<>();
        mapData.put("user.name", "Bob"); // Overwrite existing property for resolution test
        mapData.put("map[key1]", "new_value1");
        mapData.put("unknown.key", "should_be_ignored");

        // 4. Setup temporary file for file IO tests
        tempFile = File.createTempFile("props_test", ".properties");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            testProperties.store(fos, "Benchmark Header");
        }
    }

    // --- String Conversion Benchmarks ---

    @Benchmark
    public Properties createFromString(Blackhole bh) throws IOException {
        Properties p = PropertiesUtil.createFromString(largeStringData);
        bh.consume(p);
        return p;
    }

    @Benchmark
    public void loadFromString(Blackhole bh) throws IOException {
        Properties p = new Properties();
        PropertiesUtil.loadFromString(p, largeStringData);
        bh.consume(p);
    }

    // --- File IO Benchmarks (Using temporary file setup) ---

    @Benchmark
    public void writeToFileWithHeader(Blackhole bh) throws IOException {
        PropertiesUtil.writeToFile(testProperties, tempFile.getAbsolutePath(), "Custom Header");
        bh.consume(null);
    }

    @Benchmark
    public void loadFromFile(Blackhole bh) throws IOException {
        Properties p = new Properties();
        PropertiesUtil.loadFromFile(p, tempFile);
        bh.consume(p);
    }

    // --- Subset Benchmarks ---

    @Benchmark
    public Properties subsetWithStrip(Blackhole bh) {
        Properties subset = PropertiesUtil.subset(testProperties, "config.", true);
        bh.consume(subset);
        return subset;
    }

    @Benchmark
    public Properties subsetWithoutStrip(Blackhole bh) {
        Properties subset = PropertiesUtil.subset(testProperties, "app.", false);
        bh.consume(subset);
        return subset;
    }

    // --- Variable Resolution Benchmarks ---

    @Benchmark
    public void resolveAllVariables(Blackhole bh) {
        PropertiesUtil.resolveAllVariables(testProperties);
        bh.consume(testProperties);
    }

    @Benchmark
    public String resolveProperty(Blackhole bh) {
        // Test resolving a key that exists and is a String
        String result = PropertiesUtil.resolveProperty(mapData, "user.name");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String resolvePropertyNotFound(Blackhole bh) {
        // Test resolving a key that does not exist
        String result = PropertiesUtil.resolveProperty(mapData, "non.existent.key");
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String resolvePropertyWithDefault(Blackhole bh) {
        // Test resolving a key that does not exist with a default value
        String result = PropertiesUtil.getProperty(mapData, "missing.key", "DEFAULT_VALUE");
        bh.consume(result);
        return result;
    }
}

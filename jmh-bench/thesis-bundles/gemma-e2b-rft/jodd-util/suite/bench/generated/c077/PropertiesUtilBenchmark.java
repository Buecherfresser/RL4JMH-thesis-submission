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

    private Properties largeProperties;
    private String largeStringData;
    private Map<String, String> mapData;
    private File tempFile;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // 1. Setup large string data for string conversion tests
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("key").append(i).append("=value").append(i).append("\n");
        }
        this.largeStringData = sb.toString();

        // 2. Setup complex Properties object for subsetting and resolution tests
        this.largeProperties = new Properties();
        largeProperties.setProperty("app.name", "BenchmarkApp");
        largeProperties.setProperty("config.version", "1.0");
        largeProperties.setProperty("db.host", "localhost");
        largeProperties.setProperty("db.port", "5432");
        largeProperties.setProperty("user.name", "admin");
        largeProperties.setProperty("user.email", "admin@example.com");
        largeProperties.setProperty("items[0].name", "ItemA");
        largeProperties.setProperty("items[1].name", "ItemB");
        largeProperties.setProperty("items[2].name", "ItemC");
        largeProperties.setProperty("map[key1]", "value1");
        largeProperties.setProperty("map[key2]", "value2");

        // 3. Setup Map data for getProperty tests
        this.mapData = new HashMap<>();
        mapData.put("key1", "mapValue1");
        mapData.put("key2", "mapValue2");
        mapData.put("missingKey", "default");

        // 4. Setup temporary file for file IO tests
        this.tempFile = File.createTempFile("props_test", ".properties");
        // Write some initial content to the temp file
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write("initial_key=initial_value\n".getBytes(StandardCharsets.ISO_8859_1));
        }
    }

    // --- String Conversion Benchmarks ---

    @Benchmark
    public void createFromString(Blackhole bh) throws IOException {
        Properties p = PropertiesUtil.createFromString(largeStringData);
        bh.consume(p);
    }

    @Benchmark
    public void loadFromString(Blackhole bh) throws IOException {
        Properties p = new Properties();
        PropertiesUtil.loadFromString(p, largeStringData);
        bh.consume(p);
    }

    // --- Subsetting Benchmark ---

    @Benchmark
    public void subset(Blackhole bh) {
        Properties subset = PropertiesUtil.subset(largeProperties, "db.", true);
        bh.consume(subset);
    }

    @Benchmark
    public void subsetNoStrip(Blackhole bh) {
        Properties subset = PropertiesUtil.subset(largeProperties, "items[", false);
        bh.consume(subset);
    }

    // --- Variable Resolution Benchmarks ---

    @Benchmark
    public void resolveAllVariables(Blackhole bh) {
        PropertiesUtil.resolveAllVariables(largeProperties);
        bh.consume(largeProperties);
    }

    @Benchmark
    public void resolveProperty(Blackhole bh) {
        String resolvedValue = PropertiesUtil.resolveProperty(mapData, "key1");
        bh.consume(resolvedValue);
    }

    @Benchmark
    public void resolvePropertyMissing(Blackhole bh) {
        String resolvedValue = PropertiesUtil.resolveProperty(mapData, "nonExistentKey");
        bh.consume(resolvedValue);
    }

    // --- Map Property Access Benchmarks ---

    @Benchmark
    public void getProperty(Blackhole bh) {
        String value = PropertiesUtil.getProperty(mapData, "key1");
        bh.consume(value);
    }

    @Benchmark
    public void getPropertyWithDefault(Blackhole bh) {
        String value = PropertiesUtil.getProperty(mapData, "missingKey", "DEFAULT_VALUE");
        bh.consume(value);
    }

    // --- File IO Benchmarks (Using In-Memory Simulation where possible) ---

    @Benchmark
    public void writeToFileInMemory(Blackhole bh) throws IOException {
        Properties p = new Properties();
        p.setProperty("test.key", "test.value");
        // Simulate writing to a file by using a ByteArrayOutputStream and then loading it back
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        p.store(baos, "header");
        
        // We cannot directly call writeToFile(p, ByteArrayOutputStream) as the API requires File or String.
        // We simulate the IO operation by using the File overload with a temporary file, 
        // as this is the only way to test the actual FileOutputStream logic provided in the source.
        PropertiesUtil.writeToFile(p, tempFile, "test_header");
        bh.consume(p);
    }

    @Benchmark
    public void loadFromFile(Blackhole bh) throws IOException {
        Properties p = new Properties();
        // Load from the temporary file created in setup
        PropertiesUtil.loadFromFile(p, tempFile);
        bh.consume(p);
    }
}

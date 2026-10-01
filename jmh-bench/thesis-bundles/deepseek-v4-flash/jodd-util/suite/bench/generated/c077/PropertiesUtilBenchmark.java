package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import jodd.util.PropertiesUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertiesUtilBenchmark {

    private String propertiesData;
    private Properties sourceProps;
    private Map<String, String> mapForGet;
    private Map<String, String> mapForResolve;

    private static final int NUM_PROPERTIES = 1000;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        // Build a large properties string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUM_PROPERTIES; i++) {
            sb.append("key").append(i).append("=value").append(i).append('\n');
        }
        propertiesData = sb.toString();

        // Build a Properties object with many keys, some with prefix
        sourceProps = new Properties();
        for (int i = 0; i < NUM_PROPERTIES; i++) {
            if (i % 2 == 0) {
                sourceProps.setProperty("prefix.key" + i, "value" + i);
            } else {
                sourceProps.setProperty("other.key" + i, "value" + i);
            }
        }

        // Map for getProperty
        mapForGet = new HashMap<>();
        mapForGet.put("key", "value");
        mapForGet.put("missing", "present");

        // Map for resolveProperty
        mapForResolve = new HashMap<>();
        mapForResolve.put("key", "${other}");
        mapForResolve.put("other", "resolved");
    }

    @Benchmark
    public Properties createFromString() throws IOException {
        return PropertiesUtil.createFromString(propertiesData);
    }

    @Benchmark
    public Properties loadFromString() throws IOException {
        Properties p = new Properties();
        PropertiesUtil.loadFromString(p, propertiesData);
        return p;
    }

    @Benchmark
    public Properties subset() {
        return PropertiesUtil.subset(sourceProps, "prefix", true);
    }

    @Benchmark
    public String getProperty() {
        return PropertiesUtil.getProperty(mapForGet, "key");
    }

    @Benchmark
    public String getPropertyWithDefault() {
        return PropertiesUtil.getProperty(mapForGet, "nonexistent", "default");
    }

    @Benchmark
    public String resolveProperty() {
        return PropertiesUtil.resolveProperty(mapForResolve, "key");
    }

    @Benchmark
    public Properties resolveAllVariables() {
        Properties p = new Properties();
        p.setProperty("a", "${b}");
        p.setProperty("b", "value");
        PropertiesUtil.resolveAllVariables(p);
        return p;
    }
}

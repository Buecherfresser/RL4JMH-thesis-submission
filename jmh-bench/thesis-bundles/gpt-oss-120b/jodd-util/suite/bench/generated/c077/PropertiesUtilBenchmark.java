package bench.generated.c077;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import jodd.util.PropertiesUtil;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertiesUtilBenchmark {

    private Properties sourceProperties;
    private String propertiesString;
    private Map<String, String> stringMap;

    @Setup(org.openjdk.jmh.annotations.Level.Trial)
    public void setUp() throws IOException {
        sourceProperties = new Properties();
        sourceProperties.setProperty("pre.one", "1");
        sourceProperties.setProperty("pre.two", "2");
        sourceProperties.setProperty("other", "3");
        sourceProperties.setProperty("key1", "value1");
        sourceProperties.setProperty("key2", "${key1}");
        sourceProperties.setProperty("var1", "hello");
        sourceProperties.setProperty("var2", "${var1} world");
        sourceProperties.setProperty("plain", "plain");

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> e : sourceProperties.entrySet()) {
            sb.append(e.getKey()).append('=').append(e.getValue()).append('\n');
        }
        propertiesString = sb.toString();

        stringMap = new HashMap<>();
        for (Map.Entry<Object, Object> e : sourceProperties.entrySet()) {
            stringMap.put((String) e.getKey(), (String) e.getValue());
        }
    }

    @Benchmark
    public Properties benchmarkCreateFromString() throws IOException {
        return PropertiesUtil.createFromString(propertiesString);
    }

    @Benchmark
    public void benchmarkLoadFromString(Blackhole bh) throws IOException {
        Properties p = new Properties();
        PropertiesUtil.loadFromString(p, propertiesString);
        bh.consume(p);
    }

    @Benchmark
    public Properties benchmarkSubsetStripPrefix() {
        return PropertiesUtil.subset(sourceProperties, "pre", true);
    }

    @Benchmark
    public Properties benchmarkSubsetKeepPrefix() {
        return PropertiesUtil.subset(sourceProperties, "pre", false);
    }

    @Benchmark
    public String benchmarkGetProperty() {
        return PropertiesUtil.getProperty(stringMap, "key1");
    }

    @Benchmark
    public String benchmarkGetPropertyWithDefault() {
        return PropertiesUtil.getProperty(stringMap, "missing", "default");
    }

    @Benchmark
    public void benchmarkResolveProperty(Blackhole bh) {
        String result = PropertiesUtil.resolveProperty(stringMap, "key2");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkResolveAllVariables(Blackhole bh) {
        Properties p = new Properties();
        for (Map.Entry<String, String> e : stringMap.entrySet()) {
            p.setProperty(e.getKey(), e.getValue());
        }
        PropertiesUtil.resolveAllVariables(p);
        bh.consume(p);
    }
}

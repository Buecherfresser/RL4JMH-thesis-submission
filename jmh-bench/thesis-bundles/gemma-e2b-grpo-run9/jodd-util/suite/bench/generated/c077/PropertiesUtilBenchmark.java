package bench.generated.c077;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.File;
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
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class PropertiesUtilBenchmark {

    // Since PropertiesUtil is static, we don't strictly need @State fields,
    // but we can use them if we were testing an instance method.
    // We will rely on local variables or method arguments for input data.

    @Benchmark
    public void benchmarkCreateFromString(Blackhole bh) {
        try {
            // Use a moderately complex string to ensure some processing occurs
            String data = "key1=value1\nkey2=value2";
            PropertiesUtil.createFromString(data);
        } catch (IOException e) {
            // Ignore IO exceptions for this benchmark if they occur during setup/teardown
        }
    }

    @Benchmark
    public void benchmarkLoadFromString(Blackhole bh) {
        try {
            String data = "key1=value1\nkey2=value2";
            Properties p = new Properties();
            PropertiesUtil.loadFromString(p, data);
        } catch (IOException e) {
            // Ignore IO exceptions
        }
    }

    @Benchmark
    public void benchmarkSubset(Blackhole bh) {
        try {
            Properties p = new Properties();
            // Populate properties minimally to test subset logic
            p.setProperty("prefix.key1", "value1");
            p.setProperty("other.key2", "value2");

            Properties subset = PropertiesUtil.subset(p, "prefix.", false);
            bh.consume(subset);
        } catch (Exception e) {
            // Catch potential exceptions during subset operation
        }
    }

    @Benchmark
    public void benchmarkGetPropertyFromMap(Blackhole bh) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("test.key", "test.value");
            String result = PropertiesUtil.getProperty(map, "test.key");
            bh.consume(result);
        } catch (Exception e) {
            // Catch potential exceptions
        }
    }

    @Benchmark
    public void benchmarkGetPropertyWithDefault(Blackhole bh) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("test.key", "test.value");
            String result = PropertiesUtil.getProperty(map, "non.existent.key", "default_value");
            bh.consume(result);
        } catch (Exception e) {
            // Catch potential exceptions
        }
    }

    @Benchmark
    public void benchmarkResolveAllVariables(Blackhole bh) {
        try {
            Properties p = new Properties();
            // Set a property that relies on a template macro (if the implementation uses it)
            p.setProperty("config.path", "base/path");

            PropertiesUtil.resolveAllVariables(p);
            bh.consume(p);
        } catch (Exception e) {
            // Catch potential exceptions
        }
    }
}

package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.DumperOptions.NonPrintableStyle;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumperOptionsBenchmark {

    private DumperOptions options;

    @Setup
    public void setup() {
        // Initialize a baseline DumperOptions instance
        options = new DumperOptions();
        
        // Set some default configurations for testing purposes
        options.setDefaultScalarStyle(ScalarStyle.PLAIN);
        options.setDefaultFlowStyle(FlowStyle.AUTO);
        options.setIndent(2);
        options.setLineBreak(LineBreak.UNIX);
        options.setVersion(Version.V1_1);
    }

    @Benchmark
    public void benchmarkDefaultConfiguration(Blackhole bh) {
        // Test the baseline configuration state
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkScalarStyleSetting(Blackhole bh) {
        // Test setting a specific scalar style
        options.setDefaultScalarStyle(ScalarStyle.DOUBLE_QUOTED);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkFlowStyleSetting(Blackhole bh) {
        // Test setting a specific flow style
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkIndentationSetting(Blackhole bh) {
        // Test setting indentation
        options.setIndent(4);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkLineBreakSetting(Blackhole bh) {
        // Test setting line break
        options.setLineBreak(LineBreak.WIN);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkVersionSetting(Blackhole bh) {
        // Test setting a specific version
        options.setVersion(Version.V1_0);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkNonPrintableStyleSetting(Blackhole bh) {
        // Test setting non-printable style
        options.setNonPrintableStyle(NonPrintableStyle.BINARY);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkTagsSetting(Blackhole bh) {
        // Test setting tags map
        Map<String, String> tags = Map.of("custom_tag", "value");
        options.setTags(tags);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkAllowReadOnlyPropertiesSetting(Blackhole bh) {
        // Test setting allowReadOnlyProperties
        options.setAllowReadOnlyProperties(true);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkMaxSimpleKeyLengthSetting(Blackhole bh) {
        // Test setting max simple key length
        options.setMaxSimpleKeyLength(512);
        bh.consume(options);
    }
}

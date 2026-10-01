package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
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

    private DumperOptions defaultOptions;
    private DumperOptions indentedOptions;
    private DumperOptions prettyFlowOptions;
    private DumperOptions unicodeOptions;

    @Setup
    public void setup() {
        // 1. Setup Default Options
        defaultOptions = new DumperOptions();
        defaultOptions.setDefaultScalarStyle(ScalarStyle.PLAIN);
        defaultOptions.setDefaultFlowStyle(FlowStyle.AUTO);
        defaultOptions.setIndent(2);
        defaultOptions.setLineBreak(LineBreak.UNIX);

        // 2. Setup Indented Options
        indentedOptions = new DumperOptions();
        indentedOptions.setDefaultScalarStyle(ScalarStyle.LITERAL);
        indentedOptions.setIndent(4);
        indentedOptions.setLineBreak(LineBreak.WIN);

        // 3. Setup Pretty Flow Options
        prettyFlowOptions = new DumperOptions();
        prettyFlowOptions.setDefaultFlowStyle(FlowStyle.BLOCK);
        prettyFlowOptions.setPrettyFlow(true);
        prettyFlowOptions.setIndent(2);

        // 4. Setup Unicode Options
        unicodeOptions = new DumperOptions();
        unicodeOptions.setAllowUnicode(false);
    }

    @Benchmark
    public void benchmarkDefaultScalarStyle(Blackhole bh) {
        DumperOptions options = defaultOptions;
        bh.consume(options.getDefaultScalarStyle());
    }

    @Benchmark
    public void benchmarkIndentedOptions(Blackhole bh) {
        DumperOptions options = indentedOptions;
        bh.consume(options.getIndent());
    }

    @Benchmark
    public void benchmarkPrettyFlowOptions(Blackhole bh) {
        DumperOptions options = prettyFlowOptions;
        bh.consume(options.isPrettyFlow());
    }

    @Benchmark
    public void benchmarkUnicodeOptions(Blackhole bh) {
        DumperOptions options = unicodeOptions;
        bh.consume(options.isAllowUnicode());
    }

    @Benchmark
    public void benchmarkSetVersion(Blackhole bh) {
        DumperOptions options = defaultOptions;
        options.setVersion(Version.V1_1);
        bh.consume(options.getVersion());
    }

    @Benchmark
    public void benchmarkSetNonPrintableStyle(Blackhole bh) {
        DumperOptions options = defaultOptions;
        options.setNonPrintableStyle(NonPrintableStyle.BINARY);
        bh.consume(options.getNonPrintableStyle());
    }

    @Benchmark
    public void benchmarkSetTags(Blackhole bh) {
        DumperOptions options = defaultOptions;
        Map<String, String> tags = Map.of("custom_tag", "value");
        options.setTags(tags);
        bh.consume(options.getTags());
    }

    @Benchmark
    public void benchmarkSetMaxSimpleKeyLength(Blackhole bh) {
        DumperOptions options = defaultOptions;
        options.setMaxSimpleKeyLength(512);
        bh.consume(options.getMaxSimpleKeyLength());
    }

    @Benchmark
    public void benchmarkSetAllowReadOnlyProperties(Blackhole bh) {
        DumperOptions options = defaultOptions;
        options.setAllowReadOnlyProperties(true);
        bh.consume(options.isAllowReadOnlyProperties());
    }

    @Benchmark
    public void benchmarkSetTimeZone(Blackhole bh) {
        DumperOptions options = defaultOptions;
        TimeZone tz = TimeZone.getTimeZone("UTC");
        options.setTimeZone(tz);
        bh.consume(options.getTimeZone());
    }

    @Benchmark
    public void benchmarkSetAnchorGenerator(Blackhole bh) {
        DumperOptions options = defaultOptions;
        org.yaml.snakeyaml.serializer.AnchorGenerator customGenerator = new org.yaml.snakeyaml.serializer.NumberAnchorGenerator(100);
        options.setAnchorGenerator(customGenerator);
        bh.consume(options.getAnchorGenerator());
    }
}

package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import java.util.Map;
import java.util.TimeZone;
import java.util.HashMap;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.DumperOptions.NonPrintableStyle;
import org.yaml.snakeyaml.serializer.AnchorGenerator;
import org.yaml.snakeyaml.serializer.NumberAnchorGenerator;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumperOptionsBenchmark {

    private DumperOptions options;

    @Setup(Level.Trial)
    public void setup() {
        options = new DumperOptions();
    }

    // --- Unicode ---
    @Benchmark
    public void testSetAllowUnicode(Blackhole bh) {
        options.setAllowUnicode(false);
        bh.consume(options.isAllowUnicode());
    }

    // --- Scalar Style ---
    @Benchmark
    public void testSetDefaultScalarStyle(Blackhole bh) {
        options.setDefaultScalarStyle(ScalarStyle.DOUBLE_QUOTED);
        bh.consume(options.getDefaultScalarStyle());
    }

    // --- Indent ---
    @Benchmark
    public void testSetIndent(Blackhole bh) {
        options.setIndent(4);
        bh.consume(options.getIndent());
    }

    // --- Indicator Indent ---
    @Benchmark
    public void testSetIndicatorIndent(Blackhole bh) {
        options.setIndicatorIndent(2);
        bh.consume(options.getIndicatorIndent());
    }

    // --- Indent With Indicator ---
    @Benchmark
    public void testSetIndentWithIndicator(Blackhole bh) {
        options.setIndentWithIndicator(true);
        bh.consume(options.getIndentWithIndicator());
    }

    // --- Version ---
    @Benchmark
    public void testSetVersion(Blackhole bh) {
        options.setVersion(Version.V1_1);
        bh.consume(options.getVersion());
    }

    // --- Canonical ---
    @Benchmark
    public void testSetCanonical(Blackhole bh) {
        options.setCanonical(true);
        bh.consume(options.isCanonical());
    }

    // --- Pretty Flow ---
    @Benchmark
    public void testSetPrettyFlow(Blackhole bh) {
        options.setPrettyFlow(true);
        bh.consume(options.isPrettyFlow());
    }

    // --- Width ---
    @Benchmark
    public void testSetWidth(Blackhole bh) {
        options.setWidth(120);
        bh.consume(options.getWidth());
    }

    // --- Split Lines ---
    @Benchmark
    public void testSetSplitLines(Blackhole bh) {
        options.setSplitLines(false);
        bh.consume(options.getSplitLines());
    }

    // --- Line Break ---
    @Benchmark
    public void testSetLineBreak(Blackhole bh) {
        options.setLineBreak(LineBreak.WIN);
        bh.consume(options.getLineBreak());
    }

    // --- Flow Style ---
    @Benchmark
    public void testSetDefaultFlowStyle(Blackhole bh) {
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        bh.consume(options.getDefaultFlowStyle());
    }

    // --- Explicit Start ---
    @Benchmark
    public void testSetExplicitStart(Blackhole bh) {
        options.setExplicitStart(true);
        bh.consume(options.isExplicitStart());
    }

    // --- Explicit End ---
    @Benchmark
    public void testSetExplicitEnd(Blackhole bh) {
        options.setExplicitEnd(true);
        bh.consume(options.isExplicitEnd());
    }

    // --- Tags ---
    @Benchmark
    public void testSetTags(Blackhole bh) {
        Map<String, String> tags = new HashMap<>();
        tags.put("myTag", "value");
        options.setTags(tags);
        bh.consume(options.getTags());
    }

    // --- Allow Read Only Properties ---
    @Benchmark
    public void testSetAllowReadOnlyProperties(Blackhole bh) {
        options.setAllowReadOnlyProperties(true);
        bh.consume(options.isAllowReadOnlyProperties());
    }

    // --- Time Zone ---
    @Benchmark
    public void testSetTimeZone(Blackhole bh) {
        options.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        bh.consume(options.getTimeZone());
    }

    // --- Anchor Generator ---
    @Benchmark
    public void testSetAnchorGenerator(Blackhole bh) {
        AnchorGenerator generator = new NumberAnchorGenerator(1);
        options.setAnchorGenerator(generator);
        bh.consume(options.getAnchorGenerator());
    }

    // --- Max Simple Key Length ---
    @Benchmark
    public void testSetMaxSimpleKeyLength(Blackhole bh) {
        options.setMaxSimpleKeyLength(256);
        bh.consume(options.getMaxSimpleKeyLength());
    }

    // --- Process Comments ---
    @Benchmark
    public void testSetProcessComments(Blackhole bh) {
        options.setProcessComments(true);
        bh.consume(options.isProcessComments());
    }

    // --- Non Printable Style ---
    @Benchmark
    public void testSetNonPrintableStyle(Blackhole bh) {
        options.setNonPrintableStyle(NonPrintableStyle.ESCAPE);
        bh.consume(options.getNonPrintableStyle());
    }

    // --- Dereference Aliases ---
    @Benchmark
    public void testSetDereferenceAliases(Blackhole bh) {
        options.setDereferenceAliases(true);
        bh.consume(options.isDereferenceAliases());
    }
}

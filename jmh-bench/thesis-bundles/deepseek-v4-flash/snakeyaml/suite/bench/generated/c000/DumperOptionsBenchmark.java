package bench.generated.c000;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.serializer.AnchorGenerator;
import org.yaml.snakeyaml.serializer.NumberAnchorGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumperOptionsBenchmark {

    private DumperOptions options;
    private Map<String, String> tags;
    private TimeZone timeZone;
    private AnchorGenerator anchorGenerator;

    @Setup(Level.Trial)
    public void setup() {
        options = new DumperOptions();
        // Pre-set values for getter benchmarks
        options.setIndent(4);
        options.setWidth(100);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setLineBreak(DumperOptions.LineBreak.UNIX);
        options.setCanonical(true);
        options.setPrettyFlow(true);
        options.setExplicitStart(true);
        options.setExplicitEnd(true);
        options.setVersion(DumperOptions.Version.V1_1);
        tags = new HashMap<>();
        tags.put("!foo", "tag:example.com,2000:foo");
        options.setTags(tags);
        options.setAllowUnicode(false);
        options.setAllowReadOnlyProperties(true);
        timeZone = TimeZone.getTimeZone("UTC");
        options.setTimeZone(timeZone);
        anchorGenerator = new NumberAnchorGenerator(0);
        options.setAnchorGenerator(anchorGenerator);
        options.setMaxSimpleKeyLength(256);
        options.setProcessComments(true);
        options.setNonPrintableStyle(DumperOptions.NonPrintableStyle.ESCAPE);
        options.setDereferenceAliases(true);
        options.setIndicatorIndent(2);
        options.setIndentWithIndicator(true);
        options.setSplitLines(false);
    }

    // --- Setters ---

    @Benchmark
    public void setIndent() {
        options.setIndent(4);
    }

    @Benchmark
    public void setWidth() {
        options.setWidth(100);
    }

    @Benchmark
    public void setDefaultScalarStyle() {
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.SINGLE_QUOTED);
    }

    @Benchmark
    public void setDefaultFlowStyle() {
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
    }

    @Benchmark
    public void setLineBreak() {
        options.setLineBreak(DumperOptions.LineBreak.WIN);
    }

    @Benchmark
    public void setCanonical() {
        options.setCanonical(false);
    }

    @Benchmark
    public void setPrettyFlow() {
        options.setPrettyFlow(false);
    }

    @Benchmark
    public void setExplicitStart() {
        options.setExplicitStart(false);
    }

    @Benchmark
    public void setExplicitEnd() {
        options.setExplicitEnd(false);
    }

    @Benchmark
    public void setVersion() {
        options.setVersion(DumperOptions.Version.V1_0);
    }

    @Benchmark
    public void setTags() {
        options.setTags(tags);
    }

    @Benchmark
    public void setAllowUnicode() {
        options.setAllowUnicode(true);
    }

    @Benchmark
    public void setAllowReadOnlyProperties() {
        options.setAllowReadOnlyProperties(false);
    }

    @Benchmark
    public void setTimeZone() {
        options.setTimeZone(timeZone);
    }

    @Benchmark
    public void setAnchorGenerator() {
        options.setAnchorGenerator(anchorGenerator);
    }

    @Benchmark
    public void setMaxSimpleKeyLength() {
        options.setMaxSimpleKeyLength(128);
    }

    @Benchmark
    public void setProcessComments() {
        options.setProcessComments(false);
    }

    @Benchmark
    public void setNonPrintableStyle() {
        options.setNonPrintableStyle(DumperOptions.NonPrintableStyle.BINARY);
    }

    @Benchmark
    public void setDereferenceAliases() {
        options.setDereferenceAliases(false);
    }

    @Benchmark
    public void setIndicatorIndent() {
        options.setIndicatorIndent(0);
    }

    @Benchmark
    public void setIndentWithIndicator() {
        options.setIndentWithIndicator(false);
    }

    @Benchmark
    public void setSplitLines() {
        options.setSplitLines(true);
    }

    // --- Getters ---

    @Benchmark
    public void getIndent(Blackhole bh) {
        bh.consume(options.getIndent());
    }

    @Benchmark
    public void getWidth(Blackhole bh) {
        bh.consume(options.getWidth());
    }

    @Benchmark
    public void getDefaultScalarStyle(Blackhole bh) {
        bh.consume(options.getDefaultScalarStyle());
    }

    @Benchmark
    public void getDefaultFlowStyle(Blackhole bh) {
        bh.consume(options.getDefaultFlowStyle());
    }

    @Benchmark
    public void getLineBreak(Blackhole bh) {
        bh.consume(options.getLineBreak());
    }

    @Benchmark
    public void isCanonical(Blackhole bh) {
        bh.consume(options.isCanonical());
    }

    @Benchmark
    public void isPrettyFlow(Blackhole bh) {
        bh.consume(options.isPrettyFlow());
    }

    @Benchmark
    public void isExplicitStart(Blackhole bh) {
        bh.consume(options.isExplicitStart());
    }

    @Benchmark
    public void isExplicitEnd(Blackhole bh) {
        bh.consume(options.isExplicitEnd());
    }

    @Benchmark
    public void getVersion(Blackhole bh) {
        bh.consume(options.getVersion());
    }

    @Benchmark
    public void getTags(Blackhole bh) {
        bh.consume(options.getTags());
    }

    @Benchmark
    public void isAllowUnicode(Blackhole bh) {
        bh.consume(options.isAllowUnicode());
    }

    @Benchmark
    public void isAllowReadOnlyProperties(Blackhole bh) {
        bh.consume(options.isAllowReadOnlyProperties());
    }

    @Benchmark
    public void getTimeZone(Blackhole bh) {
        bh.consume(options.getTimeZone());
    }

    @Benchmark
    public void getAnchorGenerator(Blackhole bh) {
        bh.consume(options.getAnchorGenerator());
    }

    @Benchmark
    public void getMaxSimpleKeyLength(Blackhole bh) {
        bh.consume(options.getMaxSimpleKeyLength());
    }

    @Benchmark
    public void isProcessComments(Blackhole bh) {
        bh.consume(options.isProcessComments());
    }

    @Benchmark
    public void getNonPrintableStyle(Blackhole bh) {
        bh.consume(options.getNonPrintableStyle());
    }

    @Benchmark
    public void isDereferenceAliases(Blackhole bh) {
        bh.consume(options.isDereferenceAliases());
    }

    @Benchmark
    public void getIndicatorIndent(Blackhole bh) {
        bh.consume(options.getIndicatorIndent());
    }

    @Benchmark
    public void getIndentWithIndicator(Blackhole bh) {
        bh.consume(options.getIndentWithIndicator());
    }

    @Benchmark
    public void getSplitLines(Blackhole bh) {
        bh.consume(options.getSplitLines());
    }

    // --- Enum methods (nested in DumperOptions) ---

    @Benchmark
    public void scalarStyleCreateStyle(Blackhole bh) {
        bh.consume(DumperOptions.ScalarStyle.createStyle('"'));
    }

    @Benchmark
    public void scalarStyleGetChar(Blackhole bh) {
        bh.consume(DumperOptions.ScalarStyle.DOUBLE_QUOTED.getChar());
    }

    @Benchmark
    public void scalarStyleToString(Blackhole bh) {
        bh.consume(DumperOptions.ScalarStyle.PLAIN.toString());
    }

    @Benchmark
    public void flowStyleToString(Blackhole bh) {
        bh.consume(DumperOptions.FlowStyle.AUTO.toString());
    }

    @Benchmark
    public void lineBreakGetPlatformLineBreak(Blackhole bh) {
        bh.consume(DumperOptions.LineBreak.getPlatformLineBreak());
    }

    @Benchmark
    public void lineBreakGetString(Blackhole bh) {
        bh.consume(DumperOptions.LineBreak.UNIX.getString());
    }

    @Benchmark
    public void versionGetRepresentation(Blackhole bh) {
        bh.consume(DumperOptions.Version.V1_1.getRepresentation());
    }

    @Benchmark
    public void versionMajor(Blackhole bh) {
        bh.consume(DumperOptions.Version.V1_1.major());
    }

    @Benchmark
    public void versionMinor(Blackhole bh) {
        bh.consume(DumperOptions.Version.V1_1.minor());
    }
}

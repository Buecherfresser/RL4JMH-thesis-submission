package bench.generated.c000;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.DumperOptions.NonPrintableStyle;
import org.yaml.snakeyaml.serializer.AnchorGenerator;
import org.yaml.snakeyaml.serializer.NumberAnchorGenerator;
import java.util.Map;
import java.util.Collections;
import java.util.TimeZone;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DumperOptionsBenchmark {

    private DumperOptions options;

    // Input values for setters
    private ScalarStyle scalarStyle;
    private FlowStyle flowStyle;
    private LineBreak lineBreak;
    private Version version;
    private int indent;
    private int indicatorIndent;
    private boolean indentWithIndicator;
    private boolean canonical;
    private boolean prettyFlow;
    private int width;
    private boolean splitLines;
    private boolean explicitStart;
    private boolean explicitEnd;
    private Map<String, String> tags;
    private boolean allowReadOnlyProperties;
    private TimeZone timeZone;
    private int maxSimpleKeyLength;
    private boolean processComments;
    private NonPrintableStyle nonPrintableStyle;
    private boolean dereferenceAliases;
    private AnchorGenerator anchorGenerator;

    @Setup
    public void setup() {
        options = new DumperOptions();

        scalarStyle = ScalarStyle.DOUBLE_QUOTED;
        flowStyle = FlowStyle.FLOW;
        lineBreak = LineBreak.UNIX;
        version = Version.V1_1;
        indent = 4;
        indicatorIndent = 2;
        indentWithIndicator = true;
        canonical = true;
        prettyFlow = true;
        width = 120;
        splitLines = false;
        explicitStart = true;
        explicitEnd = true;
        tags = Collections.singletonMap("!!int", "tag:yaml.org,2002:int");
        allowReadOnlyProperties = true;
        timeZone = TimeZone.getTimeZone("UTC");
        maxSimpleKeyLength = 256;
        processComments = true;
        nonPrintableStyle = NonPrintableStyle.ESCAPE;
        dereferenceAliases = true;
        anchorGenerator = new NumberAnchorGenerator(5);
    }

    // Getters

    @Benchmark
    public ScalarStyle benchmarkGetDefaultScalarStyle() {
        return options.getDefaultScalarStyle();
    }

    @Benchmark
    public FlowStyle benchmarkGetDefaultFlowStyle() {
        return options.getDefaultFlowStyle();
    }

    @Benchmark
    public int benchmarkGetIndent() {
        return options.getIndent();
    }

    @Benchmark
    public int benchmarkGetIndicatorIndent() {
        return options.getIndicatorIndent();
    }

    @Benchmark
    public boolean benchmarkGetIndentWithIndicator() {
        return options.getIndentWithIndicator();
    }

    @Benchmark
    public boolean benchmarkIsCanonical() {
        return options.isCanonical();
    }

    @Benchmark
    public boolean benchmarkIsPrettyFlow() {
        return options.isPrettyFlow();
    }

    @Benchmark
    public int benchmarkGetWidth() {
        return options.getWidth();
    }

    @Benchmark
    public boolean benchmarkGetSplitLines() {
        return options.getSplitLines();
    }

    @Benchmark
    public LineBreak benchmarkGetLineBreak() {
        return options.getLineBreak();
    }

    @Benchmark
    public boolean benchmarkIsExplicitStart() {
        return options.isExplicitStart();
    }

    @Benchmark
    public boolean benchmarkIsExplicitEnd() {
        return options.isExplicitEnd();
    }

    @Benchmark
    public Map<String, String> benchmarkGetTags() {
        return options.getTags();
    }

    @Benchmark
    public boolean benchmarkIsAllowReadOnlyProperties() {
        return options.isAllowReadOnlyProperties();
    }

    @Benchmark
    public TimeZone benchmarkGetTimeZone() {
        return options.getTimeZone();
    }

    @Benchmark
    public int benchmarkGetMaxSimpleKeyLength() {
        return options.getMaxSimpleKeyLength();
    }

    @Benchmark
    public boolean benchmarkIsProcessComments() {
        return options.isProcessComments();
    }

    @Benchmark
    public NonPrintableStyle benchmarkGetNonPrintableStyle() {
        return options.getNonPrintableStyle();
    }

    @Benchmark
    public boolean benchmarkIsDereferenceAliases() {
        return options.isDereferenceAliases();
    }

    @Benchmark
    public AnchorGenerator benchmarkGetAnchorGenerator() {
        return options.getAnchorGenerator();
    }

    @Benchmark
    public Version benchmarkGetVersion() {
        return options.getVersion();
    }

    @Benchmark
    public boolean benchmarkIsAllowUnicode() {
        return options.isAllowUnicode();
    }

    // Setters (void, consume the object to avoid dead-code elimination)

    @Benchmark
    public void benchmarkSetDefaultScalarStyle(Blackhole bh) {
        options.setDefaultScalarStyle(scalarStyle);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetDefaultFlowStyle(Blackhole bh) {
        options.setDefaultFlowStyle(flowStyle);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetIndent(Blackhole bh) {
        options.setIndent(indent);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetIndicatorIndent(Blackhole bh) {
        options.setIndicatorIndent(indicatorIndent);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetIndentWithIndicator(Blackhole bh) {
        options.setIndentWithIndicator(indentWithIndicator);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetCanonical(Blackhole bh) {
        options.setCanonical(canonical);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetPrettyFlow(Blackhole bh) {
        options.setPrettyFlow(prettyFlow);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetWidth(Blackhole bh) {
        options.setWidth(width);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetSplitLines(Blackhole bh) {
        options.setSplitLines(splitLines);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetLineBreak(Blackhole bh) {
        options.setLineBreak(lineBreak);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetExplicitStart(Blackhole bh) {
        options.setExplicitStart(explicitStart);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetExplicitEnd(Blackhole bh) {
        options.setExplicitEnd(explicitEnd);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetTags(Blackhole bh) {
        options.setTags(tags);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetAllowReadOnlyProperties(Blackhole bh) {
        options.setAllowReadOnlyProperties(allowReadOnlyProperties);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetTimeZone(Blackhole bh) {
        options.setTimeZone(timeZone);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetMaxSimpleKeyLength(Blackhole bh) {
        options.setMaxSimpleKeyLength(maxSimpleKeyLength);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetProcessComments(Blackhole bh) {
        options.setProcessComments(processComments);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetNonPrintableStyle(Blackhole bh) {
        options.setNonPrintableStyle(nonPrintableStyle);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetDereferenceAliases(Blackhole bh) {
        options.setDereferenceAliases(dereferenceAliases);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetAnchorGenerator(Blackhole bh) {
        options.setAnchorGenerator(anchorGenerator);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetVersion(Blackhole bh) {
        options.setVersion(version);
        bh.consume(options);
    }

    @Benchmark
    public void benchmarkSetAllowUnicode(Blackhole bh) {
        options.setAllowUnicode(!options.isAllowUnicode());
        bh.consume(options);
    }
}

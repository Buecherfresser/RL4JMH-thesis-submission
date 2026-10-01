package bench.generated.c001;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.inspector.TagInspector;
import org.yaml.snakeyaml.inspector.UnTrustedTagInspector;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class LoaderOptionsBenchmark {

    private LoaderOptions options;
    private boolean boolValue;
    private int intValue;
    private TagInspector tagInspector;

    @Setup(Level.Trial)
    public void setUp() {
        options = new LoaderOptions();
        boolValue = false;
        intValue = 123;
        tagInspector = new UnTrustedTagInspector();
    }

    // Getters

    @Benchmark
    public boolean benchmarkIsAllowDuplicateKeys() {
        return options.isAllowDuplicateKeys();
    }

    @Benchmark
    public boolean benchmarkIsWarnOnDuplicateKeys() {
        return options.isWarnOnDuplicateKeys();
    }

    @Benchmark
    public boolean benchmarkIsWrappedToRootException() {
        return options.isWrappedToRootException();
    }

    @Benchmark
    public int benchmarkGetMaxAliasesForCollections() {
        return options.getMaxAliasesForCollections();
    }

    @Benchmark
    public boolean benchmarkGetAllowRecursiveKeys() {
        return options.getAllowRecursiveKeys();
    }

    @Benchmark
    public boolean benchmarkIsProcessComments() {
        return options.isProcessComments();
    }

    @Benchmark
    public boolean benchmarkIsEnumCaseSensitive() {
        return options.isEnumCaseSensitive();
    }

    @Benchmark
    public int benchmarkGetNestingDepthLimit() {
        return options.getNestingDepthLimit();
    }

    @Benchmark
    public int benchmarkGetCodePointLimit() {
        return options.getCodePointLimit();
    }

    @Benchmark
    public boolean benchmarkIsMergeOnCompose() {
        return options.isMergeOnCompose();
    }

    @Benchmark
    public TagInspector benchmarkGetTagInspector() {
        return options.getTagInspector();
    }

    // Setters (void)

    @Benchmark
    public void benchmarkSetAllowDuplicateKeys(Blackhole bh) {
        options.setAllowDuplicateKeys(boolValue);
        bh.consume(options.isAllowDuplicateKeys());
    }

    @Benchmark
    public void benchmarkSetWarnOnDuplicateKeys(Blackhole bh) {
        options.setWarnOnDuplicateKeys(boolValue);
        bh.consume(options.isWarnOnDuplicateKeys());
    }

    @Benchmark
    public void benchmarkSetWrappedToRootException(Blackhole bh) {
        options.setWrappedToRootException(boolValue);
        bh.consume(options.isWrappedToRootException());
    }

    @Benchmark
    public void benchmarkSetMaxAliasesForCollections(Blackhole bh) {
        options.setMaxAliasesForCollections(intValue);
        bh.consume(options.getMaxAliasesForCollections());
    }

    @Benchmark
    public void benchmarkSetAllowRecursiveKeys(Blackhole bh) {
        options.setAllowRecursiveKeys(boolValue);
        bh.consume(options.getAllowRecursiveKeys());
    }

    @Benchmark
    public void benchmarkSetEnumCaseSensitive(Blackhole bh) {
        options.setEnumCaseSensitive(boolValue);
        bh.consume(options.isEnumCaseSensitive());
    }

    @Benchmark
    public void benchmarkSetNestingDepthLimit(Blackhole bh) {
        options.setNestingDepthLimit(intValue);
        bh.consume(options.getNestingDepthLimit());
    }

    @Benchmark
    public void benchmarkSetCodePointLimit(Blackhole bh) {
        options.setCodePointLimit(intValue);
        bh.consume(options.getCodePointLimit());
    }

    @Benchmark
    public void benchmarkSetMergeOnCompose(Blackhole bh) {
        options.setMergeOnCompose(boolValue);
        bh.consume(options.isMergeOnCompose());
    }

    @Benchmark
    public void benchmarkSetTagInspector(Blackhole bh) {
        options.setTagInspector(tagInspector);
        bh.consume(options.getTagInspector());
    }

    // Setter with fluent return

    @Benchmark
    public LoaderOptions benchmarkSetProcessComments() {
        return options.setProcessComments(boolValue);
    }
}

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
    private TagInspector unTrustedTagInspector;

    @Setup
    public void setup() {
        options = new LoaderOptions();
        unTrustedTagInspector = new UnTrustedTagInspector();
    }

    // Benchmark 1: Setting allowDuplicateKeys
    @Benchmark
    public void setAllowDuplicateKeys(Blackhole bh) {
        options.setAllowDuplicateKeys(true);
        bh.consume(options.isAllowDuplicateKeys());
    }

    // Benchmark 2: Setting warnOnDuplicateKeys
    @Benchmark
    public void setWarnOnDuplicateKeys(Blackhole bh) {
        options.setWarnOnDuplicateKeys(false);
        bh.consume(options.isWarnOnDuplicateKeys());
    }

    // Benchmark 3: Setting wrappedToRootException
    @Benchmark
    public void setWrappedToRootException(Blackhole bh) {
        options.setWrappedToRootException(true);
        bh.consume(options.isWrappedToRootException());
    }

    // Benchmark 4: Setting maxAliasesForCollections
    @Benchmark
    public void setMaxAliasesForCollections(Blackhole bh) {
        int maxAliases = 100;
        options.setMaxAliasesForCollections(maxAliases);
        bh.consume(options.getMaxAliasesForCollections());
    }

    // Benchmark 5: Setting allowRecursiveKeys
    @Benchmark
    public void setAllowRecursiveKeys(Blackhole bh) {
        options.setAllowRecursiveKeys(true);
        bh.consume(options.getAllowRecursiveKeys());
    }

    // Benchmark 6: Setting processComments
    @Benchmark
    public void setProcessComments(Blackhole bh) {
        options.setProcessComments(true);
        bh.consume(options.isProcessComments());
    }

    // Benchmark 7: Setting enumCaseSensitive
    @Benchmark
    public void setEnumCaseSensitive(Blackhole bh) {
        options.setEnumCaseSensitive(false);
        bh.consume(options.isEnumCaseSensitive());
    }

    // Benchmark 8: Setting nestingDepthLimit
    @Benchmark
    public void setNestingDepthLimit(Blackhole bh) {
        int depthLimit = 100;
        options.setNestingDepthLimit(depthLimit);
        bh.consume(options.getNestingDepthLimit());
    }

    // Benchmark 9: Setting codePointLimit
    @Benchmark
    public void setCodePointLimit(Blackhole bh) {
        int limit = 5 * 1024 * 1024;
        options.setCodePointLimit(limit);
        bh.consume(options.getCodePointLimit());
    }

    // Benchmark 10: Setting mergeOnCompose
    @Benchmark
    public void setMergeOnCompose(Blackhole bh) {
        options.setMergeOnCompose(true);
        bh.consume(options.isMergeOnCompose());
    }

    // Benchmark 11: Setting TagInspector
    @Benchmark
    public void setTagInspector(Blackhole bh) {
        options.setTagInspector(unTrustedTagInspector);
        bh.consume(options.getTagInspector());
    }
}

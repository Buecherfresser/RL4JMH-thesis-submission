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
    private boolean testBoolTrue;
    private boolean testBoolFalse;
    private int testIntPositive;
    private int testIntNegative;
    private TagInspector testTagInspector;

    @Setup(Level.Invocation)
    public void setup() {
        options = new LoaderOptions();
        testBoolTrue = true;
        testBoolFalse = false;
        testIntPositive = 1000;
        testIntNegative = -500;
        testTagInspector = new UnTrustedTagInspector();
    }

    // --- Boolean Setter/Getter Tests ---

    @Benchmark
    public void testSetAndGetAllowDuplicateKeys(Blackhole bh) {
        options.setAllowDuplicateKeys(testBoolTrue);
        bh.consume(options.isAllowDuplicateKeys());
    }

    @Benchmark
    public void testSetAndGetAllowDuplicateKeysFalse(Blackhole bh) {
        options.setAllowDuplicateKeys(testBoolFalse);
        bh.consume(options.isAllowDuplicateKeys());
    }

    @Benchmark
    public void testSetAndGetWarnOnDuplicateKeys(Blackhole bh) {
        options.setWarnOnDuplicateKeys(testBoolTrue);
        bh.consume(options.isWarnOnDuplicateKeys());
    }

    @Benchmark
    public void testSetAndGetWarnOnDuplicateKeysFalse(Blackhole bh) {
        options.setWarnOnDuplicateKeys(testBoolFalse);
        bh.consume(options.isWarnOnDuplicateKeys());
    }

    @Benchmark
    public void testSetAndGetWrappedToRootException(Blackhole bh) {
        options.setWrappedToRootException(testBoolTrue);
        bh.consume(options.isWrappedToRootException());
    }

    @Benchmark
    public void testSetAndGetWrappedToRootExceptionFalse(Blackhole bh) {
        options.setWrappedToRootException(testBoolFalse);
        bh.consume(options.isWrappedToRootException());
    }

    @Benchmark
    public void testSetAndGetAllowRecursiveKeys(Blackhole bh) {
        options.setAllowRecursiveKeys(testBoolTrue);
        bh.consume(options.getAllowRecursiveKeys());
    }

    @Benchmark
    public void testSetAndGetAllowRecursiveKeysFalse(Blackhole bh) {
        options.setAllowRecursiveKeys(testBoolFalse);
        bh.consume(options.getAllowRecursiveKeys());
    }

    @Benchmark
    public void testSetAndGetProcessComments(Blackhole bh) {
        options.setProcessComments(testBoolTrue);
        bh.consume(options.isProcessComments());
    }

    @Benchmark
    public void testSetAndGetProcessCommentsFalse(Blackhole bh) {
        options.setProcessComments(testBoolFalse);
        bh.consume(options.isProcessComments());
    }

    @Benchmark
    public void testSetAndGetEnumCaseSensitive(Blackhole bh) {
        options.setEnumCaseSensitive(testBoolTrue);
        bh.consume(options.isEnumCaseSensitive());
    }

    @Benchmark
    public void testSetAndGetEnumCaseSensitiveFalse(Blackhole bh) {
        options.setEnumCaseSensitive(testBoolFalse);
        bh.consume(options.isEnumCaseSensitive());
    }

    @Benchmark
    public void testSetAndGetMergeOnCompose(Blackhole bh) {
        options.setMergeOnCompose(testBoolTrue);
        bh.consume(options.isMergeOnCompose());
    }

    @Benchmark
    public void testSetAndGetMergeOnComposeFalse(Blackhole bh) {
        options.setMergeOnCompose(testBoolFalse);
        bh.consume(options.isMergeOnCompose());
    }

    // --- Integer Setter/Getter Tests ---

    @Benchmark
    public void testSetAndGetMaxAliasesForCollections(Blackhole bh) {
        options.setMaxAliasesForCollections(testIntPositive);
        bh.consume(options.getMaxAliasesForCollections());
    }

    @Benchmark
    public void testSetAndGetMaxAliasesForCollectionsNegative(Blackhole bh) {
        options.setMaxAliasesForCollections(testIntNegative);
        bh.consume(options.getMaxAliasesForCollections());
    }

    @Benchmark
    public void testSetAndGetNestingDepthLimit(Blackhole bh) {
        options.setNestingDepthLimit(testIntPositive);
        bh.consume(options.getNestingDepthLimit());
    }

    @Benchmark
    public void testSetAndGetNestingDepthLimitNegative(Blackhole bh) {
        options.setNestingDepthLimit(testIntNegative);
        bh.consume(options.getNestingDepthLimit());
    }

    @Benchmark
    public void testSetAndGetCodePointLimit(Blackhole bh) {
        options.setCodePointLimit(testIntPositive);
        bh.consume(options.getCodePointLimit());
    }

    @Benchmark
    public void testSetAndGetCodePointLimitNegative(Blackhole bh) {
        options.setCodePointLimit(testIntNegative);
        bh.consume(options.getCodePointLimit());
    }

    // --- Object Setter/Getter Tests ---

    @Benchmark
    public void testSetAndGetTagInspector(Blackhole bh) {
        options.setTagInspector(testTagInspector);
        bh.consume(options.getTagInspector());
    }

    @Benchmark
    public void testSetAndGetTagInspectorNull(Blackhole bh) {
        options.setTagInspector(null);
        bh.consume(options.getTagInspector());
    }
}

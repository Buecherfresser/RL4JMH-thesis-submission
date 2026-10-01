package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.StringTemplateMatcher;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringTemplateMatcherBenchmark {

    // Patterns and inputs
    private String patternWithMacros;
    private String matchingInput;
    private String nonMatchingInput;

    private String wildcardPattern;
    private String wildcardInput;

    // Matcher instances
    private StringTemplateMatcher regexMatcher;
    private StringTemplateMatcher wildcardMatcher;

    // Matcher used for setter benchmarks
    private StringTemplateMatcher prefixSetterMatcher;
    private StringTemplateMatcher suffixSetterMatcher;
    private StringTemplateMatcher splitSetterMatcher;

    // Values for setters
    private String newPrefix;
    private String newSuffix;
    private String newSplit;

    @Setup(Level.Trial)
    public void setUp() {
        // Regular expression based pattern
        patternWithMacros = "Hello {name}, you have {count:\\d+} messages.";
        matchingInput = "Hello John, you have 5 messages.";
        nonMatchingInput = "Hello John, you have five messages.";

        // Wildcard based pattern
        wildcardPattern = "file_{name}.txt";
        wildcardInput = "file_report.txt";

        // Create matchers
        regexMatcher = StringTemplateMatcher.of(patternWithMacros);
        wildcardMatcher = StringTemplateMatcher.of(wildcardPattern).useWildcardMatch();

        // Matchers for setter benchmarks
        prefixSetterMatcher = StringTemplateMatcher.of(patternWithMacros);
        suffixSetterMatcher = StringTemplateMatcher.of(patternWithMacros);
        splitSetterMatcher = StringTemplateMatcher.of(patternWithMacros);

        // New macro delimiters
        newPrefix = "[";
        newSuffix = "]";
        newSplit = "|";
    }

    // --------------------------------------------------------------------
    // Benchmark: hasMacros()
    @Benchmark
    public boolean benchmarkHasMacros() {
        return regexMatcher.hasMacros();
    }

    // Benchmark: compile()
    @Benchmark
    public int benchmarkCompile() {
        return regexMatcher.compile().macrosCount();
    }

    // Benchmark: matches() with a matching input
    @Benchmark
    public boolean benchmarkMatchesTrue() {
        return regexMatcher.matches(matchingInput);
    }

    // Benchmark: matches() with a non‑matching input
    @Benchmark
    public boolean benchmarkMatchesFalse() {
        return regexMatcher.matches(nonMatchingInput);
    }

    // Benchmark: match() extracting values
    @Benchmark
    public int benchmarkMatchExtract() {
        return regexMatcher.match(matchingInput).length;
    }

    // Benchmark: useWildcardMatch() + match()
    @Benchmark
    public int benchmarkWildcardMatchExtract() {
        return wildcardMatcher.match(wildcardInput).length;
    }

    // Benchmark: useRegexMatch() (explicit call) then matches()
    @Benchmark
    public boolean benchmarkUseRegexMatchThenMatches() {
        regexMatcher.useRegexMatch();
        return regexMatcher.matches(matchingInput);
    }

    // Benchmark: setMacroPrefix()
    @Benchmark
    public StringTemplateMatcher benchmarkSetMacroPrefix() {
        return prefixSetterMatcher.setMacroPrefix(newPrefix);
    }

    // Benchmark: setMacroSuffix()
    @Benchmark
    public StringTemplateMatcher benchmarkSetMacroSuffix() {
        return suffixSetterMatcher.setMacroSuffix(newSuffix);
    }

    // Benchmark: setMacroSplit()
    @Benchmark
    public StringTemplateMatcher benchmarkSetMacroSplit() {
        return splitSetterMatcher.setMacroSplit(newSplit);
    }

    // Benchmark: compiled.matches() directly
    @Benchmark
    public boolean benchmarkCompiledMatches() {
        return regexMatcher.compile().matches(matchingInput);
    }

    // Benchmark: compiled.match() directly
    @Benchmark
    public int benchmarkCompiledMatch() {
        return regexMatcher.compile().match(matchingInput).length;
    }

    // Benchmark: compiled.names() accessor
    @Benchmark
    public int benchmarkCompiledNamesLength() {
        return regexMatcher.compile().names().length;
    }

    // Benchmark: compiled.patterns() accessor
    @Benchmark
    public int benchmarkCompiledPatternsLength() {
        return regexMatcher.compile().patterns().length;
    }

    // Benchmark: consume result via Blackhole (void benchmark example)
    @Benchmark
    public void benchmarkMatchConsume(Blackhole bh) {
        bh.consume(regexMatcher.match(matchingInput));
    }
}

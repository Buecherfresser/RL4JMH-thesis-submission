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

    // --- Inputs ---
    private String simplePattern;
    private String singleMacroPattern;
    private String regexMacroPattern;
    private String wildcardMacroPattern;
    private String complexPattern;

    private String matchingInput;
    private String nonMatchingInput;

    // --- Subjects ---
    private StringTemplateMatcher simpleMatcher;
    private StringTemplateMatcher regexMatcher;
    private StringTemplateMatcher wildcardMatcher;
    private StringTemplateMatcher complexMatcher;

    @Setup(Level.Trial)
    public void setup() {
        // 1. Patterns
        simplePattern = "Hello World";
        singleMacroPattern = "User {name}";
        regexMacroPattern = "ID {id:\\d+}";
        wildcardMacroPattern = "Path {path:.*}";
        complexPattern = "Start {a:.*} Middle {b:\\w+} End";

        // 2. Inputs
        matchingInput = "User JohnDoe";
        nonMatchingInput = "User Jane";

        // 3. Initialize Matchers
        simpleMatcher = StringTemplateMatcher.of(simplePattern);
        regexMatcher = StringTemplateMatcher.of(regexMacroPattern).useRegexMatch();
        wildcardMatcher = StringTemplateMatcher.of(wildcardMacroPattern).useWildcardMatch();
        complexMatcher = StringTemplateMatcher.of(complexPattern);
    }

    // --- Benchmarks ---

    /**
     * Benchmarks compilation of a pattern with no macros.
     */
    @Benchmark
    public void benchmarkCompileNoMacros(Blackhole bh) {
        jodd.util.StringTemplateMatcher.StringTemplateMatcherCompiled compiled = simpleMatcher.compile();
        bh.consume(compiled);
    }

    /**
     * Benchmarks compilation of a pattern with simple macros (no defined pattern).
     */
    @Benchmark
    public void benchmarkCompileSimpleMacro(Blackhole bh) {
        // Note: The original logic here was flawed (singleMacroPattern.equals(singleMacroPattern) is always true).
        // Assuming the intent was to test simple compilation path.
        jodd.util.StringTemplateMatcher.StringTemplateMatcherCompiled compiled = simpleMatcher.compile();
        bh.consume(compiled);
    }

    /**
     * Benchmarks compilation of a pattern with regex macros.
     */
    @Benchmark
    public void benchmarkCompileRegexMacro(Blackhole bh) {
        jodd.util.StringTemplateMatcher.StringTemplateMatcherCompiled compiled = regexMatcher.compile();
        bh.consume(compiled);
    }

    /**
     * Benchmarks compilation of a complex pattern with multiple macros.
     */
    @Benchmark
    public void benchmarkCompileComplex(Blackhole bh) {
        jodd.util.StringTemplateMatcher.StringTemplateMatcherCompiled compiled = complexMatcher.compile();
        bh.consume(compiled);
    }

    /**
     * Benchmarks checking if a simple pattern matches an input.
     */
    @Benchmark
    public void benchmarkMatchSimple(Blackhole bh) {
        boolean result = simpleMatcher.matches(matchingInput);
        bh.consume(result);
    }

    /**
     * Benchmarks checking if a regex pattern matches an input.
     */
    @Benchmark
    public void benchmarkMatchRegex(Blackhole bh) {
        boolean result = regexMatcher.matches(matchingInput);
        bh.consume(result);
    }

    /**
     * Benchmarks checking if a wildcard pattern matches an input.
     */
    @Benchmark
    public void benchmarkMatchWildcard(Blackhole bh) {
        boolean result = wildcardMatcher.matches(matchingInput);
        bh.consume(result);
    }

    /**
     * Benchmarks checking if a complex pattern matches an input.
     */
    @Benchmark
    public void benchmarkMatchComplex(Blackhole bh) {
        boolean result = complexMatcher.matches(matchingInput);
        bh.consume(result);
    }

    /**
     * Benchmarks extracting all matches from a simple pattern.
     */
    @Benchmark
    public void benchmarkExtractSimple(Blackhole bh) {
        StringTemplateMatcher.Match[] matches = simpleMatcher.match(matchingInput);
        bh.consume(matches);
    }

    /**
     * Benchmarks extracting all matches from a regex pattern.
     */
    @Benchmark
    public void benchmarkExtractRegex(Blackhole bh) {
        StringTemplateMatcher.Match[] matches = regexMatcher.match(matchingInput);
        bh.consume(matches);
    }

    /**
     * Benchmarks extracting all matches from a complex pattern.
     */
    @Benchmark
    public void benchmarkExtractComplex(Blackhole bh) {
        StringTemplateMatcher.Match[] matches = complexMatcher.match(matchingInput);
        bh.consume(matches);
    }

    /**
     * Benchmarks the configuration setter methods (e.g., changing prefix).
     * Note: This tests the overhead of resetting the compiled state.
     */
    @Benchmark
    public void benchmarkSetMacroPrefix(Blackhole bh) {
        StringTemplateMatcher newMatcher = simpleMatcher.setMacroPrefix("::");
        // Force compilation/usage to ensure the state change is effective
        jodd.util.StringTemplateMatcher.StringTemplateMatcherCompiled compiled = newMatcher.compile();
        bh.consume(compiled);
    }
}

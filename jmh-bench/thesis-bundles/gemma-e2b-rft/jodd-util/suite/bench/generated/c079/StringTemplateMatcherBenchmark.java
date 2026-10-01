package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.Arrays;

import jodd.util.StringTemplateMatcher;
import jodd.util.StringTemplateMatcher.Match;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class StringTemplateMatcherBenchmark {

    // --- Setup State ---
    private StringTemplateMatcher matcher;
    private final String COMPLEX_PATTERN = "{NAME:REGEX_PATTERN} {FIXED_TEXT} {OTHER_MACRO:SPLIT}";
    private final String REGEX_PATTERN = "(\\w+)\\s+(\\d+)"; // Macro 0: Word, Macro 1: Digit
    private final String WILDCARD_PATTERN = ".*\\d+.*"; // Macro 0: Any chars, Macro 1: Digit

    @Setup
    public void setup() {
        // Initialize the matcher with a complex regex pattern
        matcher = StringTemplateMatcher.of(COMPLEX_PATTERN);
        // Use regex matching mode for the main tests
        matcher.useRegexMatch();
    }

    // --- Benchmark Methods ---

    /**
     * Benchmark 1: Regex matching with a successful match.
     * Tests the core functionality of the compiled regex matcher.
     */
    @Benchmark
    public void testRegexMatchSuccess(Blackhole bh) {
        String input = "WORD 123";
        Match[] matches = matcher.match(input);
        bh.consume(matches);
    }

    /**
     * Benchmark 2: Regex matching with a failed match.
     * Tests the failure path of the compiled regex matcher.
     */
    @Benchmark
    public void testRegexMatchFailure(Blackhole bh) {
        String input = "WORD ABC";
        Match[] matches = matcher.match(input);
        bh.consume(matches);
    }

    /**
     * Benchmark 3: Wildcard matching with a successful match.
     * Tests the wildcard matching mode.
     */
    @Benchmark
    public void testWildcardMatchSuccess(Blackhole bh) {
        String input = "Some random text 456";
        // Temporarily switch mode for this test
        matcher.useWildcardMatch();
        Match[] matches = matcher.match(input);
        bh.consume(matches);
    }

    /**
     * Benchmark 4: Wildcard matching with a failed match.
     * Tests the failure path of the wildcard matcher.
     */
    @Benchmark
    public void testWildcardMatchFailure(Blackhole bh) {
        String input = "Only text";
        // Temporarily switch mode for this test
        matcher.useWildcardMatch();
        Match[] matches = matcher.match(input);
        bh.consume(matches);
    }

    /**
     * Benchmark 5: Testing compilation overhead (compiling a new pattern).
     * This tests the cost of the compile() method itself, which is crucial for setup.
     */
    @Benchmark
    public void testCompilationOverhead(Blackhole bh) {
        // Create a new matcher instance with a different pattern to force recompilation
        StringTemplateMatcher newMatcher = StringTemplateMatcher.of("A{MACRO:SPLIT}B");
        StringTemplateMatcher.StringTemplateMatcherCompiled compiled = newMatcher.compile();
        bh.consume(compiled);
    }

    /**
     * Benchmark 6: Testing configuration change (setting a new prefix).
     * Tests the setter methods and the subsequent state reset (compiled = null).
     */
    @Benchmark
    public void testSetMacroPrefix(Blackhole bh) {
        String newPrefix = "NEW_";
        StringTemplateMatcher newMatcher = matcher.setMacroPrefix(newPrefix);
        bh.consume(newMatcher);
    }

    /**
     * Benchmark 7: Testing configuration change (switching to wildcard mode).
     * Tests the setter method and the subsequent state change (matchValue update).
     */
    @Benchmark
    public void testUseWildcardMatchMode(Blackhole bh) {
        matcher.useWildcardMatch();
        bh.consume(matcher);
    }
}

package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import jodd.util.StringTemplateMatcher;
import jodd.util.StringUtil;
import jodd.util.StringPool;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class StringTemplateMatcherBenchmark {

    private StringTemplateMatcher matcher;
    private StringTemplateMatcher.StringTemplateMatcherCompiled compiled;

    // --- Setup Fixtures ---
    private String testPattern;
    private String successInput;
    private String failureInput;

    @Setup
    public void setup() {
        // 1. Define a complex pattern with macros
        this.testPattern = "{name}:{value}{end}";

        // 2. Create the matcher instance
        this.matcher = StringTemplateMatcher.of(testPattern);

        // 3. Compile the pattern once (expensive operation)
        this.compiled = matcher.compile();

        // 4. Define test inputs
        // Success case: name=TestName, value=123
        this.successInput = "{TestName}:123{end}";
        // Failure case: wrong name
        this.failureInput = "{WrongName}:456{end}";
    }

    // --- Benchmarks ---

    @Benchmark
    public void regexMatchSuccess(Blackhole bh) {
        StringTemplateMatcher.Match[] matches = matcher.match(successInput);
        bh.consume(matches);
    }

    @Benchmark
    public void regexMatchFailure(Blackhole bh) {
        StringTemplateMatcher.Match[] matches = matcher.match(failureInput);
        bh.consume(matches);
    }

    @Benchmark
    public void wildcardMatchSuccess(Blackhole bh) {
        // Switch to wildcard mode
        StringTemplateMatcher wildcardMatcher = matcher.useWildcardMatch();
        StringTemplateMatcher.Match[] matches = wildcardMatcher.match(successInput);
        bh.consume(matches);
    }

    @Benchmark
    public void wildcardMatchFailure(Blackhole bh) {
        // Switch to wildcard mode
        StringTemplateMatcher wildcardMatcher = matcher.useWildcardMatch();
        StringTemplateMatcher.Match[] matches = wildcardMatcher.match(failureInput);
        bh.consume(matches);
    }

    @Benchmark
    public void compileTimeCheck(Blackhole bh) {
        // This benchmark ensures the compilation step is measurable, though it should be fast
        StringTemplateMatcher freshMatcher = StringTemplateMatcher.of("a:{b}");
        StringTemplateMatcher.StringTemplateMatcherCompiled freshCompiled = freshMatcher.compile();
        bh.consume(freshCompiled);
    }

    @Benchmark
    public void setMacroPrefixChange(Blackhole bh) {
        // Test configuration change overhead
        StringTemplateMatcher newMatcher = matcher.setMacroPrefix("NEW_");
        bh.consume(newMatcher);
    }

    @Benchmark
    public void setMacroSplitChange(Blackhole bh) {
        // Test configuration change overhead
        StringTemplateMatcher newMatcher = matcher.setMacroSplit("|");
        bh.consume(newMatcher);
    }
}

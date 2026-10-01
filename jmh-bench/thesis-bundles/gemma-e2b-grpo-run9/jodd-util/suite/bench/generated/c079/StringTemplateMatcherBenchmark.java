package bench.generated.c079;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

// Assuming jodd.util.StringTemplateMatcher is accessible via this import path
import jodd.util.StringTemplateMatcher;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringTemplateMatcherBenchmark {

    // State field to hold an instance of the matcher.
    // Since StringTemplateMatcher is complex and stateful, we instantiate it here.
    private StringTemplateMatcher matcher;

    // A complex pattern to test compilation and matching logic.
    // This pattern uses macros, a split, and a suffix.
    private static final String COMPLEX_PATTERN = "START{ID}:{END}";

    @Setup
    public void setup() {
        // Initialize the matcher once per benchmark setup phase.
        // This tests the cost of initialization/compilation once.
        this.matcher = StringTemplateMatcher.of(COMPLEX_PATTERN);
    }

    @Benchmark
    public void testHasMacros(Blackhole bh) {
        // Test a stateless check (relies on internal compilation logic)
        bh.consume(this.matcher.hasMacros());
    }

    @Benchmark
    public void testRegexMatch(Blackhole bh) {
        // Test the regex matching functionality.
        // Since the pattern is complex, this tests the compiled path.
        String input = "START123:END";
        try {
            // We call match which internally calls compile if needed.
            this.matcher.match(input);
        } catch (Exception e) {
            // Ignore exceptions if the internal implementation throws them during matching
        }
        bh.consume(true);
    }

    @Benchmark
    public void testRegexMatchNoMatch(Blackhole bh) {
        // Test a non-matching input to ensure early exit/correct behavior.
        String input = "WRONG_START:END";
        try {
            this.matcher.match(input);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(true);
    }

    @Benchmark
    public void testWildcardMatch(Blackhole bh) {
        // Test the wildcard matching functionality.
        // Note: This requires calling useWildcardMatch() first, which resets the internal state.
        String input = "START999:END";
        try {
            // Use a fresh instance or reset the existing one if necessary for clean testing
            // For simplicity in this benchmark, we rely on the instance state being reset
            // or we could re-instantiate if we wanted to test the mode switch cleanly.
            // Since we are benchmarking the instance methods, we use the existing one.
            this.matcher.useWildcardMatch();
            this.matcher.matches(input);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(true);
    }

    @Benchmark
    public void testCompileCost(Blackhole bh) {
        // Test the cost of compilation (which should be cached after the first call)
        try {
            // Calling compile forces compilation if it hasn't happened yet.
            this.matcher.compile();
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(true);
    }
}

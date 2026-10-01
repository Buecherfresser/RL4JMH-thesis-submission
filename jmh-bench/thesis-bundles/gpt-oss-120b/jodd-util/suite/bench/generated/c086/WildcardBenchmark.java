package bench.generated.c086;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import jodd.util.Wildcard;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class WildcardBenchmark {

    private String simpleString;
    private String simplePattern;
    private String[] matchOnePatterns;
    private String path;
    private String pathPattern;
    private String[] pathPatterns;

    @Setup
    public void setup() {
        // Simple string and pattern for basic matching
        simpleString = "abcdefg";
        simplePattern = "a*e?g";

        // Patterns for matchOne (some will match, some not)
        matchOnePatterns = new String[] {
                "xyz*",
                "a*e?g",
                "a?c*",
                "*def*",
                "no_match"
        };

        // Path and patterns for path matching
        path = "src/main/java/com/example/Test.java";
        pathPattern = "src/**/Test.java";

        pathPatterns = new String[] {
                "src/**/Test.java",
                "src/*/java/**",
                "**/Test.java",
                "src/main/java/*.java",
                "*/main/*/com/**"
        };
    }

    @Benchmark
    public boolean benchmarkMatch() {
        return Wildcard.match(simpleString, simplePattern);
    }

    @Benchmark
    public boolean benchmarkEqualsOrMatchExact() {
        return Wildcard.equalsOrMatch(simpleString, simpleString);
    }

    @Benchmark
    public boolean benchmarkEqualsOrMatchWildcard() {
        return Wildcard.equalsOrMatch(simpleString, simplePattern);
    }

    @Benchmark
    public int benchmarkMatchOne() {
        return Wildcard.matchOne(simpleString, matchOnePatterns);
    }

    @Benchmark
    public boolean benchmarkMatchPath() {
        return Wildcard.matchPath(path, pathPattern);
    }

    @Benchmark
    public int benchmarkMatchPathOne() {
        return Wildcard.matchPathOne(path, pathPatterns);
    }
}

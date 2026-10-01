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

    // --- Input Data Setup ---

    // Simple string matching cases
    private String simpleString = "file_name_01";
    private String simplePatternExact = "file_name_01";
    private String simplePatternWildcard = "file_name_?1";
    private String simplePatternStar = "file_*_01";
    private String simplePatternEscaped = "file\\_name_01";
    private String simplePatternComplex = "file*name*01";

    // Path matching cases
    private String simplePath = "src/main/java/com/app/File.java";
    private String simplePathPatternExact = "src/main/java/com/app/File.java";
    private String simplePathPatternWildcard = "src/main/java/com/app/File?.java";
    private String simplePathPatternStar = "src/main/java/*/*.java";
    private String simplePathPatternDeep = "src/**/File.java";
    private String simplePathPatternComplex = "src/*/main/*/*.java";

    // Multiple pattern matching cases
    private String multiplePatternSource = "target_file.txt";
    private String[] multiplePatterns = {"*.txt", "target_file.txt", "other.log"};

    private String multiplePathSource = "src/data/config.xml";
    private String[] multiplePathPatterns = {"src/**/config.xml", "src/data/*.xml"};


    @Setup(Level.Trial)
    public void setup() {
        // Inputs are pre-built and reused for all benchmarks
    }

    // --- Benchmarks for match(CharSequence, CharSequence) ---

    @Benchmark
    public boolean benchMatchSimpleExact(Blackhole bh) {
        return Wildcard.match(simpleString, simplePatternExact);
    }

    @Benchmark
    public boolean benchMatchSimpleWildcard(Blackhole bh) {
        return Wildcard.match(simpleString, simplePatternWildcard);
    }

    @Benchmark
    public boolean benchMatchSimpleStar(Blackhole bh) {
        return Wildcard.match(simpleString, simplePatternStar);
    }

    @Benchmark
    public boolean benchMatchSimpleEscaped(Blackhole bh) {
        return Wildcard.match(simpleString, simplePatternEscaped);
    }

    @Benchmark
    public boolean benchMatchSimpleComplex(Blackhole bh) {
        return Wildcard.match(simpleString, simplePatternComplex);
    }

    // --- Benchmarks for equalsOrMatch(CharSequence, CharSequence) ---

    @Benchmark
    public boolean benchEqualsOrMatchExact(Blackhole bh) {
        return Wildcard.equalsOrMatch(simpleString, simplePatternExact);
    }

    @Benchmark
    public boolean benchEqualsOrMatchWildcard(Blackhole bh) {
        return Wildcard.equalsOrMatch(simpleString, simplePatternWildcard);
    }

    // --- Benchmarks for matchOne(String src, String... patterns) ---

    @Benchmark
    public int benchMatchOneSuccess(Blackhole bh) {
        // Test case where the first pattern matches
        return Wildcard.matchOne(multiplePatternSource, "*.txt", "other.log");
    }

    @Benchmark
    public int benchMatchOneFailure(Blackhole bh) {
        // Test case where no pattern matches
        return Wildcard.matchOne(multiplePatternSource, "nonexistent.log", "other.log");
    }

    // --- Benchmarks for matchPath(String path, String pattern) ---

    @Benchmark
    public boolean benchMatchPathSimpleExact(Blackhole bh) {
        return Wildcard.matchPath(simplePath, simplePathPatternExact);
    }

    @Benchmark
    public boolean benchMatchPathSimpleWildcard(Blackhole bh) {
        return Wildcard.matchPath(simplePath, simplePathPatternWildcard);
    }

    @Benchmark
    public boolean benchMatchPathSimpleStar(Blackhole bh) {
        return Wildcard.matchPath(simplePath, simplePathPatternStar);
    }

    @Benchmark
    public boolean benchMatchPathDeep(Blackhole bh) {
        return Wildcard.matchPath(simplePath, simplePathPatternDeep);
    }

    @Benchmark
    public boolean benchMatchPathComplex(Blackhole bh) {
        return Wildcard.matchPath(simplePath, simplePathPatternComplex);
    }

    // --- Benchmarks for matchPathOne(String path, String... patterns) ---

    @Benchmark
    public int benchMatchPathOneSuccess(Blackhole bh) {
        // Test case where the first pattern matches
        return Wildcard.matchPathOne(multiplePathSource, "src/**/config.xml", "src/data/*.xml");
    }

    @Benchmark
    public int benchMatchPathOneFailure(Blackhole bh) {
        // Test case where no pattern matches
        return Wildcard.matchPathOne(multiplePathSource, "nonexistent/**/file.xml", "src/data/*.xml");
    }
}

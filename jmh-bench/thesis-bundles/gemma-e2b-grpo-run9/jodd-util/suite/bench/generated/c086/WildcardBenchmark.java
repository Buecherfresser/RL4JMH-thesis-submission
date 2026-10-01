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

    // Since Wildcard methods are static and operate on immutable String inputs,
    // no instance state is required.

    @Benchmark
    public void benchmarkMatchSimple(Blackhole bh) {
        // Test case 1: Simple exact match
        boolean result = Wildcard.match("hello", "hello");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchSimpleMismatch(Blackhole bh) {
        // Test case 2: Simple mismatch
        boolean result = Wildcard.match("hello", "world");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEqualsOrMatch(Blackhole bh) {
        // Test case 3: equalsOrMatch (exact match)
        boolean result = Wildcard.equalsOrMatch("test", "test");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEqualsOrMatchMismatch(Blackhole bh) {
        // Test case 4: equalsOrMatch (mismatch)
        boolean result = Wildcard.equalsOrMatch("test", "other");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchPathSimple(Blackhole bh) {
        // Test case 5: Simple path match (no wildcards)
        boolean result = Wildcard.matchPath("/a/b/c", "/a/b/c");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchPathMismatch(Blackhole bh) {
        // Test case 6: Path mismatch
        boolean result = Wildcard.matchPath("/a/b/c", "/a/d/c");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchPathWithWildcard(Blackhole bh) {
        // Test case 7: Path match with '*' wildcard
        boolean result = Wildcard.matchPath("/a/b/c", "/a/*/c");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchPathDeepWildcard(Blackhole bh) {
        // Test case 8: Path match with '**' deep wildcard
        boolean result = Wildcard.matchPath("/a/b/c/d", "/a/**/d");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchOne(Blackhole bh) {
        // Test case 9: matchOne with multiple patterns (success case)
        int result = Wildcard.matchOne("data", "data", "other", "final");
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkMatchOneFailure(Blackhole bh) {
        // Test case 10: matchOne with multiple patterns (failure case)
        int result = Wildcard.matchOne("data", "other", "final", "other_final");
        bh.consume(result);
    }
}

package bench;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class PathMatcherBenchmark {

    @Param({"64"})
    public int calls;

    private PathMatcher matcher;
    private String[] patterns;
    private String[] paths;

    @Setup
    public void setup() {
        matcher = new PathMatcher();
        patterns = new String[]{"/a/b/c", "/x/*/z", "/foo/bar/baz", "/p/q/r/s",
                                "/svc/api/v1", "/svc/*/v2", "/static/css/main", "/static/*/main"};
        paths = new String[calls];
        for (int i = 0; i < calls; i++) paths[i] = patterns[i % patterns.length].replace("*", "match");
    }

    @Benchmark
    public void matches(Blackhole bh) {
        for (int i = 0; i < calls; i++) {
            bh.consume(matcher.matches(patterns[i % patterns.length], paths[i]));
        }
    }
}

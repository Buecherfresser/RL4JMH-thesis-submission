package bench.generated.c014;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.io.FileNameUtil;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FileNameUtilBenchmark {

    // Since FileNameUtil methods are static and operate on input strings,
    // we do not need instance fields for state, adhering to the anti-pattern
    // against per-invocation fixtures for stateless operations.

    @Benchmark
    public void normalize(Blackhole bh) {
        String input = "a/b/c/d.txt";
        bh.consume(FileNameUtil.normalize(input));
    }

    @Benchmark
    public void normalizeNoEndSeparator(Blackhole bh) {
        String input = "a/b/c/d.txt";
        bh.consume(FileNameUtil.normalizeNoEndSeparator(input, false));
    }

    @Benchmark
    public void concat(Blackhole bh) {
        String base = "/foo/";
        String file = "bar.txt";
        bh.consume(FileNameUtil.concat(base, file));
    }

    @Benchmark
    public void concatUnix(Blackhole bh) {
        String base = "/foo";
        String file = "bar.txt";
        bh.consume(FileNameUtil.concat(base, file, true));
    }

    @Benchmark
    public void separatorsToUnix(Blackhole bh) {
        String input = "C:\\path\\file.txt";
        bh.consume(FileNameUtil.separatorsToUnix(input));
    }

    @Benchmark
    public void separatorsToWindows(Blackhole bh) {
        String input = "/path/file.txt";
        bh.consume(FileNameUtil.separatorsToWindows(input));
    }

    @Benchmark
    public void getPrefixLength(Blackhole bh) {
        String input = "C:\\a\\b\\c.txt";
        bh.consume(FileNameUtil.getPrefixLength(input));
    }

    @Benchmark
    public void getPrefix(Blackhole bh) {
        String input = "C:\\a\\b\\c.txt";
        bh.consume(FileNameUtil.getPrefix(input));
    }

    @Benchmark
    public void getPath(Blackhole bh) {
        String input = "C:\\a\\b\\c.txt";
        bh.consume(FileNameUtil.getPath(input));
    }

    @Benchmark
    public void getFullPath(Blackhole bh) {
        String input = "C:\\a\\b\\c.txt";
        bh.consume(FileNameUtil.getFullPath(input));
    }

    @Benchmark
    public void getName(Blackhole bh) {
        String input = "a/b/c/d.txt";
        bh.consume(FileNameUtil.getName(input));
    }

    @Benchmark
    public void getBaseName(Blackhole bh) {
        String input = "a/b/c.txt";
        bh.consume(FileNameUtil.getBaseName(input));
    }

    @Benchmark
    public void getExtension(Blackhole bh) {
        String input = "a/b/c.jpg";
        bh.consume(FileNameUtil.getExtension(input));
    }

    @Benchmark
    public void removeExtension(Blackhole bh) {
        String input = "foo.txt";
        bh.consume(FileNameUtil.removeExtension(input));
    }

    @Benchmark
    public void split(Blackhole bh) {
        String input = "C:\\a\\b\\c.txt";
        bh.consume(FileNameUtil.split(input));
    }

    @Benchmark
    public void equals(Blackhole bh) {
        String f1 = "file.txt";
        String f2 = "file.txt";
        bh.consume(FileNameUtil.equals(f1, f2));

        String f3 = "File.txt";
        bh.consume(FileNameUtil.equalsOnSystem(f1, f3));
    }
}

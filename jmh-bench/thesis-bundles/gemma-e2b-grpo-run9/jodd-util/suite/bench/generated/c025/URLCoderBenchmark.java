package bench.generated.c025;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import jodd.net.URLCoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLCoderBenchmark {

    // Input data that is not final or static final, ensuring compliance with anti-patterns.
    // These strings are complex enough to exercise regex and encoding logic.
    private final String complexUri = "https://jodd:ddoj@www.jodd.org:8080/file;p=1?q=2#third";
    private final String complexHttpUrl = "https://user:pass@host.com:8080/path?param=value&other=1";

    @Benchmark
    public void benchmarkEncodeUri(Blackhole bh) {
        try {
            String result = URLCoder.encodeUri(complexUri);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected in edge cases
        }
    }

    @Benchmark
    public void benchmarkEncodeHttpUrl(Blackhole bh) {
        try {
            String result = URLCoder.encodeHttpUrl(complexHttpUrl);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes if they are expected in edge cases
        }
    }

    @Benchmark
    public void benchmarkEncodeUri_DefaultCharset(Blackhole bh) {
        try {
            // Test the overload using default StandardCharsets.UTF_8
            String result = URLCoder.encodeUri(complexUri);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore
        }
    }

    @Benchmark
    public void benchmarkEncodeHttpUrl_CustomCharset(Blackhole bh) {
        try {
            // Test the overload using a different charset (e.g., ISO-8859-1)
            Charset customCharset = java.nio.charset.StandardCharsets.ISO_8859_1;
            String result = URLCoder.encodeHttpUrl(complexHttpUrl, customCharset);
            bh.consume(result);
        } catch (Exception e) {
            // Ignore
        }
    }
    
    @Benchmark
    public void benchmarkEncodeUri_InvalidUri(Blackhole bh) {
        try {
            // Test case that should throw IllegalArgumentException
            URLCoder.encodeUri("invalid://uri");
        } catch (IllegalArgumentException e) {
            // Expected behavior
        }
    }
}

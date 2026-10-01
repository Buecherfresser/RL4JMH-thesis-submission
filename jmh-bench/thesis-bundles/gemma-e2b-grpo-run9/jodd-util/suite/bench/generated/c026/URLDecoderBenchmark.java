package bench.generated.c026;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import jodd.net.URLDecoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class URLDecoderBenchmark {

    // Since URLDecoder is static and stateless, no instance fields are required.

    @Benchmark
    public String benchmarkBasicDecode(Blackhole bh) {
        // Test a simple string with no special characters
        String input = "Hello World 123";
        try {
            String result = URLDecoder.decode(input);
            bh.consume(result);
            return null;
        } catch (Exception e) {
            // Catch exceptions if they occur during testing, though unlikely for this input
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public String benchmarkPercentDecode(Blackhole bh) {
        // Test a string requiring percent decoding (%41=A, %42=B, %20=space)
        // This tests the core logic of the private decode method.
        String input = "Test%41%42%20";
        try {
            String result = URLDecoder.decode(input);
            bh.consume(result);
            return null;
        } catch (IllegalArgumentException e) {
            // Ignore expected exceptions if the input is malformed, but consume the result path
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public String benchmarkPlusDecode(Blackhole bh) {
        // Test decoding '+' as space (decodePlus=true)
        String input = "a+b%20c";
        try {
            String result = URLDecoder.decode(input, StandardCharsets.UTF_8);
            bh.consume(result);
            return null;
        } catch (Exception e) {
            bh.consume(null);
            return null;
        }
    }

    @Benchmark
    public void benchmarkDecodeQuery(Blackhole bh) {
        // Test decodeQuery (which uses StandardCharsets.UTF_8 and decodePlus=true internally)
        String input = "query+param%20value";
        try {
            URLDecoder.decodeQuery(input);
            bh.consume(null);
        } catch (Exception e) {
            bh.consume(null);
        }
    }

    @Benchmark
    public void benchmarkInvalidPercentDecode(Blackhole bh) {
        // Test case designed to throw IllegalArgumentException (e.g., %GZ)
        String input = "Invalid%GZ";
        try {
            URLDecoder.decode(input);
            // If execution reaches here, the benchmark might be flawed or the exception is suppressed.
            // We consume null to satisfy the void requirement.
            bh.consume(null);
        } catch (IllegalArgumentException e) {
            // Expected behavior for invalid sequences
        } catch (Exception e) {
            // Catch other potential runtime exceptions
        }
    }
}

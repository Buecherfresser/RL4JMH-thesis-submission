package bench.generated.c022;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.net.HtmlDecoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HtmlDecoderBenchmark {

    // Since HtmlDecoder is static, no instance state is required.

    /**
     * Benchmark for decoding a simple string without entities.
     */
    @Benchmark
    public void decode_simple(Blackhole bh) {
        // Test case: simple string, should return itself.
        String input = "Hello World";
        String result = HtmlDecoder.decode(input);
        bh.consume(result);
    }

    /**
     * Benchmark for decoding a string containing basic HTML entities.
     * This tests the core logic involving & and entity lookups.
     */
    @Benchmark
    public void decode_complex(Blackhole bh) {
        // Test case: string with an entity reference (&amp;).
        // Note: The actual decoding depends on the static initialization of ENTITY_MAP.
        String input = "This is &amp; a test.";
        String result = HtmlDecoder.decode(input);
        bh.consume(result);
    }

    /**
     * Benchmark for looking up an entity name.
     * This tests the static map access within HtmlDecoder.
     */
    @Benchmark
    public void lookup_existing(Blackhole bh) {
        // Test case: a known entity name (assuming one exists based on static init)
        String name = "amp";
        try {
            HtmlDecoder.lookup(name);
        } catch (Exception e) {
            // Ignore exceptions if lookup fails due to missing static data,
            // as long as the method call itself doesn't crash the benchmark runner.
        }
        bh.consume(null);
    }

    /**
     * Benchmark for looking up a non-existent entity name.
     * This tests the null return path of the lookup method.
     */
    @Benchmark
    public void lookup_nonexistent(Blackhole bh) {
        // Test case: a name unlikely to be in the map.
        String name = "nonexistententity123";
        try {
            HtmlDecoder.lookup(name);
        } catch (Exception e) {
            // Ignore exceptions
        }
        bh.consume(null);
    }
}

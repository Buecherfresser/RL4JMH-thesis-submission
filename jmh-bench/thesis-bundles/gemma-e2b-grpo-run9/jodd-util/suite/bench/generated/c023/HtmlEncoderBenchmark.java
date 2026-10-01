package bench.generated.c023;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import jodd.net.HtmlEncoder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HtmlEncoderBenchmark {

    // Since HtmlEncoder methods are static and stateless, no instance state is required.

    @Benchmark
    public void benchmarkText(Blackhole bh) {
        // Test the standard text encoding method
        // Creating a new String here is acceptable for stateless static methods
        String input = "This is a test string with < and > symbols and a non-breaking space \u00A0.";
        String result = HtmlEncoder.text(input);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAttributeDoubleQuoted(Blackhole bh) {
        // Test attribute encoding
        String input = "value with \"quotes\" and &amp; symbols.";
        String result = HtmlEncoder.attributeDoubleQuoted(input);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAttributeSingleQuoted(Blackhole bh) {
        // Test single-quoted attribute encoding
        String input = "value with 'quotes' and &amp; symbols.";
        String result = HtmlEncoder.attributeSingleQuoted(input);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkXml(Blackhole bh) {
        // Test XML encoding
        String input = "<tag>content &amp; more</tag>";
        String result = HtmlEncoder.xml(input);
        bh.consume(result);
    }
}

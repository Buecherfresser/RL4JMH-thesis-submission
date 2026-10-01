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

    private String inputString;
    private String doubleQuotedResult;
    private String singleQuotedResult;
    private String textResult;
    private String xmlResult;

    @Setup
    public void setup() {
        // Create a large, representative input string for testing.
        // This string includes characters that are likely to be encoded (like <, >, &, space).
        String baseText = "This is a test string with <tags>, &amp; symbols, and a non-breaking space\u00A0.";
        this.inputString = baseText;

        // Pre-calculate results for attribute encoding tests (read-only operation)
        this.doubleQuotedResult = HtmlEncoder.attributeDoubleQuoted(inputString);
        this.singleQuotedResult = HtmlEncoder.attributeSingleQuoted(inputString);
        this.textResult = HtmlEncoder.text(inputString);
        this.xmlResult = HtmlEncoder.xml(inputString);
    }

    @Benchmark
    public void benchmarkAttributeDoubleQuoted(Blackhole bh) {
        String result = HtmlEncoder.attributeDoubleQuoted(inputString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkAttributeSingleQuoted(Blackhole bh) {
        String result = HtmlEncoder.attributeSingleQuoted(inputString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkTextEncoding(Blackhole bh) {
        String result = HtmlEncoder.text(inputString);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkXmlEncoding(Blackhole bh) {
        String result = HtmlEncoder.xml(inputString);
        bh.consume(result);
    }
}

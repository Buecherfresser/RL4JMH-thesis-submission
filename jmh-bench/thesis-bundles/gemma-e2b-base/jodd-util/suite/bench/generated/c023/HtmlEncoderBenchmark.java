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

    private String textInput;
    private String attributeDoubleQuotedInput;
    private String attributeSingleQuotedInput;
    private String xmlInput;

    @Setup
    public void setup() {
        // Prepare representative input data.
        // Using a moderately long string to ensure the encoding logic is exercised.
        String baseText = "This is a test string containing < and > symbols, and a non-breaking space \u00A0.";
        this.textInput = baseText;

        // Inputs for attribute encoding (testing quotes and ampersands)
        String attrDouble = "value with \"quotes\" and &amp;";
        this.attributeDoubleQuotedInput = attrDouble;

        String attrSingle = "value with 'quotes' and &amp;";
        this.attributeSingleQuotedInput = attrSingle;

        // Input for XML encoding
        String xmlData = "<tag>content & more</tag>";
        this.xmlInput = xmlData;
    }

    @Benchmark
    public void testTextEncoding(Blackhole bh) {
        String result = HtmlEncoder.text(textInput);
        bh.consume(result);
    }

    @Benchmark
    public void testAttributeDoubleQuoted(Blackhole bh) {
        String result = HtmlEncoder.attributeDoubleQuoted(attributeDoubleQuotedInput);
        bh.consume(result);
    }

    @Benchmark
    public void testAttributeSingleQuoted(Blackhole bh) {
        String result = HtmlEncoder.attributeSingleQuoted(attributeSingleQuotedInput);
        bh.consume(result);
    }

    @Benchmark
    public void testXmlEncoding(Blackhole bh) {
        String result = HtmlEncoder.xml(xmlInput);
        bh.consume(result);
    }
}

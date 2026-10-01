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

    private String textSample;
    private String attrDoubleSample;
    private String attrSingleSample;
    private String xmlSample;

    @Setup(Level.Trial)
    public void setUp() {
        // Sample string containing characters that trigger each encoding path
        String base = "Hello & < > \" ' \u00A0 World";
        this.textSample = base;
        this.attrDoubleSample = base; // contains &, \" and non‑breaking space
        this.attrSingleSample = base; // contains &, ' and non‑breaking space
        this.xmlSample = base; // contains &, <, >, \" and '
    }

    @Benchmark
    public String benchmarkText() {
        return HtmlEncoder.text(textSample);
    }

    @Benchmark
    public String benchmarkAttributeDoubleQuoted() {
        return HtmlEncoder.attributeDoubleQuoted(attrDoubleSample);
    }

    @Benchmark
    public String benchmarkAttributeSingleQuoted() {
        return HtmlEncoder.attributeSingleQuoted(attrSingleSample);
    }

    @Benchmark
    public String benchmarkXml() {
        return HtmlEncoder.xml(xmlSample);
    }
}

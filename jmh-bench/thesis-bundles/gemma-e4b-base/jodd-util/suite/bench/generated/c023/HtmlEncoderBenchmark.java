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

    private CharSequence input;

    @Setup(Level.Trial)
    public void setup() {
        // Create a representative input string containing characters that require encoding
        // including &, <, >, ", ', and non-breaking space (\u00A0)
        String sample = "This is a test string with <tags>, &ampersands, \"quotes\", 'apostrophes', and a non-breaking space\u00A0.";
        this.input = sample;
    }

    @Benchmark
    public String benchmarkTextEncoding(Blackhole bh) {
        String result = HtmlEncoder.text(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkAttributeDoubleQuotedEncoding(Blackhole bh) {
        String result = HtmlEncoder.attributeDoubleQuoted(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkAttributeSingleQuotedEncoding(Blackhole bh) {
        String result = HtmlEncoder.attributeSingleQuoted(input);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkXmlEncoding(Blackhole bh) {
        String result = HtmlEncoder.xml(input);
        bh.consume(result);
        return result;
    }
}

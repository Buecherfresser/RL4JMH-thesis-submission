package bench.generated.c023;

import jodd.net.HtmlEncoder;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class HtmlEncoderBenchmark {

    private String simple;
    private String special;
    private String longSpecial;

    @Setup
    public void setup() {
        simple = "Hello World";
        special = "a&b<c>d\"e'f\u00A0g";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a&b<c>d\"e'f\u00A0g");
        }
        longSpecial = sb.toString();
    }

    @Benchmark
    public String text_simple() {
        return HtmlEncoder.text(simple);
    }

    @Benchmark
    public String text_special() {
        return HtmlEncoder.text(special);
    }

    @Benchmark
    public String text_long() {
        return HtmlEncoder.text(longSpecial);
    }

    @Benchmark
    public String xml_special() {
        return HtmlEncoder.xml(special);
    }

    @Benchmark
    public String attributeDoubleQuoted_special() {
        return HtmlEncoder.attributeDoubleQuoted(special);
    }

    @Benchmark
    public String attributeSingleQuoted_special() {
        return HtmlEncoder.attributeSingleQuoted(special);
    }
}

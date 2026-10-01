package bench.generated.c080;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;
import jodd.util.StringTemplateParser;

@State(org.openjdk.jmh.annotations.Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringTemplateParserBenchmark {

    private StringTemplateParser defaultParser;
    private StringTemplateParser strictParser;
    private StringTemplateParser escapedParser;
    private StringTemplateParser parseValuesParser;
    private StringTemplateParser missingKeyParser;
    private StringTemplateParser replaceMissingFalseParser;
    private StringTemplateParser customEscapeParser;

    private String templateSimple;
    private String templateStrict;
    private String templateEscaped;
    private String templateParseValues;
    private String templateMissing;
    private String templateCustomEscape;

    @Setup
    public void setup() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "World");
        map.put("inner", "name");
        map.put("missing", null); // not used directly

        defaultParser = StringTemplateParser.ofMap(map);

        strictParser = StringTemplateParser.ofMap(map).setStrictFormat();

        escapedParser = StringTemplateParser.ofMap(map).setResolveEscapes(false);

        parseValuesParser = StringTemplateParser.ofMap(map).setParseValues(true);

        missingKeyParser = StringTemplateParser.ofMap(map).setMissingKeyReplacement("??");

        replaceMissingFalseParser = StringTemplateParser.ofMap(map).setReplaceMissingKey(false);

        customEscapeParser = StringTemplateParser.ofMap(map).setEscapeChar('#');

        templateSimple = "Hello $name, welcome!";
        templateStrict = "Hello ${name}, welcome!";
        templateEscaped = "Hello \\${name}, welcome!";
        templateParseValues = "Hello ${${inner}}!";
        templateMissing = "Hello ${missing}!";
        templateCustomEscape = "Hello #${name}!";
    }

    @Benchmark
    public String benchmarkDefaultParser(Blackhole bh) {
        String result = defaultParser.apply(templateSimple);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkStrictParser(Blackhole bh) {
        String result = strictParser.apply(templateStrict);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkEscapedParser(Blackhole bh) {
        String result = escapedParser.apply(templateEscaped);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkParseValuesParser(Blackhole bh) {
        String result = parseValuesParser.apply(templateParseValues);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkMissingKeyReplacementParser(Blackhole bh) {
        String result = missingKeyParser.apply(templateMissing);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkReplaceMissingFalseParser(Blackhole bh) {
        String result = replaceMissingFalseParser.apply(templateMissing);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public String benchmarkCustomEscapeParser(Blackhole bh) {
        String result = customEscapeParser.apply(templateCustomEscape);
        bh.consume(result);
        return result;
    }
}

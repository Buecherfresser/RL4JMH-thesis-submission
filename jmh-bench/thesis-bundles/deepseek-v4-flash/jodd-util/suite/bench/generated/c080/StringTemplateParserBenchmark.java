package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.*;
import jodd.util.StringTemplateParser;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringTemplateParserBenchmark {

    // Parser instances
    private StringTemplateParser defaultParser;
    private StringTemplateParser parseValuesParser;
    private StringTemplateParser noResolveEscapesParser;
    private StringTemplateParser missingKeyReplaceParser;
    private StringTemplateParser customDelimiterParser;
    private StringTemplateParser mapParser;
    private StringTemplateParser beanParser;

    // Input templates
    private String simpleStrict;
    private String simpleNonStrict;
    private String nestedValues;
    private String escaped;
    private String custom;
    private String missing;

    @Setup(Level.Trial)
    public void setup() {
        // Default resolver: returns "value" for any key
        defaultParser = new StringTemplateParser(name -> "value");

        // Parse values recursively
        parseValuesParser = new StringTemplateParser(name -> {
            if ("outer".equals(name)) return "${inner}";
            if ("inner".equals(name)) return "innerValue";
            return "value";
        });
        parseValuesParser.setParseValues(true);

        // Do not resolve escapes
        noResolveEscapesParser = new StringTemplateParser(name -> "value");
        noResolveEscapesParser.setResolveEscapes(false);

        // Replace missing keys with "N/A"
        missingKeyReplaceParser = new StringTemplateParser(name -> null);
        missingKeyReplaceParser.setMissingKeyReplacement("N/A");

        // Custom delimiters [[...]]
        customDelimiterParser = new StringTemplateParser(name -> "value");
        customDelimiterParser.setMacroStart("[[");
        customDelimiterParser.setMacroEnd("]]");
        customDelimiterParser.setMacroPrefix("[[");

        // Map-based parser
        Map<String, String> map = new HashMap<>();
        map.put("name", "World");
        map.put("greeting", "Hello");
        mapParser = StringTemplateParser.ofMap(map);

        // Bean-based parser
        Bean bean = new Bean();
        bean.setName("BeanWorld");
        bean.setGreeting("Hi");
        beanParser = StringTemplateParser.ofBean(bean);

        // Input templates
        simpleStrict = "Hello ${name}!";
        simpleNonStrict = "Hello $name!";
        nestedValues = "Value: ${outer}";
        escaped = "Escaped \\${name} and ${name}";
        custom = "Hello [[name]]!";
        missing = "Hello ${missing}!";
    }

    @Benchmark
    public String defaultStrict() {
        return defaultParser.apply(simpleStrict);
    }

    @Benchmark
    public String defaultNonStrict() {
        return defaultParser.apply(simpleNonStrict);
    }

    @Benchmark
    public String parseValues() {
        return parseValuesParser.apply(nestedValues);
    }

    @Benchmark
    public String noResolveEscapes() {
        return noResolveEscapesParser.apply(escaped);
    }

    @Benchmark
    public String missingKeyReplacement() {
        return missingKeyReplaceParser.apply(missing);
    }

    @Benchmark
    public String customDelimiters() {
        return customDelimiterParser.apply(custom);
    }

    @Benchmark
    public String mapResolver() {
        return mapParser.apply("${greeting} ${name}!");
    }

    @Benchmark
    public String beanResolver() {
        return beanParser.apply("${greeting} ${name}!");
    }

    // Simple bean for ofBean
    public static class Bean {
        private String name;
        private String greeting;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getGreeting() { return greeting; }
        public void setGreeting(String greeting) { this.greeting = greeting; }
    }
}

package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import jodd.util.StringTemplateParser;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringTemplateParserBenchmark {

    private Map<String, String> inputMap;
    private StringTemplateParser parser;
    private String template;

    @Setup
    public void setup() {
        // Base map for resolvers
        inputMap = new HashMap<>();
        inputMap.put("name", "Alice");
        inputMap.put("city", "New York");
        inputMap.put("nested_macro", "${inner_key}"); // For nested parsing test
        inputMap.put("inner_key", "Bob");

        // Initialize parser with map resolver
        Function<String, String> mapResolver = macroName -> {
            String value = inputMap.get(macroName);
            return value == null ? null : value;
        };
        parser = StringTemplateParser.ofMap(inputMap);
    }

    // --- Basic Parsing Tests ---

    @Benchmark
    public String basicStrictParsing() {
        // Template: ${name}
        template = "${name}";
        // Ensure parser is configured for strict mode (default)
        parser.setMacroPrefix(StringTemplateParser.DEFAULT_MACRO_PREFIX);
        parser.setMacroStart(StringTemplateParser.DEFAULT_MACRO_START);
        parser.setMacroEnd(StringTemplateParser.DEFAULT_MACRO_END);
        
        String result = parser.apply(template);
        return result;
    }

    @Benchmark
    public String basicNonStrictParsing() {
        // Template: $name
        template = "$name";
        // Configure for non-strict mode (by setting prefix but not start/end)
        parser.setMacroPrefix(StringTemplateParser.DEFAULT_MACRO_PREFIX);
        parser.setMacroStart("");
        parser.setMacroEnd("");

        String result = parser.apply(template);
        return result;
    }

    // --- Missing Key Handling Tests ---

    @Benchmark
    public String missingKeyReplacement() {
        // Template: ${missing_key}
        template = "${missing_key}";
        
        // Configure replacement
        parser.setReplaceMissingKey(true);
        parser.setMissingKeyReplacement("DEFAULT_VALUE");

        String result = parser.apply(template);
        return result;
    }

    @Benchmark
    public String missingKeyNoReplacement() {
        // Template: ${missing_key}
        template = "${missing_key}";
        
        // Configure no replacement
        parser.setReplaceMissingKey(false);
        parser.setMissingKeyReplacement(null); // Should be ignored

        String result = parser.apply(template);
        return result;
    }

    // --- Escaping Tests ---

    @Benchmark
    public String escapedMacroResolved() {
        // Template: \$name (should resolve to $name)
        template = "\\$name";
        
        // Ensure escape char is '\' (default) and resolution is enabled
        parser.setEscapeChar('\\');
        parser.setResolveEscapes(true);

        String result = parser.apply(template);
        return result;
    }

    @Benchmark
    public String escapedMacroNotResolved() {
        // Template: \$name (should remain literal)
        template = "\\$name";
        
        // Disable escape resolution
        parser.setResolveEscapes(false);

        String result = parser.apply(template);
        return result;
    }

    // --- Nested Parsing Tests ---

    @Benchmark
    public String nestedMacroParsing() {
        // Template: ${nested_macro} which resolves to ${inner_key}
        template = "${nested_macro}";
        
        // Enable value parsing
        parser.setParseValues(true);

        String result = parser.apply(template);
        return result;
    }

    @Benchmark
    public String nestedMacroParsingDisabled() {
        // Template: ${nested_macro} which resolves to ${inner_key}
        template = "${nested_macro}";
        
        // Disable value parsing
        parser.setParseValues(false);

        String result = parser.apply(template);
        return result;
    }

    // --- Custom Delimiter Tests ---

    @Benchmark
    public String customDelimiterParsing() {
        // Template: @@name@@
        template = "@@name@@";
        
        // Configure custom delimiters
        parser.setMacroPrefix("@@");
        parser.setMacroStart(""); // No start tag
        parser.setMacroEnd("@@");

        String result = parser.apply(template);
        return result;
    }
}

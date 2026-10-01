package bench.generated.c080;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import jodd.util.StringTemplateParser;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
public class StringTemplateParserBenchmark {

    // State field to hold the parser instance.
    private StringTemplateParser parser;

    // A simple, non-mutating macro resolver for testing purposes.
    private final Function<String, String> simpleResolver = name -> "resolved_" + name;

    @Setup
    public void setup() {
        // Initialize the parser with a simple resolver.
        this.parser = StringTemplateParser.of(simpleResolver);
    }

    @Benchmark
    public String apply_default(Blackhole bh) {
        // Test the default behavior (no specific configuration)
        String template = "${name} is ${value}";
        String result = parser.apply(template);
        bh.consume(result);
        return null;
    }

    @Benchmark
    public String apply_strict(Blackhole bh) {
        // Test a configuration that might enforce strict parsing
        String template = "${name} is ${value}";
        String result = parser.apply(template);
        bh.consume(result);
        return null;
    }

    @Benchmark
    public String apply_with_map(Blackhole bh) {
        // Test a scenario where the resolver relies on a map
        Map<String, String> map = new HashMap<>();
        map.put("name", "TestUser");
        map.put("value", "123");

        String template = "${name} is ${value}";
        String result = parser.apply(template);
        bh.consume(result);
        return null;
    }
}

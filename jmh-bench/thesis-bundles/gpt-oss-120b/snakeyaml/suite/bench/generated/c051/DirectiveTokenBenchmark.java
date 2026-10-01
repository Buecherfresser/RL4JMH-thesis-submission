package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DirectiveTokenBenchmark {

    private DirectiveToken<Integer> yamlDirectiveToken;
    private DirectiveToken<String> tagDirectiveToken;
    private Mark startMark;
    private Mark endMark;

    @Setup
    public void setup() {
        // Construct Mark with the required extra argument (buffer index)
        this.startMark = new Mark("benchmark", 0, 0, 0, new char[0], 0);
        this.endMark = new Mark("benchmark", 0, 0, 0, new char[0], 0);

        List<Integer> yamlValues = new ArrayList<>(2);
        yamlValues.add(1);
        yamlValues.add(2);
        this.yamlDirectiveToken = new DirectiveToken<>("YAML", yamlValues, startMark, endMark);

        List<String> tagValues = new ArrayList<>(2);
        tagValues.add("!foo");
        tagValues.add("!bar");
        this.tagDirectiveToken = new DirectiveToken<>("TAG", tagValues, startMark, endMark);
    }

    @Benchmark
    public DirectiveToken<Integer> benchmarkCreateYamlDirective() {
        List<Integer> values = new ArrayList<>(2);
        values.add(1);
        values.add(2);
        return new DirectiveToken<>("YAML", values, startMark, endMark);
    }

    @Benchmark
    public DirectiveToken<String> benchmarkCreateTagDirective() {
        List<String> values = new ArrayList<>(2);
        values.add("!foo");
        values.add("!bar");
        return new DirectiveToken<>("TAG", values, startMark, endMark);
    }

    @Benchmark
    public String benchmarkGetNameYaml() {
        return yamlDirectiveToken.getName();
    }

    @Benchmark
    public String benchmarkGetNameTag() {
        return tagDirectiveToken.getName();
    }

    @Benchmark
    public List<Integer> benchmarkGetValueYaml() {
        return yamlDirectiveToken.getValue();
    }

    @Benchmark
    public List<String> benchmarkGetValueTag() {
        return tagDirectiveToken.getValue();
    }

    @Benchmark
    public Token.ID benchmarkGetTokenIdYaml() {
        return yamlDirectiveToken.getTokenId();
    }

    @Benchmark
    public Token.ID benchmarkGetTokenIdTag() {
        return tagDirectiveToken.getTokenId();
    }

    @Benchmark
    public void benchmarkConsumeNameYaml(Blackhole bh) {
        bh.consume(yamlDirectiveToken.getName());
    }

    @Benchmark
    public void benchmarkConsumeValueTag(Blackhole bh) {
        bh.consume(tagDirectiveToken.getValue());
    }
}

package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.DirectiveToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DirectiveTokenBenchmark {

    private DirectiveToken<String> token;
    private final String directiveName = "tag";
    private final List<String> directiveValues = Arrays.asList("value1", "value2");
    private final Mark dummyStartMark = null;
    private final Mark dummyEndMark = null;

    @Setup
    public void setup() {
        // Build the token once in setup.
        // The constructor requires exactly two values.
        this.token = new DirectiveToken<>(directiveName, directiveValues, dummyStartMark, dummyEndMark);
    }

    @Benchmark
    public void getName(Blackhole bh) {
        String name = token.getName();
        bh.consume(name);
    }

    @Benchmark
    public void getValue(Blackhole bh) {
        List<String> values = token.getValue();
        bh.consume(values);
    }

    @Benchmark
    public void getTokenId(Blackhole bh) {
        org.yaml.snakeyaml.tokens.Token.ID id = token.getTokenId();
        bh.consume(id);
    }
}

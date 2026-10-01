package bench.generated.c051;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

    private DirectiveToken<String> directiveToken;

    @Setup(Level.Trial)
    public void setup() {
        // Setup inputs for DirectiveToken<String>
        String name = "myDirective";
        List<String> value = Arrays.asList("value1", "value2");
        
        // Instantiate Marks using a valid constructor (Mark(String, int, int, int, char[], int))
        Mark startMark = new Mark("start", 0, 0, 0, new char[0], 0); 
        Mark endMark = new Mark("end", 0, 0, 0, new char[0], 0);

        // Create the subject under test
        directiveToken = new DirectiveToken<>(name, value, startMark, endMark);
    }

    @Benchmark
    public void benchmarkGetName(Blackhole bh) {
        String name = directiveToken.getName();
        bh.consume(name);
    }

    @Benchmark
    public void benchmarkGetValue(Blackhole bh) {
        List<String> value = directiveToken.getValue();
        bh.consume(value);
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Token.ID is a nested type of Token
        Token.ID id = directiveToken.getTokenId();
        bh.consume(id);
    }
}

package bench.generated.c065;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.ValueToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ValueTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private ValueToken valueToken;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize dummy Marks using a valid constructor based on compilation errors
        // Mark(String, int, int, int, char[], int)
        startMark = new Mark("", 0, 0, 0, new char[0], 0);
        endMark = new Mark("", 0, 0, 0, new char[0], 0);

        // Initialize the subject instance
        valueToken = new ValueToken(startMark, endMark);
    }

    @Benchmark
    public void benchmarkValueTokenConstruction(Blackhole bh) {
        // Recreate the token in each invocation to measure construction cost
        ValueToken token = new ValueToken(startMark, endMark);
        bh.consume(token);
    }

    @Benchmark
    public void benchmarkValueTokenGetTokenId(Blackhole bh) {
        // Measure the cost of calling getTokenId() on an existing instance
        org.yaml.snakeyaml.tokens.Token.ID id = valueToken.getTokenId();
        bh.consume(id);
    }
}

package bench.generated.c044;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.tokens.Token;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AliasTokenBenchmark {

    private String aliasValue;
    private Mark startMark;
    private Mark endMark;
    private AliasToken prebuiltToken;

    @Setup
    public void setup() {
        aliasValue = "myAlias";
        char[] buffer = new char[] {'a', 'b', 'c'};
        startMark = new Mark("start", 0, 0, 0, buffer, 0);
        endMark = new Mark("end", 0, 0, 0, buffer, 0);
        prebuiltToken = new AliasToken(aliasValue, startMark, endMark);
    }

    @Benchmark
    public AliasToken benchmarkConstruction() {
        return new AliasToken(aliasValue, startMark, endMark);
    }

    @Benchmark
    public String benchmarkGetValue() {
        return prebuiltToken.getValue();
    }

    @Benchmark
    public Token.ID benchmarkGetTokenId() {
        return prebuiltToken.getTokenId();
    }

    // Example of a void benchmark consuming the result via Blackhole
    @Benchmark
    public void benchmarkConsumeWithBlackhole(Blackhole bh) {
        bh.consume(prebuiltToken.getValue());
    }
}

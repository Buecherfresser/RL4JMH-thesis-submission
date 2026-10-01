package bench.generated.c055;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.FlowMappingEndToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowMappingEndTokenBenchmark {

    private Mark startMark;
    private Mark endMark;
    private FlowMappingEndToken token;

    @Setup(Level.Trial)
    public void setup() {
        // Initialize Mark objects using a valid constructor since no-arg constructor is unavailable.
        // We use dummy values as the specific content of the Mark is irrelevant for this benchmark.
        String dummyString = "dummy";
        int dummyLine = 0;
        int dummyCol = 0;
        int dummyDepth = 0;
        char[] dummyChars = new char[0];
        int dummyEnd = 0;

        startMark = new Mark(dummyString, dummyLine, dummyCol, dummyDepth, dummyChars, dummyEnd);
        endMark = new Mark(dummyString, dummyLine, dummyCol, dummyDepth, dummyChars, dummyEnd);
        
        token = new FlowMappingEndToken(startMark, endMark);
    }

    @Benchmark
    public FlowMappingEndToken benchmarkConstruction(Blackhole bh) {
        // Test the constructor
        FlowMappingEndToken result = new FlowMappingEndToken(startMark, endMark);
        return result;
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Test the getter method. Token.ID is the nested type.
        Token.ID id = token.getTokenId();
        bh.consume(id);
    }
}

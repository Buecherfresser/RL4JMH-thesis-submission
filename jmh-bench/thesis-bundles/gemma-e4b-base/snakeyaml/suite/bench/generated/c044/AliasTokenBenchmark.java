package bench.generated.c044;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.AliasToken;
import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.tokens.Token;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class AliasTokenBenchmark {

    private AliasToken aliasToken;
    private String testValue;
    private Mark startMark;
    private Mark endMark;

    @Setup(Level.Trial)
    public void setup() {
        // Setup inputs
        testValue = "some_alias_reference";
        
        // Fix: Mark requires arguments based on compilation errors.
        // Using dummy values to satisfy the constructor signature:
        // Mark(java.lang.String,int,int,int,char[],int)
        String dummyString = "dummy";
        int dummy1 = 0;
        int dummy2 = 0;
        int dummy3 = 0;
        char[] dummyChars = new char[0];
        int dummy4 = 0;

        startMark = new Mark(dummyString, dummy1, dummy2, dummy3, dummyChars, dummy4); 
        endMark = new Mark(dummyString, dummy1, dummy2, dummy3, dummyChars, dummy4);

        // Build the subject under test
        aliasToken = new AliasToken(testValue, startMark, endMark);
    }

    @Benchmark
    public void testGetValue(Blackhole bh) {
        // Test the getter method
        String result = aliasToken.getValue();
        bh.consume(result);
    }

    @Benchmark
    public void testGetTokenId(Blackhole bh) {
        // Test the getTokenId method
        Token.ID result = aliasToken.getTokenId();
        bh.consume(result);
    }
}

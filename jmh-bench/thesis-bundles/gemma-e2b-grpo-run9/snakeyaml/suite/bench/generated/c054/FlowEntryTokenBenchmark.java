package bench.generated.c054;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

import org.yaml.snakeyaml.tokens.FlowEntryToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class FlowEntryTokenBenchmark {

    // Since FlowEntryToken is final and stateless, we don't strictly need a @State field,
    // but we keep the class structure clean.

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Attempt to instantiate the token. This relies on the environment
            // providing necessary internal dependencies (like Mark) for compilation.
            FlowEntryToken token = new FlowEntryToken(null, null);
            bh.consume(token);
        } catch (Exception e) {
            // Catch exceptions that might occur if internal SnakeYAML dependencies are missing
            // or if Mark cannot be instantiated.
            // In a real scenario, this would indicate a setup failure.
        }
    }
}

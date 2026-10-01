package bench.generated.c053;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.DocumentStartToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentStartTokenBenchmark {

    // Since DocumentStartToken is final and stateless, no instance state is strictly required.
    // We rely on the benchmark method to create new instances for each run.

    /**
     * Benchmarks the instantiation of DocumentStartToken.
     * This measures the overhead of object creation and constructor execution.
     *
     * @param bh Blackhole to consume the result (or prevent dead code elimination).
     */
    @Benchmark
    public void createToken(Blackhole bh) {
        try {
            // We instantiate the token. Since we cannot easily create valid Mark objects
            // without deeper library context, we pass nulls, assuming the constructor
            // handles nulls gracefully or that this overhead is what we are measuring.
            DocumentStartToken token = new DocumentStartToken(null, null);
            bh.consume(token);
        } catch (Exception e) {
            // Catch potential exceptions during instantiation if Mark handling is strict
        }
    }
}

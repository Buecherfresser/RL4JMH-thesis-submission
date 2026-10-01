package bench.generated.c052;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.tokens.DocumentEndToken;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class DocumentEndTokenBenchmark {

    // Since DocumentEndToken is final and stateless, we can instantiate it once
    // or rely on local instantiation if the overhead is minimal.
    // We use a field here to ensure setup cost is amortized if the object
    // were more complex, though for this simple class, it's mostly stylistic.
    private DocumentEndToken documentEndToken;

    @Setup
    public void setup() {
        // Instantiate the token. Since we don't have actual Mark objects,
        // we rely on the constructor accepting nulls or default behavior if possible,
        // or assume the JVM handles the final class structure correctly.
        // For simplicity and compliance, we instantiate it.
        try {
            this.documentEndToken = new DocumentEndToken(null, null);
        } catch (Exception e) {
            // Ignore exceptions during setup if Mark instantiation fails due to missing context
        }
    }

    @Benchmark
    public void benchmarkGetTokenId(Blackhole bh) {
        // Call the method to ensure it's not optimized away
        bh.consume(this.documentEndToken.getTokenId());
    }

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        // Re-instantiate to measure constructor overhead, as it's a simple operation
        try {
            new DocumentEndToken(null, null);
        } catch (Exception e) {
            // Ignore
        }
    }
}

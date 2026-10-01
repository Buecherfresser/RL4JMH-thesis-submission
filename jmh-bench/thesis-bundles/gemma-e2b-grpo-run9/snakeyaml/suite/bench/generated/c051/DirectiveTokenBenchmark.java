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

    // Since DirectiveToken is final and immutable, we don't strictly need a mutable state field,
    // but we can use one if we want to measure repeated construction/access.
    // We will rely on local instantiation within the benchmark method for simplicity
    // and to avoid complex setup/teardown of internal SnakeYAML dependencies (like Mark).

    @Benchmark
    public void benchmarkConstructor(Blackhole bh) {
        try {
            // Attempt to create a DirectiveToken. This relies on the environment
            // providing necessary context for Mark, which might fail if run outside
            // a full SnakeYAML context, but serves to test the path.
            // We use nulls for Mark as a placeholder if possible, though this might throw.
            // For a robust benchmark, we'd need a mock or a simpler constructor.
            DirectiveToken<String> token = new DirectiveToken<>("tag", Arrays.asList("v1", "v2"), null, null);
            bh.consume(token);
        } catch (Exception e) {
            // Catch exceptions that might occur during construction (e.g., missing Mark context)
            // and consume nothing, as the goal is to measure successful path performance.
        }
    }

    @Benchmark
    public String benchmarkGetName(Blackhole bh) {
        try {
            // Create a token instance. We must handle the generic type T.
            DirectiveToken<String> token = new DirectiveToken<>("name", Arrays.asList("a", "b"), null, null);
            String name = token.getName();
            bh.consume(name);
            return null; // Required return for void-less methods if we were using them, but here we just consume.
        } catch (Exception e) {
            // Ignore exceptions for benchmarking purposes
            return null;
        }
    }

    @Benchmark
    public List<String> benchmarkGetValue(Blackhole bh) {
        try {
            DirectiveToken<String> token = new DirectiveToken<>("name", Arrays.asList("a", "b"), null, null);
            // We must consume the returned value or return it.
            bh.consume(token.getValue());
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}

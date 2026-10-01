package bench.generated.c032;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.yaml.snakeyaml.parser.ParserException;
import org.yaml.snakeyaml.error.Mark;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ParserExceptionBenchmark {

    // Since ParserException is a simple exception wrapper, we don't need complex state setup.
    // We rely on the constructor accepting nulls for the complex Mark objects.

    @Benchmark
    public void createException(Blackhole bh) {
        try {
            // Attempt to instantiate the exception. We pass nulls for the complex Mark objects
            // as we are only measuring the overhead of the constructor call itself.
            ParserException exception = new ParserException(null, null, null, null);
            bh.consume(exception);
        } catch (Exception e) {
            // Catching exceptions during benchmark setup/execution is acceptable if they don't
            // interfere with the measurement, though ideally, the benchmark should not fail.
        }
    }
}

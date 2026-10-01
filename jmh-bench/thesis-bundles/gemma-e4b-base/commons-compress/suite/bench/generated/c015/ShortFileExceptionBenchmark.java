package bench.generated.c015;

import org.apache.commons.compress.archivers.dump.ShortFileException;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortFileExceptionBenchmark {

    // Since ShortFileException is a simple, parameterless exception, 
    // no complex setup is required.

    @Benchmark
    public void benchmarkInstantiation(Blackhole bh) {
        // Call the subject method (constructor) exactly once per invocation
        ShortFileException exception = new ShortFileException();
        
        // Consume the result to prevent dead code elimination
        bh.consume(exception);
    }
}

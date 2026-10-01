package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.ShortFileException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortFileExceptionBenchmark {

    // Since ShortFileException has a parameterless constructor, we don't need complex state setup.

    @Benchmark
    public void createException(Blackhole bh) {
        // Call the public constructor. We consume the result via Blackhole.
        try {
            new ShortFileException();
        } catch (Exception e) {
            // Catching exceptions during benchmark setup is generally bad practice,
            // but necessary if the SUT throws unchecked exceptions during instantiation.
        }
        bh.consume(null);
    }
}

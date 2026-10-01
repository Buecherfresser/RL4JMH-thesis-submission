package bench.generated.c015;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.dump.ShortFileException;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class ShortFileExceptionBenchmark {

    @Benchmark
    public ShortFileException construct() {
        return new ShortFileException();
    }
}

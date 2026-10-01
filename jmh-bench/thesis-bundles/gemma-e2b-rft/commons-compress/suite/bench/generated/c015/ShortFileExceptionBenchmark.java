package bench.generated.c015;

import org.apache.commons.compress.archivers.dump.ShortFileException;
import org.apache.commons.compress.archivers.dump.DumpArchiveException;
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

    @Benchmark
    public void createShortFileException(Blackhole bh) {
        ShortFileException exception = new ShortFileException();
        bh.consume(exception);
    }
}

package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.Level;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveStructSparseBenchmark {

    private long offset;
    private long numbytes;
    private TarArchiveStructSparse instance;
    private TarArchiveStructSparse equalInstance;
    private TarArchiveStructSparse differentInstance;

    @Setup(Level.Trial)
    public void setUp() {
        // Use non-constant values set at runtime
        offset = 12345L;
        numbytes = 6789L;
        instance = new TarArchiveStructSparse(offset, numbytes);
        equalInstance = new TarArchiveStructSparse(offset, numbytes);
        differentInstance = new TarArchiveStructSparse(offset + 1, numbytes + 1);
    }

    @Benchmark
    public TarArchiveStructSparse construct() {
        return new TarArchiveStructSparse(offset, numbytes);
    }

    @Benchmark
    public long getOffset() {
        return instance.getOffset();
    }

    @Benchmark
    public long getNumbytes() {
        return instance.getNumbytes();
    }

    @Benchmark
    public boolean equalsSame() {
        return instance.equals(equalInstance);
    }

    @Benchmark
    public boolean equalsDifferent() {
        return instance.equals(differentInstance);
    }

    @Benchmark
    public int hashCodeBenchmark() {
        return instance.hashCode();
    }

    @Benchmark
    public String toStringBenchmark() {
        return instance.toString();
    }
}

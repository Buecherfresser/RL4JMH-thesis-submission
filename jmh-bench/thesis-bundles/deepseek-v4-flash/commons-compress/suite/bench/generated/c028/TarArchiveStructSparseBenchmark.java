package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveStructSparseBenchmark {

    private long offset;
    private long numbytes;
    private TarArchiveStructSparse instance1;
    private TarArchiveStructSparse instance2;
    private TarArchiveStructSparse instance3;

    @Setup(Level.Trial)
    public void setup() {
        offset = 123456789L;
        numbytes = 987654321L;
        instance1 = new TarArchiveStructSparse(offset, numbytes);
        instance2 = new TarArchiveStructSparse(offset, numbytes);
        instance3 = new TarArchiveStructSparse(offset + 1, numbytes);
    }

    @Benchmark
    public TarArchiveStructSparse constructor() {
        return new TarArchiveStructSparse(offset, numbytes);
    }

    @Benchmark
    public long getOffset() {
        return instance1.getOffset();
    }

    @Benchmark
    public long getNumbytes() {
        return instance1.getNumbytes();
    }

    @Benchmark
    public boolean equalsSame() {
        return instance1.equals(instance2);
    }

    @Benchmark
    public boolean equalsDifferent() {
        return instance1.equals(instance3);
    }

    @Benchmark
    public int hashCode() {
        return instance1.hashCode();
    }

    @Benchmark
    public String toString() {
        return instance1.toString();
    }
}

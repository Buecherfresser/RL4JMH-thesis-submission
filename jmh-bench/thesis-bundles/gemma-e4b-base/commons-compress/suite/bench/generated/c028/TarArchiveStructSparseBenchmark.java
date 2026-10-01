package bench.generated.c028;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.archivers.tar.TarArchiveStructSparse;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 3, time = 1)
public class TarArchiveStructSparseBenchmark {

    private TarArchiveStructSparse sparse1;
    private TarArchiveStructSparse sparse2;
    private TarArchiveStructSparse sparseEqual;
    private TarArchiveStructSparse sparseDifferent;

    @Setup(Level.Trial)
    public void setup() {
        // Instance 1: Standard case
        sparse1 = new TarArchiveStructSparse(1024L, 512L);

        // Instance 2: Different case
        sparse2 = new TarArchiveStructSparse(2048L, 100L);

        // Instance 3: Equal to sparse1
        sparseEqual = new TarArchiveStructSparse(1024L, 512L);

        // Instance 4: Different from sparse1
        sparseDifferent = new TarArchiveStructSparse(1025L, 512L);
    }

    @Benchmark
    public void benchmarkGetOffset(Blackhole bh) {
        long offset = sparse1.getOffset();
        bh.consume(offset);
    }

    @Benchmark
    public void benchmarkGetNumbytes(Blackhole bh) {
        long numbytes = sparse1.getNumbytes();
        bh.consume(numbytes);
    }

    @Benchmark
    public void benchmarkEquals_Self(Blackhole bh) {
        boolean result = sparse1.equals(sparse1);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEquals_Equal(Blackhole bh) {
        boolean result = sparse1.equals(sparseEqual);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEquals_Different(Blackhole bh) {
        boolean result = sparse1.equals(sparseDifferent);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkEquals_Null(Blackhole bh) {
        boolean result = sparse1.equals(null);
        bh.consume(result);
    }

    @Benchmark
    public void benchmarkHashCode(Blackhole bh) {
        int hash = sparse1.hashCode();
        bh.consume(hash);
    }

    @Benchmark
    public void benchmarkToString(Blackhole bh) {
        String s = sparse1.toString();
        bh.consume(s);
    }
}
